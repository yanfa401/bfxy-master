package com.bfxy.bfxyorder.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bfxy.bfxyorder.config.mq.producer.OrderlyProducer;
import com.bfxy.bfxyorder.constants.OrderStateEnum;
import com.bfxy.bfxyorder.entity.dto.OrderDto;
import com.bfxy.bfxyorder.entity.po.Order;
import com.bfxy.bfxyorder.mapper.OrderMapper;
import com.bfxy.bfxyorder.service.OrderServiceI;
import com.bfxy.common.dto.Result;
import com.bfxy.store.service.api.StoreServiceApi;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述：实现
 *
 * @author xielei
 * @date 2019/07/04
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderServiceI {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Reference(
            version = "0.0.1",
            application = "${dubbo.application.id}",
            interfaceClass = StoreServiceApi.class,
            check = false,
            timeout = 3000,//比较吉利
            retries = 0 //如果provider是写操作,没做幂等性0次,读操作最多重试3次
    )
    private StoreServiceApi storeServiceApi;
    
    /**
     * 创建订单
     *
     * @param orderDto
     * @return
     */
    @Transactional
    @Override
    public Result createOrder(OrderDto orderDto) {
        log.info("获取订单数据{}",orderDto);
        //查询库存数量
        Integer storeCount = null;
        try {
            storeCount = storeServiceApi.selectStoreCount(orderDto.getSupplierId(), orderDto.getGoodsId());
        } catch (Exception e) {
            log.error("rpc调用异常:"+e.getMessage());
            return Result.builder().success(false).msg("交易失败,RPC调用异常").data(null).build();
        }
        //库存如果存在,则可以继续交易,否则直接返回
        if (storeCount>0) {
            Order order = new Order();
            BeanUtils.copyProperties(orderDto, order);
            order.setOrderId(UUID.randomUUID().toString().substring(0,31));
            order.setPlatformId("1");
            order.setOrderType("1");
            order.setOrderStatus(OrderStateEnum.ORDER_CREATED.getValue());
            order.setRemark("");
            order.setCreateBy("admin");
            Date now = new Date();
            order.setCreateTime(now);
            order.setUpdateBy("admin");
            order.setUpdateTime(now);
            Integer updateResult = null;
            orderMapper.insert(order);
            try {
                //查询乐观锁版本号
                Integer lockVersion = storeServiceApi.selectVersion(orderDto.getSupplierId(), orderDto.getGoodsId());
                //更新库存,获取更新数量
                updateResult = storeServiceApi.updateStoreCountByVersion(lockVersion, orderDto.getSupplierId(), orderDto.getGoodsId(), "admin", new Date());
            } catch (Exception e) {
                log.error("rpc调用异常:"+e.getMessage());
                return Result.builder().success(false).msg("交易失败,RPC调用异常").data(null).build();
            }
            if (updateResult == 1) {
                //表示更新成功
                log.info("下单成功");
                return Result.builder().success(true).msg("交易成功").data(null).build();
            }
            else {
                //再次查询库存数量
                Integer currentCount = storeServiceApi.selectStoreCount(orderDto.getSupplierId(), orderDto.getGoodsId());
                if (currentCount <= 0) {
                    //表示库存不足了
                    log.info("库存不足");
                    return Result.builder().success(false).msg("库存不足,交易失败").data(null).build();
                }
                else {
                    //表示乐观锁更新失败
                    log.error("更新失败");
                    return Result.builder().success(false).msg("未获取乐观锁,交易失败").data(null).build();
                }
            }
            
        }
        else {
            return Result.builder().success(false).msg("库存不足,交易失败").data(null).build();
        }
    }
    
    public static final String PKG_TOPIC = "pkg_topic";
    
    public static final String PKG_TAG = "pkg_tag";
    
    @Autowired
    private OrderlyProducer orderlyProducer;
    
    @Override
    public void sendOrderlyMessage4Pkg(String userId, String orderId) {
        List<Message> messageList = new ArrayList<>();
    
        Map<String, Object> params1 = new HashMap<>();
        params1.put("userId", userId);
        params1.put("orderId", orderId);
        params1.put("text", "创建包裹操作---1");
        
        String key1 = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
        Message message1 = new Message(PKG_TOPIC, PKG_TAG, key1, JSONObject.toJSONString(params1).getBytes());
        messageList.add(message1);
    
    
        Map<String, Object> params2 = new HashMap<>();
        params2.put("userId", userId);
        params2.put("orderId", orderId);
        params2.put("text", "发送物流通知操作---2");
    
        String key2 = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
        Message message2 = new Message(PKG_TOPIC, PKG_TAG, key2, JSONObject.toJSONString(params2).getBytes());
        messageList.add(message2);
    
    
        Map<String, Object> params3 = new HashMap<>();
        params3.put("userId", userId);
        params3.put("orderId", orderId);
        params3.put("text", "发送优惠券---3");
    
        String key3 = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
        Message message3 = new Message(PKG_TOPIC, PKG_TAG, key3, JSONObject.toJSONString(params3).getBytes());
        messageList.add(message3);
        
        //顺序消息投递是 应该按照 order表中 supplierId 和 messageQueueId 进行绑定,专门有张表维护才对
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Integer messageQueueNumber = Integer.parseInt(order.getSupplierId());
    
        //对应的顺序消息的生产者,把MessageList 发送出去
        orderlyProducer.sendOrderlyMsg(messageList, messageQueueNumber);
    
    }
}

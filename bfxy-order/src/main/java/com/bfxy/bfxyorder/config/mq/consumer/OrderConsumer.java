package com.bfxy.bfxyorder.config.mq.consumer;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.bfxy.bfxyorder.mapper.OrderMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述： 订单服务接受支付服务 pay-a 的回调消息
 * 通知订单服务是否支付成功
 *
 * @author xielei
 * @date 2019/07/09
 */
@Slf4j
@SpringBootConfiguration
public class OrderConsumer {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Value("${mq.nameserver}")
    private String NAME_SERVER;
    
    @Value("${mq.group.order.callback}")
    private String CONSUMER_GROUP_NAME;
    
    @Value("${mq.topic.pay}")
    private String topic;
    
    @PostConstruct
    public void consume() throws MQClientException {
        //指定group_name
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP_NAME);
        //指定name_svr
        consumer.setNamesrvAddr(NAME_SERVER);
        //设置最小消费线程数
        consumer.setConsumeThreadMin(10);
        //设置最大消费线程数
        consumer.setConsumeThreadMax(50);
        //从哪开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //订阅主题
        consumer.subscribe(topic,"*");
        //消费方法具体实现
        consumer.registerMessageListener(new MessageListenerConcurrently4Pay());
        consumer.start();
    }
    
    //消费方法具体实现
    class MessageListenerConcurrently4Pay implements MessageListenerConcurrently{
    
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            MessageExt me = list.get(0);
            log.info("order接到消息:{}"+ me);
            try {
                String topic = me.getTopic();
                String tags = me.getTags();
                //这里不需要去重,因为是设置状态,天然幂等性,执行多少次都一样
                String keys = me.getKeys();
                String body = new String(me.getBody(), RemotingHelper.DEFAULT_CHARSET);
                //解析body,转回Map
                Map<String, Object> paramsBody = JSONObject.parseObject(body, Map.class);
                //FastJsonConvertUtil.convertJSONToObject(body, Map.class);
                String userId = (String) paramsBody.get("userId"); // customer userId
                String orderId = (String) paramsBody.get("orderId");  // 	统一的订单
                String status = (String) paramsBody.get("status"); // status
                
                //修改订单表中订单状态
                int result = orderMapper.updateOrderStatus(orderId, status, "xielei", new Date());
                System.out.println("修改了" + result + "条数据");
    
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("MQ异常:{}",e.getMessage());
                if (me.getReconsumeTimes() >= 3) {
                    //如果处理多次操作还是失败, 记录失败日志（做补偿 回顾 人工处理）,标记为成功标志
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }else {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

}

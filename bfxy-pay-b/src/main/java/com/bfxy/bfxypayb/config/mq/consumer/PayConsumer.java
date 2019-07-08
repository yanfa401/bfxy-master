package com.bfxy.bfxypayb.config.mq.consumer;

import java.math.BigDecimal;
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

import com.alibaba.fastjson.JSONObject;
import com.bfxy.bfxypayb.entity.PlatformAccount;
import com.bfxy.bfxypayb.mapper.PlatformAccountMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述：支付的 Consumer
 *
 * @author xielei
 * @date 2019/07/07
 */

@Slf4j
@SpringBootConfiguration
public class PayConsumer {

    @Value("${mq.nameserver}")
    private String nameserver;
    
    @Value("${mq.group.tx}")
    private String consumerGroup;
    
    @Value("${mq.topic.payment}")
    private String topic;
    
    @Value("${mq.tag.payment}")
    private String tag;
    
    private DefaultMQPushConsumer consumer;
    
    @Autowired
    private PlatformAccountMapper platformAccountMapper;
    
    
    /**
     * 构造函数方式启动
     */
    @PostConstruct
    public void init(){
        try {
            this.consumer = new DefaultMQPushConsumer(consumerGroup);
            this.consumer.setConsumeThreadMin(10);
            this.consumer.setConsumeThreadMax(30);
            this.consumer.setNamesrvAddr(nameserver);
            this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            this.consumer.subscribe(topic, tag);
            this.consumer.registerMessageListener(new MessageListenerConcurrently4Pay());
            this.consumer.start();
        } catch (MQClientException e) {
            log.error("{}启动失败",PayConsumer.class);
        }
    }
    
    
    /*private PayConsumer(){
        try {
            this.consumer = new DefaultMQPushConsumer(consumerGroup);
            this.consumer.setConsumeThreadMin(10);
            this.consumer.setConsumeThreadMax(30);
            this.consumer.setNamesrvAddr(nameserver);
            this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            this.consumer.subscribe(topic, tag);
            this.consumer.registerMessageListener(new MessageListenerConcurrently4Pay());
            this.consumer.start();
        } catch (MQClientException e) {
            log.error("{}启动失败",PayConsumer.class);
        }
    }*/
    
    /**
     * 监听方法具体实现(业务代码)
     */
    class MessageListenerConcurrently4Pay implements MessageListenerConcurrently {
    
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            MessageExt me = list.get(0);
            log.debug("pay-b接到消息:{}"+ me);
            try {
                String topic = me.getTopic();
                String tags = me.getTags();
                //可用于支付去重,保证幂等性;数据库主键去重<去重表 keys>
                String keys = me.getKeys();
                String body = new String(me.getBody(), RemotingHelper.DEFAULT_CHARSET);
                //解析body,转回马屁
                Map<String, Object> paramsBody = JSONObject.parseObject(body, Map.class);
                        //FastJsonConvertUtil.convertJSONToObject(body, Map.class);
                String userId = (String) paramsBody.get("userId"); // customer userId
                String orderId = (String) paramsBody.get("orderId");  // 	统一的订单
                String accountId = (String) paramsBody.get("accountId"); //customer accountId
                BigDecimal money = (BigDecimal) paramsBody.get("money"); //	当前的收益款
    
                PlatformAccount pa = platformAccountMapper.selectByPrimaryKey("platform001");//	当前平台的一个账号
                BigDecimal oldBalance = pa.getCurrentBalance();
                Integer oldVersion = pa.getVersion();
                pa.setCurrentBalance(oldBalance.add(money));
                pa.setVersion(oldVersion+1);
                Date currentDateTime = new Date();
                pa.setDateTime(currentDateTime);
                pa.setUpdateTime(currentDateTime);
                int res = platformAccountMapper.updateByPrimaryKeySelective(pa);
                if (res == 1) {
                    //更新成功,记录日志,入库
                    log.info("交易成功");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                else {
                    //表示乐观锁更新失败,记录日志,重试
                    log.info("交易失败");
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("MQ异常:{}",e.getMessage());
                if (me.getReconsumeTimes() >= 3) {
                    //如果处理多次操作还是失败, 记录失败日志（做补偿 回顾 人工处理）,标记为成功标志
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }else {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                
            }
        }
    }
    
}

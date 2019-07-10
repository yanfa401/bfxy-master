package com.bfxy.bfxyorder.config.mq.producer;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 描述：顺序消息生产者
 *
 * @author xielei
 * @date 2019/07/10
 */

@Component
public class OrderlyProducer {
    
    private DefaultMQProducer producer;
    
    @Value("${mq.nameserver}")
    private String nameSvr;
    
    @Value("${mq.group.pkg.orderly}")
    private String groupName;
    
   
    @Value("${mq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimes;
    
    @PostConstruct
    public void init(){
        this.producer = new DefaultMQProducer(groupName);
        this.producer.setNamesrvAddr(nameSvr);
        this.producer.setSendMsgTimeout(3000);
        this.producer.setRetryTimesWhenSendFailed(retryTimes);
        try {
            this.producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 发送顺序消息
     * @param messageList  消息集合
     * @param messageQueueNumer  指定队列 id
     */
    public void sendOrderlyMsg(List<Message> messageList, Integer messageQueueNumer) {
        for (Message msg : messageList) {
            try {
                this.producer.send(msg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        int messageQueueNumer = (int) o;
                        return list.get(messageQueueNumer);
                    }
                }, messageQueueNumer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}

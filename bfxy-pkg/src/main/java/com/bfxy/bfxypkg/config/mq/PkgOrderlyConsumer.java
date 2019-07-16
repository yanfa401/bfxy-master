package com.bfxy.bfxypkg.config.mq;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
 * 描述： 顺序消费 Consumer
 *
 * @author xielei
 * @date 2019/07/10
 */

@Component
public class PkgOrderlyConsumer {
    
    private DefaultMQPushConsumer consumer;
    
    @Value("${mq.nameserver}")
    private String nameSvr;
    
    @Value("${mq.group.consumer.pkg.orderly}")
    private String groupName;
    
    @Value("${mq.topic.pkg}")
    private String pkgTopic;
    
    @PostConstruct
    public void init() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(groupName);
        this.consumer.setConsumeThreadMin(10);
        this.consumer.setConsumeThreadMax(30);
        this.consumer.setNamesrvAddr(nameSvr);
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        this.consumer.subscribe(pkgTopic, "*");
        this.consumer.registerMessageListener(new PkgOrderlyListener());
        this.consumer.start();
    }
    
    
    /**
     * 实际起作用的顺序监听方法
     */
    class PkgOrderlyListener implements MessageListenerOrderly {
    
        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext context) {
            context.setAutoCommit(true);
            for (MessageExt msg : list) {
                try {
                    String topic = msg.getTopic();
                    String keys = msg.getKeys();
                    String tags = msg.getTags();
                    int queueId = msg.getQueueId();
                    String msgBody = new String(msg.getBody(), "utf-8");
                    System.out.println("收到消息：" + "  topic :" + topic + "  ,tags : " + tags + "keys :" + keys + ", msg : " + msgBody + "queueId: "+queueId);
    
                    Map<String, Object> body = JSONObject.parseObject(msgBody, Map.class);
                    String orderId = (String) body.get("orderId");
                    String userId = (String) body.get("userId");
                    String text = (String) body.get("text");
    
                    //	模拟实际的业务耗时操作
                    //	PS: 创建包裹信息  、对物流的服务调用（异步调用）
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
    
                    System.out.println("执行了业务操作:"+text);
                
                } catch (Exception e) {
                    e.printStackTrace();
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}

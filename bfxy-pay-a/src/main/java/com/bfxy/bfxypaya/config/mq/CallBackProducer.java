package com.bfxy.bfxypaya.config.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 描述：同步消息生产者
 *
 * @author xielei
 * @date 2019/07/09
 */

@SpringBootConfiguration
public class CallBackProducer {
    
    @Value("${mq.nameserver}")
    private String NAME_SERVER;
    
    @Value("${mq.group.order.callback}")
    private String PRODUCER_GROUP_NAME;
    
    @Value("${mq.producer.retryTimesWhenSendFailed}")
    private int RETRY_TIMES_WHEN_SEND_FAILED;
    
    @Bean(name = "callBackOrderProducer")
    public DefaultMQProducer getCallBackProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP_NAME);
        producer.setNamesrvAddr(NAME_SERVER);
        producer.setRetryTimesWhenSendAsyncFailed(RETRY_TIMES_WHEN_SEND_FAILED);
        producer.start();
        return producer;
    }

}

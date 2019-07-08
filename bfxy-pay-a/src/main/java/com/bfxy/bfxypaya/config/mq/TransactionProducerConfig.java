package com.bfxy.bfxypaya.config.mq;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：事务消息Producer
 *
 * @author xielei
 * @date 2019/07/07
 */

@SpringBootConfiguration
@AutoConfigureAfter({TransactionListenerImpl.class})
public class TransactionProducerConfig {
    
    private TransactionMQProducer producer;
    
    @Autowired
    private TransactionListenerImpl listenerImpl;
    
    @Value("${mq.nameserver}")
    private String NAME_SERVER;
    
    @Value("${mq.group.tx}")
    private String PRODUCER_GROUP_NAME;
    
    @Bean(name = "txnProducer")
    public TransactionMQProducer createProducer() throws MQClientException {
        producer = new TransactionMQProducer(PRODUCER_GROUP_NAME);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setNamesrvAddr(NAME_SERVER);
        //设置mq失败投递次数
        producer.setRetryTimesWhenSendFailed(3);
        //设置check线程池
        producer.setExecutorService(executorService);
        producer.setTransactionListener(listenerImpl);
        producer.start();
        return producer;
    }
}

package com.bfxy.bfxypaya.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.bfxy.bfxypaya.entity.CustomerAccount;
import com.bfxy.bfxypaya.mapper.CustomerAccountMapper;
import com.bfxy.bfxypaya.service.PayServiceI;
import com.bfxy.common.dto.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述：
 *
 * @author xielei
 * @date 2019/07/07
 */
@Slf4j
@Service
public class PayServiceImpl implements PayServiceI {
    
    @Autowired
    private CustomerAccountMapper accountMapper;
    
    @Value("${mq.topic.payment}")
    private String TX_TOPIC_PAYMENT;
    
    @Value("${mq.tag.payment}")
    private String TX_TAG_PAYMENT;
    
    //消息producer
    @Resource(name = "txnProducer")
    private TransactionMQProducer txnProducer;
    
    /**
     * 支付
     *
     * @param userId    用户id
     * @param orderId   订单id
     * @param accountId 账户id
     * @param money     支付金额
     * @return
     */
    @Transactional
    @Override
    public Result payment(String userId, String orderId, String accountId, double money) {
        //先去重,token验证
        
        /**
         * TODO:对大概率事件进行提前预判,对小概率事件做放过,但是要保障最终一致性即可
         * 业务角度:
         *         当前一个账户,只允许一个应用端登录(一个线程)
         *         技术角度:
         *         1 redis去重,分布式锁(如果获取不到锁,应该再给个独立的方法自动放行,避免阻塞在分布式锁上,并避免用户不能支付情况)
         *         2 虽然已经有了分布式锁,但是还要做数据库乐观锁去重,防止莫名其妙的问题,多一重保障
         */
        
        
        //修改前查询账户信息
        CustomerAccount old = accountMapper.selectByPrimaryKey(accountId);
        //修改前账户
        BigDecimal oldBalance = old.getCurrentBalance();
        //乐观锁版本号
        Integer version = old.getVersion();
        //计算支付后金额
        BigDecimal newBlance = oldBalance.subtract(new BigDecimal(money));
        // 如果计算过后金额大于0
        if (newBlance.doubleValue()>0) {
            //组装消息
            //执行本地事务
            String keys = "payment$" + accountId;
            Map<String, Object> params = new HashMap<>();
            params.put("userId",userId);
            params.put("orderId",orderId);
            params.put("accountId",accountId);
            params.put("money",money);
            params.put("newBlance",newBlance);
            params.put("version",version);
    
            Message message = new Message(TX_TOPIC_PAYMENT, TX_TAG_PAYMENT, keys, JSONObject.toJSONString(params).getBytes() );
            try {
                TransactionSendResult sendResult = txnProducer.sendMessageInTransaction(message, params);
                log.debug("sendResult:"+sendResult);
            } catch (MQClientException e) {
                log.error("MQ发送异常,记录到数据库某个表中,并提醒管理员进行人工干预;错误信息:{}", e.getMessage());
            }
        }
        else {
            return Result.builder().code("100").msg("金额不足,转账失败").success(false).build();
        }
        return Result.builder().code("200").msg("转账成功").success(true).build();
    }
    
}



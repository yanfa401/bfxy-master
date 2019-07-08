package com.bfxy.bfxypaya.config.mq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bfxy.bfxypaya.mapper.CustomerAccountMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述：事务消息具体实现
 *
 * @author xielei
 * @date 2019/07/07
 */
@Slf4j
@Component
public class TransactionListenerImpl implements TransactionListener {
    
    @Autowired
    private CustomerAccountMapper accountMapper;
    
    
    /**
     * 执行本地事务
     * @param message
     * @param params
     * @return
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object params) {
        HashMap<String,Object> map = (HashMap<String, Object>) params;
        String userId = (String) map.get("userId");
        String orderId = (String) map.get("orderId");
        String accountId = (String) map.get("accountId");
        double money = (double) map.get("money");
        BigDecimal newBlance = (BigDecimal) map.get("newBlance");
        int version = (int) map.get("version");
    
        try {
            //修改账户金额
            int res = accountMapper.updateBalance(accountId, newBlance, version, new Date());
            if (res == 1) {
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            else {
                log.error("支付失败:获取乐观锁失败");
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
            
        } catch (Exception e) {
            log.error("支付失败:{}",e.getMessage());
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    
        
    }
    
    /**
     * check本地事务
     * @param messageExt
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}

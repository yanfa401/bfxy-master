package com.bfxy.bfxypaya.service;

import com.bfxy.common.dto.Result;

public interface PayServiceI {
    
    /**
     * 支付
     * @param userId   用户id
     * @param orderId 订单id
     * @param accountId 账户id
     * @param money 支付金额
     * @return
     */
    Result payment(String userId, String orderId, String accountId, double money);
}

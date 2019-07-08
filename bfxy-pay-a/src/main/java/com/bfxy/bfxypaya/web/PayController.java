package com.bfxy.bfxypaya.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bfxy.bfxypaya.service.PayServiceI;
import com.bfxy.common.dto.Result;

/**
 * 描述：
 *
 * @author xielei
 * @date 2019/07/07
 */

@RestController
@RequestMapping("/pay")
public class PayController {
    
    @Autowired
    private PayServiceI payService;
    
    @PostMapping("/payment")
    public Result payment(String userId, String orderId, String accountId, double money){
        return payService.payment(userId, orderId, accountId, money);
    }
}

package com.bfxy.bfxyorder.service;

import com.bfxy.bfxyorder.entity.dto.OrderDto;
import com.bfxy.common.dto.Result;

public interface OrderServiceI {
    
    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    Result createOrder(OrderDto orderDto);
    
    void sendOrderlyMessage4Pkg(String userId, String orderId);
}

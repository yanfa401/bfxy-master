package com.bfxy.bfxyorder.service;

import com.bfxy.bfxyorder.entity.dto.OrderDto;

public interface OrderServiceI {
    
    /**
     * 创建订单
     * @param orderDto
     * @return
     */
    boolean createOrder(OrderDto orderDto);
}

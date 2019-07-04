package com.bfxy.bfxyorder.service.impl;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfxy.bfxyorder.constants.OrderStateEnum;
import com.bfxy.bfxyorder.entity.dto.OrderDto;
import com.bfxy.bfxyorder.entity.po.Order;
import com.bfxy.bfxyorder.mapper.OrderMapper;
import com.bfxy.bfxyorder.service.OrderServiceI;

/**
 * 描述：实现
 *
 * @author xielei
 * @date 2019/07/04
 */

@Service
public class OrderServiceImpl implements OrderServiceI {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 创建订单
     *
     * @param orderDto
     * @return
     */
    @Override
    public boolean createOrder(OrderDto orderDto) {
        boolean flag = false;
        try {
            Order order = new Order();
            BeanUtils.copyProperties(orderDto, order);
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderType("1");
            order.setOrderStatus(OrderStateEnum.ORDER_CREATED.getValue());
            order.setRemark("");
            order.setCreateBy("admin");
            Date now = new Date();
            order.setCreateTime(now);
            order.setUpdateBy("admin");
            order.setUpdateTime(now);
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return flag;
    }
}

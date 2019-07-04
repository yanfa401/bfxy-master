package com.bfxy.bfxyorder.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bfxy.bfxyorder.entity.dto.OrderDto;
import com.bfxy.bfxyorder.service.OrderServiceI;

/**
 * 描述：订单控制层
 *
 * @author xielei
 * @date 2019/07/03
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderServiceI orderService;

    @PostMapping("/createOrder")
    public String createOrder(@RequestBody OrderDto orderDto){
        return orderService.createOrder(orderDto) ? "下单成功" : "下单失败";
    }
}

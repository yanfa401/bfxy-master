package com.bfxy.bfxyorder.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bfxy.bfxyorder.entity.dto.OrderDto;
import com.bfxy.bfxyorder.service.OrderServiceI;
import com.bfxy.common.dto.Result;

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

    @PostMapping("/buyOne")
    public Result buyOne(@RequestBody OrderDto orderDto){
        return orderService.createOrder(orderDto);
    }
}

package com.bfxy.bfxyorder.constants;

/**
 * 订单类型 枚举类
 */
public enum OrderStateEnum {
    
    //订单已创建
    ORDER_CREATED("1"),
    
    //订单已支付
    ORDER_PAYED("2"),
    
    //订单支付失败
    ORDER_FAILED("3");

    private String state;
    
    private OrderStateEnum(String state){
        this.state = state;
    }
    
    public String getValue(){
        return state;
    }
    
}

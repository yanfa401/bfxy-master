package com.bfxy.bfxyorder.entity.dto;

import lombok.Data;
import lombok.ToString;

/**
 * 描述：order 数据传输对象
 *
 * @author xielei
 * @date 2019/07/04
 */

@Data
@ToString
public class OrderDto {
    
    public String cityId;
    
    public String userId;
    
    public String supplierId;
    
    public String goodsId;
}

package com.bfxy.bfxyorder.entity.po;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 描述：
 *
 * @author xielei
 * @date 2019/07/01
 */
@Data
@Table(name = "tb_order")
@ToString
@NoArgsConstructor
public class Order implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * order_id
     */
    private String orderId;
    
    /**
     * order_type
     */
    private String orderType;
    
    /**
     * city_id
     */
    private String cityId;
    
    /**
     * platform_id
     */
    private String platformId;
    
    /**
     * user_id
     */
    private String userId;
    
    /**
     * supplier_id
     */
    private String supplierId;
    
    /**
     * goods_id
     */
    private String goodsId;
    
    /**
     * order_status
     */
    private String orderStatus;
    
    /**
     * remark
     */
    private String remark;
    
    /**
     * create_by
     */
    private String createBy;
    
    /**
     * create_time
     */
    private Date createTime;
    
    /**
     * update_by
     */
    private String updateBy;
    
    /**
     * update_time
     */
    private Date updateTime;
}

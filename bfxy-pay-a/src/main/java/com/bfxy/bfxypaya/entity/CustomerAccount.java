package com.bfxy.bfxypaya.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class CustomerAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountId;

    private String accountNo;

    private Date dateTime;

    private BigDecimal currentBalance;

    private Integer version;

    private Date createTime;

    private Date updateTime;

}
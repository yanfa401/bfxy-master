package com.bfxy.bfxypaya.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BrokerMessageLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String messageId;

    private String message;

    private Integer tryCount;

    private String status;

    private Date nextRetry;

    private Date createTime;

    private Date updateTime;

}
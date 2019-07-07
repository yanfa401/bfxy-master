package com.bfxy.common.dto;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * 描述：
 *
 * @author xielei
 * @date 2019/07/05
 */

@Data
@Builder
@ToString
public class Result implements Serializable {
    
    private static final long serialVersionUID = 5978538155483436930L;
    
    private boolean success;
    
    private String code;
    
    private String msg;
    
    private Object data;
    
}

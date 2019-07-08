package com.bfxy.bfxypaya.mapper;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.bfxy.bfxypaya.entity.CustomerAccount;

@Repository
public interface CustomerAccountMapper {
    int deleteByPrimaryKey(String accountId);

    int insert(CustomerAccount record);

    int insertSelective(CustomerAccount record);

    CustomerAccount selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(CustomerAccount record);

    int updateByPrimaryKey(CustomerAccount record);

	int updateBalance(@Param("accountId") String accountId, @Param("newBalance") BigDecimal newBalance, @Param("version") int currentVersion, @Param("updateTime") Date currentTime);

}
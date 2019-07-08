package com.bfxy.bfxypayb.mapper;

import org.springframework.stereotype.Repository;

import com.bfxy.bfxypayb.entity.PlatformAccount;

@Repository
public interface PlatformAccountMapper {
    int deleteByPrimaryKey(String accountId);

    int insert(PlatformAccount record);

    int insertSelective(PlatformAccount record);

    PlatformAccount selectByPrimaryKey(String accountId);

    int updateByPrimaryKeySelective(PlatformAccount record);

    int updateByPrimaryKey(PlatformAccount record);
}
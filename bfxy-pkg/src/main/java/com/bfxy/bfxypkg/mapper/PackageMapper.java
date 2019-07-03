package com.bfxy.bfxypkg.mapper;

import com.bfxy.bfxypkg.entity.Package;

public interface PackageMapper {
    int deleteByPrimaryKey(String packageId);

    int insert(Package record);

    int insertSelective(Package record);

    Package selectByPrimaryKey(String packageId);

    int updateByPrimaryKeySelective(Package record);

    int updateByPrimaryKey(Package record);
}
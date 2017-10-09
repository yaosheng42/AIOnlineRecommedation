package com.seu.kse.dao;

import com.seu.kse.bean.UserTagKey;

public interface UserTagMapper {
    int deleteByPrimaryKey(UserTagKey key);

    int insert(UserTagKey record);

    int insertSelective(UserTagKey record);
}
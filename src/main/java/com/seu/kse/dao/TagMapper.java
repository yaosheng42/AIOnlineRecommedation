package com.seu.kse.dao;

import com.seu.kse.bean.Tag;

public interface TagMapper {
    int deleteByPrimaryKey(String tagname);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByTagName(String tagname);
}
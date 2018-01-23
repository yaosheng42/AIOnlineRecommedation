package com.seu.kse.dao;

import com.seu.kse.bean.Tag;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(String tagname);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByTagName(String tagname);

    List<Tag> selectAllTag();

    List<Tag> selectTodayTag(int time);
}
package com.seu.kse.dao;

import com.seu.kse.bean.Paper;

import java.util.List;

public interface PaperMapper {
    int deleteByPrimaryKey(String id);

    int insert(Paper record);

    int insertSelective(Paper record);

    Paper selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Paper record);

    int updateByPrimaryKeyWithBLOBs(Paper record);

    int updateByPrimaryKey(Paper record);

    List<Paper> selectPaperOrderByTime(int pageNum, int limit);

    List<Paper> selectAllPaper();

    Paper selectByTitle(String title);

    Integer countPaperId(String id);

    List<Paper> selectAllArxivPaper();
}
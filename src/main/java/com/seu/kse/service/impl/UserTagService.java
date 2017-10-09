package com.seu.kse.service.impl;

import com.seu.kse.bean.Tag;
import com.seu.kse.bean.UserTagKey;
import com.seu.kse.dao.TagMapper;
import com.seu.kse.dao.UserTagMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by yaosheng on 2017/6/2.
 */
@Service
public class UserTagService {
    @Resource
    private UserTagMapper userTagDao;

    @Resource
    private TagMapper tagDao;
    public int insertUserAndTag(UserTagKey userAndTag){
        String tagName = userAndTag.getTagname();
        Tag tag = tagDao.selectByTagName(tagName);
        int line = 0;
        if(tag == null){
            //插入
            tag = new Tag();
            tag.setTagname(userAndTag.getTagname());
            line = tagDao.insert(tag);
        }
        //插入 user_tag表
        line = line +userTagDao.insert(userAndTag);
        return line;
    }
}

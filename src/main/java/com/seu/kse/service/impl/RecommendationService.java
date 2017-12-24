package com.seu.kse.service.impl;

import com.seu.kse.bean.Paper;
import com.seu.kse.bean.User;
import com.seu.kse.bean.UserPaperBehavior;
import com.seu.kse.bean.UserPaperBehaviorKey;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.dao.UserMapper;
import com.seu.kse.dao.UserPaperBehaviorMapper;
import com.seu.kse.email.EmailSender;
import com.seu.kse.service.recommender.model.CB.CBKNNModel;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.Constant;
import com.seu.kse.util.LogUtils;
import com.seu.kse.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaosheng on 2017/5/31.
 */

@Service
public class RecommendationService {

    private static CBKNNModel cmodel;
    private final PaperMapper paperDao;
    private final UserMapper userDao;
    private final UserPaperBehaviorMapper userPaperBehaviorDao;

    @Autowired
    public RecommendationService(PaperMapper paperDao, UserMapper userDao, UserPaperBehaviorMapper userPaperBehaviorDao) {
        this.paperDao = paperDao;
        this.userDao = userDao;
        this.userPaperBehaviorDao = userPaperBehaviorDao;
        init();
    }

    public void init(){

            LogUtils.info("init start",RecommendationService.class);
            LogUtils.info("read all paper",RecommendationService.class);
            List<Paper> papers = paperDao.selectAllPaper();
            LogUtils.info("read new paper",RecommendationService.class);
            List<Paper> newPapers = paperDao.selectPaperOrderByTime(0,5,10);
            LogUtils.info("read user",RecommendationService.class);
            List<User> users = userDao.getAllUser();
            LogUtils.info("user actions",RecommendationService.class);
            Map<String,List<UserPaperBehavior>> userPaperBehaviors = new HashMap<String, List<UserPaperBehavior>>();
            for(User user : users){
                List<UserPaperBehavior> userPaperBehavior = userPaperBehaviorDao.selectByUserID(user.getId());
                userPaperBehaviors.put(user.getId(),userPaperBehavior);
            }
            //训练模型
            cmodel = new CBKNNModel(true,papers,1);
            cmodel.model(papers,userPaperBehaviors,users,newPapers);



            LogUtils.info("init complete",RecommendationService.class);

    }

    public void updateModel(){
        //判断什么时候需要更新模型
        //若需要更新模型，重新计算论文向量
        //生产文件
        LogUtils.info("model update init!",RecommendationService.class);

        List<Paper> papers = paperDao.selectAllPaper();
        List<Paper> newPapers = paperDao.selectPaperOrderByTime(0,5,10);
        List<User> users = userDao.getAllUser();
        Map<String,List<UserPaperBehavior>> userPaperBehaviors = new HashMap<String, List<UserPaperBehavior>>();
        for(User user : users){
            List<UserPaperBehavior> userPaperBehavior = userPaperBehaviorDao.selectByUserID(user.getId());
            userPaperBehaviors.put(user.getId(),userPaperBehavior);
        }


        cmodel = new CBKNNModel(true,papers,1);
        cmodel.model(papers,userPaperBehaviors,users,newPapers);
        LogUtils.info("model update complete !",RecommendationService.class);
    }





//    @Test
//    public void run(){
//        RecommendationService rs =new RecommendationService();
//        rs.updateModel();
//        rs.recommend(5);
//    }
}

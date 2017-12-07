package com.seu.kse.service.impl;

import com.seu.kse.bean.Paper;
import com.seu.kse.bean.User;
import com.seu.kse.bean.UserPaperBehavior;
import com.seu.kse.bean.UserPaperBehaviorKey;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.dao.UserMapper;
import com.seu.kse.dao.UserPaperBehaviorMapper;
import com.seu.kse.email.EmailSender;
import com.seu.kse.service.recommender.CB.CBKNNModel;
import com.seu.kse.service.recommender.CB.PaperDocument;
import com.seu.kse.util.Configuration;
import com.seu.kse.service.recommender.model.Paper2Vec;
import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.Constant;
import com.seu.kse.util.LogUtils;
import com.seu.kse.util.Utils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.net.URL;
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
    private ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    private PaperDocument paperDocument;
    private static CBKNNModel cmodel;
    private static Paper2Vec paper2Vec;
    private EmailSender emailSender;
    private

    Map<String, List<PaperSim>> res= new HashMap<String, List<PaperSim>>();

    private final PaperMapper paperDao;
    private final UserMapper userDao;
    private final UserPaperBehaviorMapper userPaperBehaviorDao;

    @Autowired
    public RecommendationService(PaperMapper paperDao, UserMapper userDao, UserPaperBehaviorMapper userPaperBehaviorDao) {
        this.paperDao = paperDao;
        this.userDao = userDao;
        this.userPaperBehaviorDao = userPaperBehaviorDao;
    }


    public static CBKNNModel getCBKKModel(){
        if(cmodel != null) return cmodel;
        synchronized (cmodel){
            if(cmodel != null) return  cmodel;
            cmodel = new CBKNNModel();
        }
        return cmodel;
    }

    public static Paper2Vec getPaper2Vec(){
        if(cmodel != null) return paper2Vec;
        synchronized (paper2Vec){
            if(cmodel != null) return  paper2Vec;
            cmodel = new CBKNNModel();
        }
        return paper2Vec;
    }


    public void init(){
        try {
            LogUtils.info("init start",RecommendationService.class);
            paper2Vec = new Paper2Vec();
            //paper2Vec.loadPaperVec();
            cmodel = new CBKNNModel(paper2Vec,true);

            emailSender = new EmailSender(Constant.sender,Constant.emailhost);
            emailSender.init();
            System.out.println("init complete!");
            LogUtils.info("init complete",RecommendationService.class);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage(),RecommendationService.class);
        }
    }



    public void updateModel(){
        //判断什么时候需要更新模型
        //若需要更新模型，重新计算论文向量
        //生产文件
        LogUtils.info("model update init!",RecommendationService.class);
        paperDocument = new PaperDocument();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url_root = classloader.getResource("/");
        String filepath = url_root.getPath();

        paperDocument.ToDocument(filepath+"/"+Configuration.sentencesFile);
        paper2Vec = new Paper2Vec();
        //训练模型
        paper2Vec.modelByWord2vce(); //服務器內存溢出，如何解決
        paper2Vec.calPaperVec();
        cmodel = new CBKNNModel(paper2Vec,true);


        res = cmodel.model();
        LogUtils.info("model update complete !",RecommendationService.class);
    }

    public void recommend(int k){
        LogUtils.info("recommend start !",RecommendationService.class);
        if(!Utils.testConnect()){
            return;
        }
        res = cmodel.model();
        Byte yes =1;
        Byte no = 0;
        for(Map.Entry<String,List<PaperSim>> e : res.entrySet()){
            String email = e.getKey();
            User user = userDao.selectByEmail(email);
            List<PaperSim> val = e.getValue();
            List<String> paperURLs = new ArrayList<String>();
            List<String> paperTitls = new ArrayList<String>();
            for(int i=0;i<k;i++){
                String paperID = val.get(i).getPid();
                Paper paper = paperDao.selectByPrimaryKey(paperID);
                String paperTitle = paper.getTitle();
                String paperURL = Constant.paperinfoURL + paperID;
                paperURLs.add(paperURL);
                paperTitls.add(paperTitle);
                //更新user_paper 表
                UserPaperBehavior upb = new UserPaperBehavior();
                upb.setUid(user.getId());
                upb.setPid(paperID);
                upb.setReaded(yes);
                upb.setInterest(0);
                upb.setAuthor(no);
                updateUserPaperB(upb);
            }
            recommendByEmail(email, paperURLs,paperTitls);
        }
        LogUtils.info("recommend end !",RecommendationService.class);
    }

    /**
     * 更新userPaper表
     * @param newRecord
     */
    public void updateUserPaperB(UserPaperBehavior newRecord){
        UserPaperBehaviorKey key = new UserPaperBehaviorKey(newRecord.getUid(),newRecord.getPid());
        UserPaperBehavior old = userPaperBehaviorDao.selectByPrimaryKey(key);
        if(old == null){
            userPaperBehaviorDao.insert(newRecord);
        }else{
            userPaperBehaviorDao.updateByPrimaryKey(newRecord);
        }
    }

    public void recommendByEmail(String email, List<String> paperURIs, List<String> paperTitls){
        String content = "下面是今日为您推荐的论文:"+"<br>";

        for(int i=0;i<paperURIs.size();i++){
            content = content + "<a href=\""+paperURIs.get(i)+"\">"+(i+1) +" : "+ paperTitls.get(i)+"</a>"+"<br>";
        }
        emailSender.send(email,content);
    }

//    @Test
//    public void run(){
//        RecommendationService rs =new RecommendationService();
//        rs.updateModel();
//        rs.recommend(5);
//    }
}

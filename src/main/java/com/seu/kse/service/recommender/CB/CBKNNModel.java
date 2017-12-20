package com.seu.kse.service.recommender.CB;

import com.seu.kse.bean.Paper;
import com.seu.kse.bean.User;
import com.seu.kse.bean.UserPaperBehavior;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.dao.UserMapper;
import com.seu.kse.dao.UserPaperBehaviorMapper;
import com.seu.kse.service.impl.RecommendationService;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.util.Configuration;
import com.seu.kse.service.recommender.ReccommendUtils;
import com.seu.kse.service.recommender.model.Paper2Vec;
import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.LogUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by yaosheng on 2017/5/26.
 */

public class CBKNNModel {
    private ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    private UserMapper userDao = (UserMapper) ac.getBean("userMapper");
    private UserPaperBehaviorMapper user_paper_Dao = (UserPaperBehaviorMapper) ac.getBean("userPaperBehaviorMapper");
    private PaperMapper paperDao = (PaperMapper) ac.getBean("paperMapper");

    private static Paper2Vec paper2Vec ;
    public CBKNNModel(){
        RecommenderCache.similarPaperList = new HashMap<String, List<PaperSim>>();
        paper2Vec= RecommendationService.getPaper2Vec();
    }

    public CBKNNModel(Paper2Vec paper2Vec,boolean open){
        RecommenderCache.similarPaperList = new HashMap<String, List<PaperSim>>();
        CBKNNModel.paper2Vec= paper2Vec;
        if(open){
            trainSimPaper();
        }else{
            loadPaperSimModel();
        }
        LogUtils.info("CBKNNModel 初始化完成",CBKNNModel.class);

    }

    private void loadPaperSimModel(){
        FileInputStream fin ;
        URL url = CBKNNModel.class.getClassLoader().getResource(Configuration.Paper_Model_Path);
        try {
            fin = new FileInputStream(url.getPath());
            ObjectInputStream oin = new ObjectInputStream(fin);
            LogUtils.info("读取 paper sims ",CBKNNModel.class);
            RecommenderCache.similarPaperList=(Map<String ,List<PaperSim>>) oin.readObject();
            LogUtils.info("paper sims 计算完成",CBKNNModel.class);
            fin.close();
        } catch (Exception e){

            LogUtils.error(e.getMessage(),CBKNNModel.class);
        }
    }


    private List<PaperSim> userColdStart(){
        //针对冷启动用户
        //选择当日的论文
        List<Paper> papers = paperDao.selectPaperOrderByTime(0,5,10);
        List<PaperSim> res = new ArrayList<PaperSim>();
        for(Paper paper : papers){
            PaperSim sim = new PaperSim(paper.getId(),1);
            res.add(sim);
        }
        return res;
    }

    public void thingColdStart(){
        //1. 获取每日新产生的论文
        //2. 计算新产生论文和用户
    }

    /**
     * 训练获得所有论文的相似论文，并持久化
     */
    private void trainSimPaper()  {
        //计算相似论文
        paper2Vec.calPaperVec();

        //URL url = CBKNNModel.class.getClassLoader().getResource(Configuration.Paper_Model_Path);

        for(Map.Entry<String, double[]> e1 : Paper2Vec.paperVecs.entrySet()){
            List<PaperSim> sims = new ArrayList<PaperSim>();
            String pid1 = e1.getKey();
            for(Map.Entry<String, double[]> e2 : Paper2Vec.paperVecs.entrySet()){
                if(e1==e2) continue;
                String pid2 = e2.getKey();
                double sim = ReccommendUtils.cosinSimilarity(e1.getValue(),e2.getValue());
                PaperSim paperSim = new PaperSim(pid2, sim);
                sims.add(paperSim);
            }
            Collections.sort(sims);
            List<PaperSim> subSims =  new ArrayList<PaperSim>(sims.subList(0,10));
            RecommenderCache.similarPaperList.put(pid1,subSims);
        }


        try {
            String root_path = CBKNNModel.class.getClassLoader().getResource("/").getPath();
            FileOutputStream fos = new FileOutputStream(root_path+"/"+Configuration.Paper_Model_Path);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(RecommenderCache.similarPaperList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void model(){
        //get the user's papers
        //1 . get all Users
        //2. get every user's papers
        //3. get the paper vec
        //4. cal knn paper for user
        List<User> users = userDao.getAllUser();

        if(Paper2Vec.paperVecs==null || Paper2Vec.paperVecs.size() == 0){
             paper2Vec.calPaperVec();
        }
        Map<String, List<PaperSim>> users_papers = new HashMap<String, List<PaperSim>>();
        for(User user : users){
            // get the k
            HashMap<String, double[]>  history = new HashMap<String, double[]>();
            HashMap<String, Integer> weight = new HashMap<String, Integer>();
            List<PaperSim> ranks = new ArrayList<PaperSim>();
            List<UserPaperBehavior> user_paper_behaves = user_paper_Dao.selectByUserID(user.getId());
            if(user_paper_behaves != null && user_paper_behaves.size() !=0){
                //calculate the k-nearest paper for user

                for(UserPaperBehavior up : user_paper_behaves){
                    int score = up.getInterest();
                    String pid = up.getPid();
                    double[] vec = Paper2Vec.paperVecs.get(pid);
                    if(vec!=null){
                        history.put(pid, vec);
                        weight.put(pid, score);
                    }
                }
                //计算其他论文和history论文中的相似度
                Iterator iter = Paper2Vec.paperVecs.entrySet().iterator();
                while(iter.hasNext()){
                    Map.Entry<String, double[]> entry = (Map.Entry<String, double[]>) iter.next();
                    String pid = entry.getKey();
                    boolean readed = false;
                    double[] vec = entry.getValue();
                    // cal every paper and history's paper sim
                    double everySim = 0.0;
                    int count = 0;
                    for(Map.Entry<String, double[]> his : history.entrySet()){
                        if(history.containsKey(pid)) {
                            readed = true;
                            continue;
                        }
                        double sim = ReccommendUtils.cosinSimilarity(his.getValue(),vec);
                        everySim = weight.get(his.getKey()) * sim;
                        count++;
                    }
                    if(!readed){
                        everySim/=count;
                        PaperSim paperSim = new PaperSim(pid, everySim);
                        ranks.add(paperSim);
                    }

                }
                try {
                    Collections.sort(ranks, new Comparator<PaperSim>() {
                        public int compare(PaperSim o1, PaperSim o2) {
                            return o1.compareTo(o2);
                        }
                    });
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
                users_papers.put(user.getMailbox(),ranks);
            }else{
                List<PaperSim> sims = userColdStart();
                users_papers.put(user.getMailbox(),sims);
            }
        }
        RecommenderCache.userRecommend = users_papers;

    }
}

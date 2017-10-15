package com.seu.kse.service.recommendation.CB;

import com.seu.kse.bean.Paper;
import com.seu.kse.bean.User;
import com.seu.kse.bean.UserPaperBehavior;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.dao.UserMapper;
import com.seu.kse.dao.UserPaperBehaviorMapper;
import com.seu.kse.service.impl.RecommendationService;
import com.seu.kse.service.recommendation.Configuration;
import com.seu.kse.service.recommendation.ReccommendUtils;
import com.seu.kse.service.recommendation.model.Paper2Vec;
import com.seu.kse.service.recommendation.model.PaperSim;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import java.io.*;
import java.util.*;

/**
 * Created by yaosheng on 2017/5/26.
 */

public class CBKNNModel {
    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    UserMapper userDao = (UserMapper) ac.getBean("userMapper");
    UserPaperBehaviorMapper user_paper_Dao = (UserPaperBehaviorMapper) ac.getBean("userPaperBehaviorMapper");
    PaperMapper paperDao = (PaperMapper) ac.getBean("paperMapper");
    static Map<String ,List<PaperSim>> paperSims ;
    static Paper2Vec paper2Vec ;
    public CBKNNModel(){
        paperSims = new HashMap<String, List<PaperSim>>();
        paper2Vec= RecommendationService.getPaper2Vec();
    }

    public CBKNNModel(Paper2Vec paper2Vec,boolean open){
        paperSims = new HashMap<String, List<PaperSim>>();
        this.paper2Vec= paper2Vec;
        trainSimPaper(open);
        System.out.println("CBKNNModel 初始化完成");
    }

    public List<PaperSim> userColdStart(){
        //针对冷启动用户
        //选择当日的论文
        List<Paper> papers = paperDao.selectPaperOrderByTime(0,5);
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
     * 获取论文的k个相似论文
     * @param pid 论文id
     * @param k  最相似的k个
     * @return
     */
    public List<Paper> getSimPaper(String pid, int k){
        List<Paper> res = new ArrayList<Paper>();
        List<PaperSim> sims = new ArrayList<PaperSim>();
        File file =null;

        file = new File(CBKNNModel.class.getClassLoader().getResource(Configuration.Paper_Model_Path).getPath());

        if(paperSims != null && paperSims.size()!=0){
            sims = paperSims.get(pid);
        }else if(file.exists()) {
            try {
                FileInputStream fin = new FileInputStream(CBKNNModel.class.getClassLoader().getResource(Configuration.Paper_Model_Path).getPath());
                ObjectInputStream oin = new ObjectInputStream(fin);
                paperSims=(Map<String ,List<PaperSim>>) oin.readObject();
                sims = paperSims.get(pid);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }else{
            //1. 加载 word2vec模型
            Map<String,double[]> papers = null;
            if(paper2Vec.paperVecs!=null && paper2Vec.paperVecs.size() != 0){
                papers = paper2Vec.paperVecs;
            }else{
                papers = paper2Vec.calPaperVec();
            }
            double[] paper = papers.get(pid);

            for(Map.Entry<String, double[]> e : papers.entrySet()){
                String id = e.getKey();
                if( pid == id ) continue;
                double[] vec = e.getValue();
                double similarity = ReccommendUtils.cosinSimilarity(paper, vec);
                PaperSim sim = new PaperSim(id,similarity);
                sims.add(sim);
            }
            Collections.sort(sims);
        }
        for(int i=0 ;i<k;i++){
            if(sims!=null) res.add(paperDao.selectByPrimaryKey(sims.get(i).getPid()));
        }
        return  res;
    }

    /**
     * 训练获得所有论文的相似论文，并持久化
     */
    public void trainSimPaper(boolean open)  {
        Map<String,double[]> papers = null;
        if(paper2Vec.paperVecs!=null && paper2Vec.paperVecs.size() != 0){
            papers = paper2Vec.paperVecs;
        }else{
            papers = paper2Vec.calPaperVec();
        }
        System.out.println("开始计算 paper sims ");
        for(Map.Entry<String, double[]> e1 : papers.entrySet()){
            List<PaperSim> sims = new ArrayList<PaperSim>();
            String pid1 = e1.getKey();
            for(Map.Entry<String, double[]> e2 : papers.entrySet()){
                if(e1==e2) continue;
                String pid2 = e2.getKey();

                double sim = ReccommendUtils.cosinSimilarity(e1.getValue(),e2.getValue());
                PaperSim paperSim = new PaperSim(pid2, sim);
                sims.add(paperSim);
            }
            Collections.sort(sims);
            paperSims.put(pid1,sims);
        }
        System.out.println("paper sims 计算完成");
        if(open){
            //持久化paperSim
            try {
                String root_path = CBKNNModel.class.getClassLoader().getResource("/").getPath();
                FileOutputStream fos = new FileOutputStream(root_path+"/"+Configuration.Paper_Model_Path);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(paperSims);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public Map<String, List<PaperSim>> model(){
        //get the user's papers
        //1 . get all Users
        //2. get every user's papers
        //3. get the paper vec
        //4. cal knn paper for user
        List<User> users = userDao.getAllUser();
        Map<String,double[]> doc2vecs = null;
        if(paper2Vec.paperVecs!=null && paper2Vec.paperVecs.size() != 0){
            doc2vecs = paper2Vec.paperVecs;
        }else{
            doc2vecs = paper2Vec.calPaperVec();
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
                    double[] vec = doc2vecs.get(pid);
                    history.put(pid, vec);
                    weight.put(pid, score);
                }
                //计算其他论文和history论文中的相似度
                Iterator iter = doc2vecs.entrySet().iterator();
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

        return  users_papers;
    }
}

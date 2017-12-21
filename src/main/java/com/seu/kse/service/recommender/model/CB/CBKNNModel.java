package com.seu.kse.service.recommender.model.CB;

import com.seu.kse.bean.Paper;
import com.seu.kse.bean.User;
import com.seu.kse.bean.UserPaperBehavior;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.service.recommender.feature.TFIDFProcessor;
import com.seu.kse.service.recommender.ReccommendUtils;
import com.seu.kse.service.recommender.feature.Word2vecProcessor;
import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.LogUtils;


import java.util.*;

/**
 * Created by yaosheng on 2017/5/26.
 */

public class CBKNNModel {



    public CBKNNModel(boolean open,List<Paper> papers,int type){
        RecommenderCache.similarPaperList = new HashMap<String, List<PaperSim>>();

        if(open){
            if(type == 0){
                Word2vecProcessor.process(papers);
            }else if(type == 1){
                TFIDFProcessor.process(papers);
            }

        }else{
            Word2vecProcessor.loadPaperSimModel();
        }
        LogUtils.info("CBKNNModel 初始化完成",CBKNNModel.class);

    }




    private List<PaperSim> userColdStart(List<Paper> newPapers){
        //针对冷启动用户
        //选择当日的论文

        List<PaperSim> res = new ArrayList<PaperSim>();
        for(Paper paper : newPapers){
            PaperSim sim = new PaperSim(paper.getId(),1);
            res.add(sim);
        }
        return res;
    }

    public void thingColdStart(){
        //1. 获取每日新产生的论文
        //2. 计算新产生论文和用户
    }



    public void model(List<Paper> papers,Map<String,List<UserPaperBehavior>> users_paper_behaves,List<User> users,List<Paper> newPapers){
        //get the user's papers
        //1 . get all Users
        //2. get every user's papers
        //3. get the paper vec
        //4. cal knn paper for user


        if(RecommenderCache.paperVecs==null || RecommenderCache.paperVecs.size() == 0){
             Word2vecProcessor.calPaperVec(papers);
        }
        Map<String, List<PaperSim>> users_papers = new HashMap<String, List<PaperSim>>();
        for(User user : users){
            // get the k
            HashMap<String, double[]>  history = new HashMap<String, double[]>();
            HashMap<String, Integer> weight = new HashMap<String, Integer>();
            List<PaperSim> ranks = new ArrayList<PaperSim>();
            List<UserPaperBehavior> user_paper_behaves = users_paper_behaves.get(user.getId());
            if(user_paper_behaves != null && user_paper_behaves.size() !=0){
                //calculate the k-nearest paper for user

                for(UserPaperBehavior up : user_paper_behaves){
                    int score = up.getInterest();
                    String pid = up.getPid();
                    double[] vec = RecommenderCache.paperVecs.get(pid);
                    if(vec!=null){
                        history.put(pid, vec);
                        weight.put(pid, score);
                    }
                }
                //计算其他论文和history论文中的相似度
                Iterator iter = RecommenderCache.paperVecs.entrySet().iterator();
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
                List<PaperSim> sims = userColdStart(newPapers);
                users_papers.put(user.getMailbox(),sims);
            }
        }
        RecommenderCache.userRecommend = users_papers;

    }




}

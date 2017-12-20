package com.seu.kse.service.impl;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.service.recommender.model.PaperSim;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yaosheng on 2017/5/24.
 */

@Service
public class PaperService {

    @Resource
    PaperMapper paperdao;

    /**
     * 根据时间排序获取limit篇论文
     * @param
     * @return
     */
    public List<Paper> selectPaperByTime(int start,int end){
        List<Paper> papers=paperdao.selectPaperOrderByTime(start,end,10);
        return papers;
    }
    /**
     * 根据论文id查找相关论文
     */
    public Paper searchPaper(String id){
        Paper paper= paperdao.selectByPrimaryKey(id);
        return paper;
    }
    /**
     * 根据论文判断是否存在该论文
     */

    public Paper getPaperByTitle(String title){
        Paper paper = paperdao.selectByTitle(title);
        return paper;
    }

    /**
     * 插入论文
     */

    public int insertPaper(Paper paper){
        try{
            //查询是否存在
            Paper temp = paperdao.selectByPrimaryKey(paper.getId());
            if(temp == null){
                int line =  paperdao.insert(paper);
                return line;
            }
            return 0;
        }catch (Exception except){
            return 0;
        }
    }

    /**
     * 更新论文
     */

    public int updatePaper(Paper paper){
        return paperdao.updateByPrimaryKey(paper);
    }

    public List<Paper> getAllPaper(){
        return paperdao.selectAllPaper();
    }

    /**
     * TODO 从 neo4j中获取参考文献
     * @param id
     * @return
     */
    public List<Paper> getRefPaper(String id) {
        List<Paper> papers = new ArrayList<Paper>();
        return papers;
    }

    /**
     * 获取所有arxiv的论文
     * @return
     */
    public List<Paper> getArxivPapers(int pageNum, int limit){
        List<Paper> papers =new ArrayList<Paper>();
        papers = paperdao.selectAllArxivPaper();
        return papers;
    }

    public List<Paper> getTodayArxivPapers(int pageNum, int limit){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date yesterday = calendar.getTime();
        String yesterday_str = new SimpleDateFormat("yyyyMMdd").format(yesterday);
        List<Paper> papers = paperdao.selectTodayArxiv(pageNum, limit, Integer.parseInt(yesterday_str),0);
        return papers;
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
        if(RecommenderCache.similarPaperList != null && RecommenderCache.similarPaperList.size()!=0){
            sims = RecommenderCache.similarPaperList.get(pid);
        }
        for(int i=0 ;i<k;i++){
            if(sims!=null) res.add(paperdao.selectByPrimaryKey(sims.get(i).getPid()));
        }
        return  res;
    }
}

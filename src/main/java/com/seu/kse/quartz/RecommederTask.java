package com.seu.kse.quartz;

import com.seu.kse.listener.RecommedationListener;
import com.seu.kse.service.impl.RecommendationService;
import com.seu.kse.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommederTask {

    private  final RecommendationService rs;

    @Autowired
    public RecommederTask(RecommendationService rs){
        this.rs = rs;
        rs.init();
    }
    public void execute(){
        LogUtils.info("开始推荐......",RecommedationListener.class);
        System.out.println("开始推荐………………………………");
        try {
            rs.recommend(5);
            System.out.println("结束推荐………………………………");
            LogUtils.info("结束推荐......",RecommedationListener.class);
        }
        catch(Exception ex){
            ex.printStackTrace();
            rs.recommend(5);
        }
    }
}

package com.seu.kse.service.recommender;

import com.seu.kse.service.recommender.model.PaperSim;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 存储推荐过程中需要的对象(皆为静态唯一的对象)
 */
public class RecommenderCache {
    //论文相似度矩阵
    public static INDArray paperSimilarityMatrix ;

    //论文PID 映射
    public static Map<String, Integer> paperIDMapRowID;

    //word2vec model
    public static Word2Vec word2vec;

    //row id 和 用户 ID映射表
    public static List<String> userRowMapID;

    //row id 和 论文 ID映射表
    public static List<String> paperRowMapID ;

    //用户推荐列表
    public static Map<String, List<PaperSim>> userRecommend ;

    //论文top10相似度列表
    public static Map<String, List<PaperSim>> similarPaperList;

    //paper vec
    public static Map<String, double[]> paperVecs = new HashMap<String, double[]>();

}

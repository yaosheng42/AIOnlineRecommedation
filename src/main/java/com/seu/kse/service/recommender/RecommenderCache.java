package com.seu.kse.service.recommender;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;


/**
 * 存储推荐过程中需要的对象(皆为静态唯一的对象)
 */
public class RecommenderCache {
    //论文相似度矩阵
    public static INDArray paperSimilarityMatrix ;

    //论文向量矩阵 row : paper's num ; col : dimension
    public static INDArray paperVectorMatrix;

    //word2vec model
    public static Word2Vec word2vec;


    //row id 和 用户 ID映射表
    public static List<String> userRowMapID;

    //row id 和 论文 ID映射表
    public static List<String> paperRowMapID;

}

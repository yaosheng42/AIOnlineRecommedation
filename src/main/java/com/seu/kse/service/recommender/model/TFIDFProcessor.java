package com.seu.kse.service.recommender.model;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.service.recommender.data.DocumentsGenerator;
import com.seu.kse.service.recommender.feature.TFIDFGenerator;
import com.seu.kse.util.Configuration;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.documentiterator.DocumentIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TFIDFProcessor {


    public void process(List<Paper> paperList){
        String path = TFIDFProcessor.class.getClassLoader().getResource("/").getPath();
        DocumentsGenerator documentsGenerator = new DocumentsGenerator(path+"/"+Configuration.documents);
        DocumentIterator documentIterator = documentsGenerator.generate(paperList);
        TFIDFGenerator tfidfGenerator = new TFIDFGenerator(documentIterator);
        TfidfVectorizer TFIDF = tfidfGenerator.train();
        String first_content = paperList.get(0).getTitle()+"."+ paperList.get(0).getPaperAbstract();
        RecommenderCache.paperRowMapID = new ArrayList<String>();

        //初始化
        //设置ID对应的向量
        INDArray res = TFIDF.transform(first_content);
        System.out.println(res);
        for(int i=1; i<paperList.size();i++){
            Paper cur_paper = paperList.get(i);
            RecommenderCache.paperRowMapID.add(cur_paper.getId());
            String cur_content = cur_paper.getTitle()+"."+cur_paper.getPaperAbstract();
            INDArray cur_vec = TFIDF.transform(cur_content);
            res = Nd4j.vstack(res,cur_vec);
        }
        RecommenderCache.paperVectorMatrix = res;
        //计算论文相似度矩阵
        RecommenderCache.paperSimilarityMatrix = res.muli(res);
        //计算论文top相似列表
        for(int i=0 ; i<RecommenderCache.paperSimilarityMatrix.rows();i++){
            String i_pid = RecommenderCache.paperRowMapID.get(i);
            List<PaperSim> paperSims = new ArrayList<PaperSim>();
            for(int j =0; j<RecommenderCache.paperSimilarityMatrix.columns();j++){
                String j_pid = RecommenderCache.paperRowMapID.get(j);
                double sim = RecommenderCache.paperSimilarityMatrix.getDouble(i,j);
                PaperSim paperSim = new PaperSim(j_pid,sim);
                paperSims.add(paperSim);
            }
            Collections.sort(paperSims);
            List<PaperSim> subSims =  new ArrayList<PaperSim>(paperSims.subList(0,20));
            RecommenderCache.similarPaperList.put(i_pid,subSims);
        }
    }

//    public static void main(String args[]){
//        TFIDFProcessor r = new TFIDFProcessor();
//        r.process();
//    }
}

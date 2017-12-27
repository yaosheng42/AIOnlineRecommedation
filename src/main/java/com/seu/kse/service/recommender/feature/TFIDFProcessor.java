package com.seu.kse.service.recommender.feature;

import com.seu.kse.bean.Paper;
import com.seu.kse.service.recommender.ReccommendUtils;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.service.recommender.data.DocumentsGenerator;
import com.seu.kse.service.recommender.data.PaperDocument;
import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.Configuration;
import com.seu.kse.util.LogUtils;
import org.deeplearning4j.bagofwords.vectorizer.TfidfVectorizer;
import org.deeplearning4j.text.documentiterator.DocumentIterator;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.stopwords.StopWords;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class TFIDFProcessor {

    private static TfidfVectorizer train(SentenceIterator sentenceIterator){
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        TfidfVectorizer TFIDF = new TfidfVectorizer.Builder()
                .setMinWordFrequency(3)
                .setStopWords(StopWords.getStopWords())
                .setIterator(sentenceIterator)
                .setTokenizerFactory(t)
                .build();
        TFIDF.fit();
        return TFIDF;
    }
    public static void process(List<Paper> paperList){
        String path = TFIDFProcessor.class.getClassLoader().getResource("/").getPath()+Configuration.documents;
        File file=new File(path);

        LogUtils.info(path,TFIDFProcessor.class);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            PaperDocument.ToDocument(path,paperList);
            SentenceIterator sentenceIterator = new BasicLineIterator(path);
            LogUtils.info("TF-IDF训练中。。。",TFIDFProcessor.class);
            TfidfVectorizer TFIDF =train(sentenceIterator);
            LogUtils.info("TF-IDF训练完成",TFIDFProcessor.class);
            RecommenderCache.paperRowMapID = new ArrayList<String>();
            RecommenderCache.paperVecs = new HashMap<String, double[]>();
            //构造集合 设置ID对应的向量
            for(int i=0;i<paperList.size();i++){
                Paper cur_paper = paperList.get(i);
                RecommenderCache.paperRowMapID.add(cur_paper.getId());
                String cur_content = cur_paper.getTitle()+"."+cur_paper.getPaperAbstract();
                INDArray cur_vec = TFIDF.transform(cur_content);
                double [] vectors = new double[cur_vec.length()];
                for(int k = 0 ; k<cur_vec.length(); k++){
                    vectors[k] = cur_vec.getDouble(k);
                }
                RecommenderCache.paperVecs.put(cur_paper.getId(),vectors);
            }
            LogUtils.info("向量计算完成",TFIDFProcessor.class);
            //计算论文相似度矩阵
            LogUtils.info("计算论文相似列表",TFIDFProcessor.class);
            //计算论文top相似列表
            ReccommendUtils.generateSimilarPaperList();

            LogUtils.info("TF-IDF模型训练完成",TFIDFProcessor.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




//    public static void main(String args[]){
//        TFIDFProcessor r = new TFIDFProcessor();
//        r.process();
//    }
}

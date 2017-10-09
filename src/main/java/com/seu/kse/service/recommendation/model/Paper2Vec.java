package com.seu.kse.service.recommendation.model;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.service.recommendation.Configuration;
import com.seu.kse.service.recommendation.ReccommendUtils;
import com.seu.kse.service.impl.PaperService;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaosheng on 2017/5/26.
 */


public class Paper2Vec {
    private static Word2Vec vec;
    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    private PaperMapper paperDao = (PaperMapper) ac.getBean("paperMapper");
    public Map<String, double[]> paperVecs = new HashMap<String, double[]>();
    //private static Logger log = LoggerFactory.getLogger(Paper2Vec.class);
    public void modelByWord2vce(){
        try {
            SentenceIterator iter = new BasicLineIterator(Configuration.sentencesFile);
            // Split on white spaces in the line to get words
            TokenizerFactory t = new DefaultTokenizerFactory();

        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens.
         */
            t.setTokenPreProcessor(new CommonPreprocessor());
            //log.info("Building model....");
            System.out.println("Building model....");
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(Configuration.dimensions)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            //log.info("Fitting Word2Vec model....");
            System.out.println("Fitting Word2Vec model....");
            vec.fit();

            //log.info("Writing word vectors to text file....");
            System.out.println("Writing word vectors to text file....");
            // Write word vectors to file
            WordVectorSerializer.writeWord2VecModel(vec, Configuration.modelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Word2Vec loadWord2VecModelFromText(){
        if(vec == null){
            vec=WordVectorSerializer.readWord2VecModel(Configuration.modelFile);
        }
        return vec;
    }

    public Map<String, double[]> calPaperVec(){
        Map<String, double[]> paperVec = new HashMap<String, double[]>();
        vec = loadWord2VecModelFromText();
        List<Paper> papers = paperDao.selectAllPaper();
        //获得每一篇论文的词表,按空格分词
        Word2DocByAve w2d = new Word2DocByAve();
        for(Paper paper : papers){
            String title = paper.getTitle();
            String paperAbstract = paper.getPaperAbstract();
            String[] words1 = ReccommendUtils.segmentation(title);
            String[] words2 = ReccommendUtils.segmentation(paperAbstract);
            int len = words1.length + words2.length;
            String[] words = new String[len];
            for(int i=0;i<words1.length;i++){
                words[i] = words1[i];
            }for(int i=0;i<words2.length;i++){
                words[i] = words2[i];
            }
            //根据 words 计算 paper向量
            double[] docVec = w2d.calDocVec(words);
            paperVec.put(paper.getId(),docVec);
        }
        paperVecs = paperVec;
        return paperVec;
    }

}

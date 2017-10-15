package com.seu.kse.service.recommendation.model;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.service.recommendation.CB.CBKNNModel;
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
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaosheng on 2017/5/26.
 */


public class Paper2Vec {

    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    private PaperMapper paperDao = (PaperMapper) ac.getBean("paperMapper");
    public Map<String, double[]> paperVecs = new HashMap<String, double[]>();
    private ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    private String filepath = classloader.getResource(Configuration.sentencesFile).getPath();
    //private static Logger log = LoggerFactory.getLogger(Paper2Vec.class);
    public void modelByWord2vce(){
        try {
            Word2Vec vec;
            SentenceIterator iter = new BasicLineIterator(filepath);
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
            URL url = Paper2Vec.class.getClassLoader().getResource(Configuration.modelFile);
            if(url==null){
                String root_path =  Paper2Vec.class.getClassLoader().getResource("/").getPath();
                File file = new File(root_path+"/"+Configuration.modelFile);
                file.createNewFile();
            }
            WordVectorSerializer.writeWord2VecModel(vec, Paper2Vec.class.getClassLoader().getResource(Configuration.modelFile).getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Word2Vec loadWord2VecModelFromText(){


        Word2Vec vec=WordVectorSerializer.readWord2VecModel(Paper2Vec.class.getClassLoader().getResource(Configuration.modelFile).getPath());

        return vec;
    }

    public Map<String, double[]> calPaperVec(){
        URL url =CBKNNModel.class.getClassLoader().getResource(Configuration.paper_vec);

        File paper2vec_file = null ;
        if (url!=null){
            String path = url.getPath();
            paper2vec_file = new File(path);
        }


        boolean flag = false;
        if(paper2vec_file!=null&&paper2vec_file.exists()) {
            try {
                FileInputStream fin = new FileInputStream(CBKNNModel.class.getClassLoader().getResource(Configuration.paper_vec).getPath());
                ObjectInputStream oin = new ObjectInputStream(fin);
                System.out.println("载入paper向量");
                paperVecs=(Map<String, double[]>) oin.readObject();
                System.out.println("加载完成paper向量");

            } catch (FileNotFoundException e) {
                flag = true;
                e.printStackTrace();
            } catch (IOException e) {
                flag = true;
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                flag = true;
                e.printStackTrace();
            }

        }
        if(flag||paper2vec_file==null){

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
                paperVecs.put(paper.getId(),docVec);
            }

            try{
                String root_path = CBKNNModel.class.getClassLoader().getResource("/").getPath();
                FileOutputStream fos = new FileOutputStream(root_path+"/"+Configuration.paper_vec);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(paperVecs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("paper2vec load 完成");
        return paperVecs;
    }

}

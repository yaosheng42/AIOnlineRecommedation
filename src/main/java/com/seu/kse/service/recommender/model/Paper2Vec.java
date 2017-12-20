package com.seu.kse.service.recommender.model;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import com.seu.kse.service.recommender.CB.CBKNNModel;
import com.seu.kse.service.recommender.RecommenderCache;
import com.seu.kse.util.Configuration;
import com.seu.kse.service.recommender.ReccommendUtils;
import com.seu.kse.util.LogUtils;
import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.stopwords.StopWords;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yaosheng on 2017/5/26.
 */


public class Paper2Vec {

    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    private PaperMapper paperDao = (PaperMapper) ac.getBean("paperMapper");
    public static Map<String, double[]> paperVecs = new HashMap<String, double[]>();
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

            LogUtils.info("Building word2vec model ...",Paper2Vec.class);
            vec = new Word2Vec.Builder()
                    .minWordFrequency(5)
                    .iterations(1)
                    .layerSize(Configuration.dimensions)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .stopWords(StopWords.getStopWords())
                    .build();


            LogUtils.info("Fitting Word2Vec model....",Paper2Vec.class);
            vec.fit();

            LogUtils.info("Writing word vectors to text file....",Paper2Vec.class);
            // Write word vectors to file
            URL url = Thread.currentThread().getContextClassLoader().getResource(Configuration.modelFile);
            if(url==null){
                String root_path =  Thread.currentThread().getContextClassLoader().getResource("/").getPath();
                File file = new File(root_path+"/"+Configuration.modelFile);
                file.createNewFile();
            }
            WordVectorSerializer.writeWord2VecModel(vec, Thread.currentThread().getContextClassLoader().getResource(Configuration.modelFile).getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage(),Paper2Vec.class);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage(),Paper2Vec.class);
        }

    }


    public Word2Vec loadWord2VecModelFromText(){
        LogUtils.info("loading word2vec from Text File",Paper2Vec.class);
        Word2Vec vec=WordVectorSerializer.readWord2VecModel(Paper2Vec.class.getClassLoader().getResource(Configuration.modelFile).getPath());
        return vec;
    }

    public void loadPaperVec(){
        try {
            FileInputStream fin = new FileInputStream(CBKNNModel.class.getClassLoader().getResource(Configuration.paper_vec).getPath());
            ObjectInputStream oin = new ObjectInputStream(fin);
            LogUtils.info("loading paper vector",Paper2Vec.class);

            paperVecs=(Map<String, double[]>) oin.readObject();

            LogUtils.info("loading paper vector complete",Paper2Vec.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage(),Paper2Vec.class);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(e.getMessage(),Paper2Vec.class);
        } catch (ClassNotFoundException e) {
            LogUtils.error(e.getMessage(),Paper2Vec.class);
            e.printStackTrace();
        }
    }

    public void calPaperVec(){

            List<Paper> papers = paperDao.selectAllPaper();
            //获得每一篇论文的词表,按空格分词
            Word2DocByAve w2d = new Word2DocByAve();
            RecommenderCache.paperRowMapID = new ArrayList<String>();
            for(Paper paper : papers){
                //构建ID映射
                RecommenderCache.paperRowMapID.add(paper.getId());
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
                String root_path = Paper2Vec.class.getClassLoader().getResource("/").getPath();
                FileOutputStream fos = new FileOutputStream(root_path+"/"+Configuration.paper_vec);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(paperVecs);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        LogUtils.info("loading paper2vec model complete",Paper2Vec.class);

    }

}

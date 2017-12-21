package com.seu.kse.service.recommender;








import com.seu.kse.service.recommender.model.PaperSim;
import com.seu.kse.util.Configuration;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yaosheng on 2017/5/25.
 */
public class ReccommendUtils {
    //根据论文的title 和摘要 计算论文相似度

    public static List<String>  stopwords;

    /**
     * 余弦相似度
     * @param paper1
     * @param paper2
     * @return
     */
    public static double cosinSimilarity(double[] paper1, double[] paper2){

        double sim=0.0;
        double simDiv1 = 0.0;
        double simDiv2 = 0.0;
        double simDiv ;
        for(int i= 0; i<paper1.length;i++){
            sim = sim+paper1[i]*paper2[i];
            simDiv1 = simDiv1+Math.pow(paper1[i],2);
        }
        simDiv1 = Math.sqrt(simDiv1);

        for(int i=0; i<paper2.length ;i++){
            simDiv2 = simDiv2+Math.pow(paper2[i],2);
        }
        simDiv2 = Math.sqrt(simDiv2);
        simDiv=simDiv1*simDiv2;
        sim = sim/simDiv;
        return sim;
    }

    public static String[] segmentation(String sentence){
        String[] words = sentence.split(" ");
        return words;
    }

    public static  double[] sumVecs(double[] vec1, double[] vec2){
        if (vec1.length != vec2.length) return null;
        for(int i=0; i<vec1.length;i++){
            vec1[i]+=vec2[i];
        }
        return vec1;
    }

    public static double[] vecDiv(double[] vecs,int n){
        for (int i=0;i<vecs.length;i++){
            vecs[i] = vecs[i]/n;
        }
        return vecs;
    }

    public static void loadStopWords(String file){
        try {
            stopwords = new ArrayList<String>();
            BufferedReader bd = new BufferedReader(new FileReader(new File(file)));
            String line = bd.readLine();
            while(line!=null && line.length()!=0){
                stopwords.add(line);
                line = bd.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> delStopWords(String[] words){
        if(stopwords == null) loadStopWords(ReccommendUtils.class.getClassLoader().getResource(Configuration.stopWords).getPath());
        List<String> delWords = new ArrayList<String>();
        for(String word : words){
            for(String stopword : stopwords){
                if(word!=null){
                    word=word.trim();
                    if(!stopword.contains(word) &&! stopword.equals(word)){
                        delWords.add(word);
                    }
                }

            }
        }
        return delWords;
    }

    public static void generateSimilarPaperList(){
        for(Map.Entry<String, double[]> e1 : RecommenderCache.paperVecs.entrySet()){
            List<PaperSim> sims = new ArrayList<PaperSim>();
            String pid1 = e1.getKey();
            for(Map.Entry<String, double[]> e2 : RecommenderCache.paperVecs.entrySet()){
                if(e1==e2) continue;
                String pid2 = e2.getKey();
                double sim = ReccommendUtils.cosinSimilarity(e1.getValue(),e2.getValue());
                PaperSim paperSim = new PaperSim(pid2, sim);
                sims.add(paperSim);
            }
            Collections.sort(sims);
            List<PaperSim> subSims =  new ArrayList<PaperSim>(sims.subList(0,20));
            RecommenderCache.similarPaperList.put(pid1,subSims);
        }
    }

    /*
    public static List<String> extractKeys(String content){
        if(sw==null){
            sw= new StopWords(Configuration.stopWords);
        }
        CWSTagger seg = null;
        List<String> keywords = new ArrayList<String>();
        try {
            seg = new CWSTagger(Configuration.projectRoot + "/resources/models/seg.m");
            AbstractExtractor key = new WordExtract(seg, sw);
            Map<String, Integer> keys = key.extract(content, 5);
            for (Map.Entry<String, Integer> e : keys.entrySet()) {
                keywords.add(e.getKey());
            }

        } catch (LoadModelException e) {
            e.printStackTrace();
        }
        return  keywords;
    }*/

}

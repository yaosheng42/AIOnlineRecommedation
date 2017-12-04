package com.seu.kse.service.recommender.CB;

import com.seu.kse.bean.Paper;
import com.seu.kse.dao.PaperMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * 将所有论文的标题和文档当作文本，形成一个大的文档
 * Created by yaosheng on 2017/5/26.
 */


public class PaperDocument {

    ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    PaperMapper paperDao ;

    public void ToDocument(String filePath){
        //加载所有论文
        paperDao = (PaperMapper) ac.getBean("paperMapper");
        List<Paper> papers = paperDao.selectAllPaper();
        //将论文的标题和摘要当作句子输出文本
        BufferedWriter bufferedWriter = null;
        try {
            File file =new File(filePath);
            System.out.println(filePath);
            if(!file.exists()){
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            for(Paper paper : papers){
                String title = paper.getTitle();
                String paper_abstract = paper.getPaperAbstract();

                bufferedWriter.write(title);
                bufferedWriter.newLine();
                bufferedWriter.write(paper_abstract);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}

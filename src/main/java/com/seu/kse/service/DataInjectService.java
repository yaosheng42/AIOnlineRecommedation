package com.seu.kse.service;

import com.seu.kse.bean.Author;
import com.seu.kse.bean.AuthorPaperKey;
import com.seu.kse.bean.Paper;
import com.seu.kse.dao.AuthorMapper;
import com.seu.kse.dao.AuthorPaperMapper;
import com.seu.kse.dao.PaperMapper;

import com.seu.kse.util.CommonFileUtil;
import com.seu.kse.util.Configuration;
import com.seu.kse.util.LogUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Zcpwillam on 2017/5/24.
 * add by Yaosheng on 2017/12/4
 */

@Service
public class DataInjectService {



    private final AuthorMapper authorDao;


    private final AuthorPaperMapper authorPaperDao ;


    private final PaperMapper paperDao ;


    private static String path= Configuration.arxiv_path;

    @Autowired
    public DataInjectService(AuthorMapper authorDao, AuthorPaperMapper authorPaperDao, PaperMapper paperDao) {
        this.authorDao = authorDao;
        this.authorPaperDao = authorPaperDao;
        this.paperDao = paperDao;
    }

    public  void dataInject(){
        try{
            //dataInject_init();
            Date now = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd");
            String date = sf.format(now);


            //date = "2017_10_16";
            System.out.println(date);
            File file=new File(path+"/"+date);

            if(!file.exists()) {
                return;
            }
            File[] list=file.listFiles();
            if(list==null) {
                LogUtils.info("无数据",DataInjectService.class);
                return;
            }
            for(File f:list)
                DataInjectByFile(f);
            System.out.println("开始导入数据！");
            LogUtils.info("开始导入数据！",DataInjectService.class);


        }catch (Exception e){
            e.printStackTrace();
            LogUtils.error(e.getMessage(),DataInjectService.class);
        }

    }
    public void dataInject_init(){

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        c.set(2017,Calendar.NOVEMBER,14);
        System.out.println("now :"+now);
        Date cur =  c.getTime();
        System.out.println("cur :"+cur);
        while(cur.before(now)){
            try{
                System.out.print("cur : "+cur);
                SimpleDateFormat sf = new SimpleDateFormat("yyyy_MM_dd");
                String date = sf.format(cur);
                System.out.println(date);
                File file=new File(path+"/"+date);
                if(!file.exists()) {
                    System.out.println(date+"无数据");
                    LogUtils.info(date+"无数据",DataInjectService.class);
                    break;
                }
                File[] list=file.listFiles();
                if(list==null){
                    LogUtils.info("无数据",DataInjectService.class);
                    return;
                }
                for(File f:list)
                    DataInjectByFile(f);
                System.out.println("导入 "+date+" 数据");
                LogUtils.info("导入 "+date+" 数据",DataInjectService.class);


            }catch (Exception e){
                e.printStackTrace();
            }
            c.add(Calendar.DATE,1);
            cur =  c.getTime();

        }

    }
    private void DataInjectByFile(File file){
        try{
            ArrayList<String> inputList= CommonFileUtil.read(file);
            if(inputList==null||inputList.isEmpty()){
                LogUtils.error("Empty File:"+file.getName(),DataInjectService.class);

            }else{
                GenerateDataRecord(inputList);
            }
        }catch (Exception e){
            System.err.println("in DataInjectByFile");
            e.printStackTrace();
            ArrayList<String> inputList= CommonFileUtil.read(file);
            if(inputList==null||inputList.isEmpty()){
                LogUtils.error("Empty File:"+file.getName(),DataInjectService.class);
            }else{
                GenerateDataRecord(inputList);
            }
        }

    }
    private  void GenerateDataRecord(ArrayList<String> recordList){
        ArrayList<String> authorList=new ArrayList<String>();
        HashMap<String,String> paperInfo=new HashMap<String,String>();
        String paperName=recordList.get(0).split("\t")[0];
        String[] tempID = paperName.split("/");
        paperName = tempID[tempID.length-1];

        for(String record:recordList){
            String[] temp=record.split("\t");
            if(temp[1].equals("authors"))
            {
                authorList.add(temp[2]);
            }
            else{
                paperInfo.put(temp[1],temp[2]);
            }
        }
        Paper paper=generatePaper(paperName,paperInfo);
        List<Author> authors=generateAuthorList(authorList);
        int i = insertPaperRecord(paper);
        System.out.print(i);
        List<Integer> aIdList=insertAuthorList(authors);
        if(aIdList!=null&&aIdList.size()>0){
            insertAuthorPaper(paperName,aIdList);
        }
    }
    private static Paper generatePaper(String paperName, Map<String,String> paperInfo){
        Paper paper=new Paper();
        paper.setId(paperName);
        if(paperInfo.containsKey("title"))
            paper.setTitle(paperInfo.get("title"));
        if(paperInfo.containsKey("abstract"))
            paper.setPaperAbstract(paperInfo.get("abstract"));
        if(paperInfo.containsKey("publisher"))
            paper.setPublisher(paperInfo.get("publisher"));
        if(paperInfo.containsKey("downlink")){
            paper.setUrl(paperInfo.get("downlink"));
        }
        if(paperInfo.containsKey("subjects")){
            paper.setKeywords(paperInfo.get("subjects"));
        }
        paper.setPublisher("arxiv");
        paper.setType(0);
        Date time= new java.sql.Date(new Date().getTime());
        paper.setTime(time);
        return paper;
    }
    private static List<Author> generateAuthorList(ArrayList<String> authorList){
        String author_add="https://arxiv.org";
        List<Author> authors=new ArrayList<Author>();
        Author author;
        for(String a:authorList){
            String[] sp = a.split(",");
            String aname = sp[0];
            String aurl = sp[1];
            author=new Author();
            author.setAuthorname(aname);
            author.setUrl(author_add+aurl);
            authors.add(author);
        }
        return  authors;
    }

    /**
     * 插入论文
     * @param p 论文p
     */
    private  int  insertPaperRecord(Paper p){
        //生成关键字

        return paperDao.insert(p);
    }

    private  List<Integer> insertAuthorList(List<Author> authorList){

        List<Integer> authorIdList=new ArrayList<Integer>();
        for(Author author:authorList){//根据作者姓名查询
            Integer authorId=authorDao.selectAidByAuthorName(author.getAuthorname());
            if(authorId==null){
                authorDao.insertSelective(author);
                authorId=authorDao.selectAidByAuthorName(author.getAuthorname());
            }
                authorIdList.add(authorId);
        }
        return authorIdList;
    }
    private  void insertAuthorPaper(String paperId,List<Integer> authorIdList){
        AuthorPaperKey apK=new AuthorPaperKey();
        apK.setPid(paperId);
        Integer count=authorPaperDao.countPid(apK.getPid());

        if(count>0){
            return;
        }
        for(Integer aId:authorIdList){
            apK.setAid(aId);
            int line = authorPaperDao.insertSelective(apK);
            if(line<=0) System.out.println("insert author error : "+aId);
        }
    }
}

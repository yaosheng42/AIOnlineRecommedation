package com.seu.kse.controller;

import com.seu.kse.util.Utils;
import com.seu.kse.bean.*;
import com.seu.kse.dao.UserAuthorFocusMapper;
import com.seu.kse.dao.UserPaperBehaviorMapper;


import com.seu.kse.service.impl.AuthorService;
import com.seu.kse.service.impl.PaperService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by yaosheng on 2017/5/22.
 */

@Controller
@RequestMapping("/")
public class IndexController {


    @Resource
    private PaperService paperService;

    @Resource
    private AuthorService authorService;

    @Resource
    private UserPaperBehaviorMapper userPaperBehaviorDao;

    @Resource
    private UserAuthorFocusMapper userAuthorFocusDao;

//    @Resource
//    private UserPaperNoteMapper userPaperNoteDao;



    /**
     * 显示最近的论文，根据time字段排序
     * @param request 请求
     * @param model 返回参数
     * @return 地址
     */
    @RequestMapping("/index")
    public String toIndex(HttpServletRequest request,HttpSession session, Model model){
        int limit = 20;
        /*if(!Utils.testConnect()){
            return "/index";
        }*/
        Utils.testLogin(session,model);
        int pageNum=0;
        if(request.getParameter("pageNum")!=null) {
            pageNum = Integer.parseInt(request.getParameter("pageNum"));
        }
        List<Paper> papers=paperService.selectPaperByTime(pageNum*limit, (pageNum+1)*limit);
        model.addAttribute("papers",papers);
        model.addAttribute("previousPage",pageNum>0?(pageNum-1):pageNum);
        model.addAttribute("nextPage",pageNum+1);
        //获得作者
        Map<String, List<Author>> authorMap = authorService.getAuthorForPapers(papers);
        model.addAttribute("authorMap",authorMap);
        model.addAttribute("limit",limit);
        model.addAttribute("paper_num",papers.size());
        return "/index";
    }



    @RequestMapping(method= RequestMethod.GET,value="/search",produces="text/plain;charset=UTF-8")
    public @ResponseBody String search(HttpServletRequest request){
        return "";
    }


    @RequestMapping(method= RequestMethod.GET,value="/todayArxiv",produces="text/plain;charset=UTF-8")
    public @ResponseBody String searchTodayArxiv(HttpServletRequest request){
        return "/index";
    }

    @RequestMapping(method= RequestMethod.GET,value="/recommender",produces="text/plain;charset=UTF-8")
    public @ResponseBody String recommender(HttpServletRequest request){
        String uid = request.getParameter("uid");
        
        return "/index";
    }


    /**
     *
     * @param request 请求
     * @return 返回布尔值
     */
    @RequestMapping(method= RequestMethod.GET,value="/testinterest",produces="text/plain;charset=UTF-8")
    public @ResponseBody String testInterest(HttpServletRequest request){

        String uid = request.getParameter("uid");
        int aid = Integer.parseInt(request.getParameter("aid"));
        UserAuthorFocusKey ub = userAuthorFocusDao.selectByPrimaryKey(new UserAuthorFocusKey(uid,aid));
        if(ub!=null ){
            return "true";
        }
        return "false";
    }
    /**
     *
     * @param request 请求
     * @return 返回兴趣
     */
    @RequestMapping(method= RequestMethod.GET,value="/testscoreonpaper",produces="text/plain;charset=UTF-8")
    public @ResponseBody String testScoreOnPaper(HttpServletRequest request){
        String uid = request.getParameter("uid");
        String pid = request.getParameter("pid");
        UserPaperBehaviorKey keys = new UserPaperBehaviorKey();
        keys.setPid(pid);
        keys.setUid(uid);
        UserPaperBehavior user_papers = userPaperBehaviorDao.selectByPrimaryKey(keys);
        if(user_papers==null)return "nothing";
        else return ""+user_papers.getInterest();
    }


    @RequestMapping("/author")
    public String showAuthor(HttpServletRequest request,HttpSession session, Model model){
//        if(!Utils.testConnect()){
//            return "/index";
//        }
        Utils.testLogin(session,model);
        int aid = Integer.valueOf(request.getParameter("id"));
        Author author = authorService.getAuthorByID(aid);
        model.addAttribute("author",author);

        List<Paper> papers=paperService.selectPaperByTime(0, 20);
        Map<String, List<Author>> authorMap = authorService.getAuthorForPapers(papers);

        model.addAttribute("authorMap",authorMap);
        List<Paper> refpapers = papers.subList(0,7);
        model.addAttribute("refPapers",refpapers);
        List<Paper> readedpapers = papers.subList(7,14);
        model.addAttribute("readedpapers",readedpapers);
        List<Paper> relatedpapers = papers.subList(14,20);
        model.addAttribute("relatedpapers",relatedpapers);
        return "/authorinfo";
    }


    /**
     * 关注某一用户
     * @param request 请求
     * @return 返回布尔值
     */
    @RequestMapping(method= RequestMethod.GET,value="/fouseonauthor",produces="text/plain;charset=UTF-8")
    public @ResponseBody String fouseOnAuthor(HttpServletRequest request,HttpSession session, Model model){
//        if(!Utils.testConnect()){
//            return "/index";
//        }
        String id = request.getParameter("aid");
        User login_user =Utils.testLogin(session,model);
        if(login_user == null || id == null) return "error";
        UserAuthorFocusKey key = new UserAuthorFocusKey(login_user.getId(), Integer.parseInt(id));
        UserAuthorFocusKey testkey = userAuthorFocusDao.selectByPrimaryKey(key);
        if(testkey==null){
            userAuthorFocusDao.insert(key);
            return "true";
        }else{
            userAuthorFocusDao.deleteByPrimaryKey(key);
            return "false";
        }
    }

    @RequestMapping("/arxiv")
    public String showArxivPapers(HttpServletRequest request, Model model){
//        if(!Utils.testConnect()){
//            return "/index";
//        }
        int pageNum =Integer.parseInt(request.getParameter("pageNum"));
        List<Paper> papers = paperService.getArxivPapers(pageNum,20);
        model.addAttribute("papers",papers);
        return "/index";
    }



}

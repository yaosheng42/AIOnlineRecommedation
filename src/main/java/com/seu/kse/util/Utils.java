package com.seu.kse.util;

import com.seu.kse.bean.User;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

/**
 * Created by yaosheng on 2017/6/1.
 */
public class Utils {
    public static User testLogin(HttpSession session, Model model){
        User login_user = (User)session.getAttribute(Constant.CURRENT_USER);
        model.addAttribute(Constant.CURRENT_USER,login_user);
        return login_user;
    }
}

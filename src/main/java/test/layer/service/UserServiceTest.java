package test.layer.service;


import com.seu.kse.bean.User;
import com.seu.kse.service.IUserService;
import com.seu.kse.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring-mybatis.xml","classpath*:/spring-mvc.xml"})
public class UserServiceTest {
    @Resource
    IUserService userService;

    @Test
    public void testVerificationUser(){
        User user=userService.verification("a@qq.com","a");
        System.out.println(user.getUname());
    }
}

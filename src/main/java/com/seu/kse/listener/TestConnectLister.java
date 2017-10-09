package com.seu.kse.listener;

import com.seu.kse.bean.User;
import com.seu.kse.dao.UserMapper;
import com.seu.kse.service.impl.RecommendationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by yaosheng on 2017/7/16.
 */
public class TestConnectLister implements ServletContextListener {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> taskHandle;
    private ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-mybatis.xml");
    UserMapper userDao = (UserMapper) ac.getBean("userMapper");
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("测试连接");

        taskBegin();
    }
    public void taskBegin() {
        // 定义一个任务

        final Runnable task = new Runnable() {
            public void run() {
                User u = userDao.testConnect();
                System.out.println(u.getId());
            }
        };
        // 参数
        // command - 要执行的任务
        // initialDelay - 首次执行的延迟时间
        // period - 连续执行之间的周期
        // unit - initialDelay 和 period 参数的时间单位
        System.out.println("测试连接………………………………");

        taskHandle = scheduler.scheduleAtFixedRate(task, 8, 60*60, TimeUnit.SECONDS);
    }
    public void taskEnd(){
        scheduler.shutdown();
    }
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        taskEnd();
    }
}

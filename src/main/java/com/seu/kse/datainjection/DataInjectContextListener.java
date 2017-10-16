package com.seu.kse.datainjection;

/**
 * Created by yaosheng on 2017/6/2.
 */

import com.seu.kse.service.DataInjectService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author wz
 * API中描述：ServletContextListener继承于EventListener，其实现者会在web应用的servlet context改变的时候收到事件通知，
 * 但是还必须要配置到web应用的部署文件中（web.xml）
 */
public class DataInjectContextListener implements ServletContextListener {

    DataInjectService dj ;

    /**
     * 本方法的描述：
     * 在所有的filter和servlet都destroyed后通知web应用的所有的ServletContextListeners[that“本店”即将打烊啦]
     */
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("contextDestroyed…………");
        taskEnd();
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> taskHandle;

    public void taskBegin() {
        // 定义一个任务
        final Runnable task =new Runnable() {
            public void run() {
                DataInjectService dj =new DataInjectService();
                dj.dataInject();
            }
        };
        // 参数
        // command - 要执行的任务
        // initialDelay - 首次执行的延迟时间
        // period - 连续执行之间的周期
        // unit - initialDelay 和 period 参数的时间单位
        System.out.println("数据注入………………………………");

        taskHandle = scheduler.scheduleAtFixedRate(task, 11*1000*60*60, 24*1000*60*60, TimeUnit.MILLISECONDS);
    }
    public void taskEnd(){
        scheduler.shutdown();
    }
    /**
     * 这个方法的描述：
     * 在所有的filter和servlet初始化之前，所有的ServletContextListeners会收到[您所在的web应用的初始化工作开始啦]通知
     */
    public void contextInitialized(ServletContextEvent arg0) {
        dj = new DataInjectService();
        System.out.println("contextInitialized……");
        taskBegin();
    }
}

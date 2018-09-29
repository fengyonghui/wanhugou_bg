package com.wanhutong.backend.modules.config.web;

import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;


/**
 * @Description: 测试类
 *
 * @ClassName: QuartzTest.java
 */
public class QuartzTest {
    public static void main(String[] args) throws BeansException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:/spring-context-quartz.xml");
        QuartzManager quartzManager = (QuartzManager) ctx.getBean("quartzManager");
        quartzManager.getScheduler();
        try {
            System.out.println("【系统启动】开始(每1秒输出一次 job2)...");

            Thread.sleep(5000);
            System.out.println("【增加job1启动】开始(每1秒输出一次)...");
            quartzManager.addJob("test", "test", "test", "test", MyJob.class, "0/1 * * * * ?");


            Thread.sleep(5000);
            System.out.println("【修改job1时间】开始(每2秒输出一次)...");
            quartzManager.modifyJobTime("test", "test", "test", "test", "0/2 * * * * ?");

            Thread.sleep(10000);
            System.out.println("【移除job1定时】开始...");
            quartzManager.removeJob("test", "test", "test", "test");

            // 关掉任务调度容器
            // quartzManager.shutdownJobs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
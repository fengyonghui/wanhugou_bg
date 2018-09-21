package com.wanhutong.backend.modules.config.web;

import org.quartz.Job;
import java.util.Date;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * quartz示例定时器类
 *
 * @author liangxi
 * @date 2018-09-20 15:05
 **/
public class MyJob2{
    public void doSomething(){
        System.out.println(new Date() + "开始执行一次定时任务");
    }
}


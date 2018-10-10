package com.wanhutong.backend.modules.config.web;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
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
public class MyJob{
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(new Date() + ": job 1 doing something...");
    }
}


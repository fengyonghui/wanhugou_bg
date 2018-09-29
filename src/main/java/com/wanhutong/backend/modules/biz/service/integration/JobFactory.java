package com.wanhutong.backend.modules.biz.service.integration;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * 自定义job
 *
 * @author liangxi
 * @date 2018-09-21 13:47
 **/
public class JobFactory extends AdaptableJobFactory {
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            //调用父类的方法
            Object jobInstance = super.createJobInstance(bundle);
            //进行注入
            capableBeanFactory.autowireBean(jobInstance);
            return jobInstance;
        }

}


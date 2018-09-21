package com.wanhutong.backend.modules.biz.service.integration;

import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * 积分活动定时业务
 *
 * @author liangxi
 * @date 2018-09-20 17:10
 **/
public class BizIntegrationTimer {
      @Resource
      private BizIntegrationActivityService bizIntegrationActivityService;

      @Resource
      private SchedulerFactory schedulerFactory;
      public void test(){
            try {
                  Scheduler scheduler = schedulerFactory.getScheduler();
                  for (String groupName : scheduler.getJobGroupNames()) {
                        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                              String jobName = jobKey.getName();
                              //从数据库中查询活动列表
                              BizIntegrationActivity bizIntegrationActivity = bizIntegrationActivityService.get(Integer.valueOf(jobName));
                              if(Objects.isNull(bizIntegrationActivity))
                              {
                                    System.out.println("正在执行的任务"+jobName);
                                    Integer id = bizIntegrationActivity.getId();
                              }
                        }
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
}


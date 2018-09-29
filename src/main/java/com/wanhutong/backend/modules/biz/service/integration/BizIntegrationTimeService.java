package com.wanhutong.backend.modules.biz.service.integration;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.utils.CloseableHttpClientUtil;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.dao.integration.BizIntegrationActivityDao;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.entity.integration.ActivityVo;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.entity.logistic.LogisticEntity;
import com.wanhutong.backend.modules.biz.service.cust.BizCustCreditService;
import com.wanhutong.backend.modules.biz.web.integration.BizIntegrationActivityController;
import com.wanhutong.backend.modules.sys.entity.Office;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
@Component
public class BizIntegrationTimeService implements Job{
      @Autowired
      private BizIntegrationActivityService bizIntegrationActivityService;
      @Autowired
      private BizIntegrationActivityDao bizIntegrationActivityDao;

      @Autowired
      private BizMoneyRecodeService bizMoneyRecodeService;

      private static Logger LOGGER = LoggerFactory.getLogger(BizIntegrationActivityController.class);
      @Override
      public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            try {
                  JobDetail jobDetail = jobExecutionContext.getJobDetail();
                  String name = jobDetail.getKey().getName();
                  BizIntegrationActivity bizIntegrationActivity = bizIntegrationActivityService.get(Integer.valueOf(name));
                  //获取发送范围
                  Integer sendScope = bizIntegrationActivity.getSendScope();
                  Date date = new Date();
                  switch(sendScope){
                        case 0:
                              List<Office> allOfficeS = bizIntegrationActivityService.findAllOffice();
                              doSomeThing(allOfficeS,bizIntegrationActivity);
                              LOGGER.info("当前时间为:"+date+"正在执行定时任务："+"发送范围为全部用户");
                              break;
                        case -1:
                              List<Office> orderedOffices = bizIntegrationActivityService.findOrderedOffice();
                              doSomeThing(orderedOffices,bizIntegrationActivity);
                              LOGGER.info("当前时间为:"+date+"正在执行定时任务："+"发送范围为下单用户");
                              break;
                        case -2:
                              List<Office> unOrderOffices = bizIntegrationActivityService.findUnOrderOffice();
                              doSomeThing(unOrderOffices,bizIntegrationActivity);
                              LOGGER.info("当前时间为:"+date+"正在执行定时任务："+"发送范围为未下单用户");
                              break;
                        default:
                              String officeIds = bizIntegrationActivity.getOfficeIds();
                              List<Office> referedOffices = bizIntegrationActivityService.findCheckedOffice(officeIds);
                              doSomeThing(referedOffices,bizIntegrationActivity);
                              LOGGER.info("当前时间为:"+date+"正在执行定时任务："+"发送范围为指定用户");
                  }
            } catch (Exception e) {
                  e.printStackTrace();
            }
      }
      private void doSomeThing(List<Office> offices,BizIntegrationActivity bizIntegrationActivity) {
            //修改活动发送状态为已发送
            bizIntegrationActivity.setSendStatus(1);
            bizIntegrationActivityDao.updateActivitySendStatus(bizIntegrationActivity.getId());
            Integer sendScope = bizIntegrationActivity.getSendScope();
            BigDecimal integrationNum = bizIntegrationActivity.getIntegrationNum();
            BizMoneyRecode bizMoneyRecode = null;
            List<BizMoneyRecode> arrayList = new ArrayList<>();
            Date date = new Date();
            if(CollectionUtils.isNotEmpty(offices))
            {
                  for (Office o : offices) {
                        bizMoneyRecode = new BizMoneyRecode();
                        Integer officeId = o.getId();
                        //根据officeId查询用户可用积分
                        BigDecimal avaiableMoney = bizMoneyRecodeService.selectMoneyByOfficeId(officeId);
                        if (!Objects.isNull(avaiableMoney)) {
                              BigDecimal newMoney = avaiableMoney.add(integrationNum);
                              bizMoneyRecode.setNewMoney(newMoney.toString());
                        }
                        bizMoneyRecode.setOffice(o);
                        bizMoneyRecode.setStatus(1);
                        bizMoneyRecode.setMoney(integrationNum);
                        bizMoneyRecode.setStatusCode(12);
                        bizMoneyRecode.setStatusName("获得-活动赠送");
                        bizMoneyRecode.setCreateId(bizIntegrationActivity.getCreateBy().getId());
                        bizMoneyRecode.setUpdateId(bizIntegrationActivity.getUpdateBy().getId());
                        bizMoneyRecode.setCreateDate(new Date());
                        bizMoneyRecode.setUpdateDate(new Date());
                        bizMoneyRecode.setComment(bizIntegrationActivity.getActivityName() + "赠送");
                        arrayList.add(bizMoneyRecode);
                  }
            }
            //添加积分流水表
            bizMoneyRecodeService.saveAll(arrayList);
            //更新用户积分
            bizMoneyRecodeService.updateMoney(arrayList);
            //发送短信
            String url = DsConfig.getActivityMsgUrl();
            CloseableHttpClient httpClient = CloseableHttpClientUtil.createSSLClientDefault();
            try {
                  HttpPost httpPost = new HttpPost(url);
                  JSONObject param = new JSONObject();
                  ActivityVo activityVo = new ActivityVo();
                  if (sendScope == -3) {
                        activityVo.setOfficeIds( bizIntegrationActivity.getOfficeIds());
                  }
                  activityVo.setSendScope(bizIntegrationActivity.getSendScope());
                  activityVo.setActivityName(bizIntegrationActivity.getActivityName());
                  activityVo.setIntegrationNum(integrationNum);
                  String json = JsonUtil.getJson(activityVo);
                  httpPost.setEntity(new StringEntity(json.toString(), Charset.forName("UTF-8")));
                  httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json;charset=utf-8");
                  httpPost.setHeader("Accept", "application/json");
                  CloseableHttpResponse response = httpClient.execute(httpPost);
                  String result = EntityUtils.toString(response.getEntity(), "utf-8");
            } catch (Exception e) {
                  LOGGER.error("短信发送异常", e);
            } finally {
                  if (httpClient != null) {
                        try {
                              httpClient.close();
                        } catch (IOException e) {
                              LOGGER.error("httpClient关闭异常，710", e);
                        }
                  }
            }
      }
}


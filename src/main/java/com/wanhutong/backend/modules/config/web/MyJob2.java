package com.wanhutong.backend.modules.config.web;

import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import com.wanhutong.backend.modules.biz.service.integration.BizMoneyRecodeService;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    @Autowired
    private BizMoneyRecodeService bizMoneyRecodeService;

    @Autowired
    private OfficeService officeService;

    List<BizMoneyRecode> arrayList = new ArrayList<>();
    Date date = new Date();
    BizMoneyRecode bizMoneyRecode = null;
    public void doSomething(){
        //系统每年7.1自动清除上一年度未使用积分：获得-使用
        List<BizMoneyRecodeDetail> list = bizMoneyRecodeService.selectExpireMoney();
        for(BizMoneyRecodeDetail biz:list)
        {
            Integer officeId = biz.getOfficeId();
            BigDecimal expireIntegration = biz.getGainIntegration().subtract(biz.getUsedIntegration());
            if(expireIntegration.equals(0))
            {
                continue;
            }
            //添加积分流水过期记录
            bizMoneyRecode = new BizMoneyRecode();
            //根据officeId查询用户可用积分
            BigDecimal aviableMoney = bizMoneyRecodeService.selectMoneyByOfficeId(officeId);
            if (!Objects.isNull(aviableMoney)) {
                BigDecimal newMoney = aviableMoney.multiply(expireIntegration);
                bizMoneyRecode.setNewMoney(newMoney.toString());
            }
            bizMoneyRecode.setStatus(1);
            bizMoneyRecode.setMoney(expireIntegration);
            bizMoneyRecode.setStatusCode(30);
            bizMoneyRecode.setStatusName("过期");
            bizMoneyRecode.setCreateId(1);
            bizMoneyRecode.setUpdateId(1);
            bizMoneyRecode.setCreateDate(new Date());
            bizMoneyRecode.setUpdateDate(new Date());
            bizMoneyRecode.setComment("每年7月1日，自动过期");
            Office office = officeService.get(officeId);
            if(!Objects.isNull(office))
            {
                bizMoneyRecode.setOffice(office);
                arrayList.add(bizMoneyRecode);
            }
        }
        //添加积分流水表
        bizMoneyRecodeService.saveAll(arrayList);
        //更新用户积分
        bizMoneyRecodeService.updateMoney(arrayList);
    }
}


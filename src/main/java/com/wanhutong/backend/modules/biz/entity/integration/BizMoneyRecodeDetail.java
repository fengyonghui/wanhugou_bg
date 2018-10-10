package com.wanhutong.backend.modules.biz.entity.integration;

import java.math.BigDecimal;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * <p>
 * 积分使用详情
 *
 * @author liangxi
 * @date 2018-09-17 11:56
 **/
public class BizMoneyRecodeDetail {
     //经销店id
     private Integer officeId;
     //累计获得
     private BigDecimal gainIntegration;
     //累计使用
     private BigDecimal usedIntegration;
     //累计过期
     private BigDecimal expireIntegration;
     //可用
     private BigDecimal availableIntegration;
     //全部用户总数
     private Integer totalUser;
     //已下单用户总数
     private Integer orderUser;
     //未下单用户总数
     private Integer unOrderUser;

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public Integer getUnOrderUser() {
        return unOrderUser;
    }

    public void setUnOrderUser(Integer unOrderUser) {
        this.unOrderUser = unOrderUser;
    }

    public Integer getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Integer totalUser) {
        this.totalUser = totalUser;
    }

    public Integer getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(Integer orderUser) {
        this.orderUser = orderUser;
    }

    public BigDecimal getGainIntegration() {
        return gainIntegration;
    }

    public void setGainIntegration(BigDecimal gainIntegration) {
        this.gainIntegration = gainIntegration;
    }

    public BigDecimal getUsedIntegration() {
        return usedIntegration;
    }

    public void setUsedIntegration(BigDecimal usedIntegration) {
        this.usedIntegration = usedIntegration;
    }

    public BigDecimal getExpireIntegration() {
        return expireIntegration;
    }

    public void setExpireIntegration(BigDecimal expireIntegration) {
        this.expireIntegration = expireIntegration;
    }

    public BigDecimal getAvailableIntegration() {
        return availableIntegration;
    }

    public void setAvailableIntegration(BigDecimal availableIntegration) {
        this.availableIntegration = availableIntegration;
    }
}


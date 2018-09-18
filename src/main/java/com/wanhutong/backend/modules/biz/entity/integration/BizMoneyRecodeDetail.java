package com.wanhutong.backend.modules.biz.entity.integration;

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
     //累计获得
     private Double gainIntegration;
     //累计使用
     private Double usedIntegration;
     //累计过期
     private Double expireIntegration;
     //可用
     private Double availableIntegration;
     //全部用户总数
     private Integer totalUser;
     //已下单用户总数
     private Integer orderUser;
     //未下单用户总数
     private Integer unOrderUser;

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

    public Double getGainIntegration() {
        return gainIntegration;
    }

    public void setGainIntegration(Double gainIntegration) {
        this.gainIntegration = gainIntegration;
    }

    public Double getUsedIntegration() {
        return usedIntegration;
    }

    public void setUsedIntegration(Double usedIntegration) {
        this.usedIntegration = usedIntegration;
    }

    public Double getExpireIntegration() {
        return expireIntegration;
    }

    public void setExpireIntegration(Double expireIntegration) {
        this.expireIntegration = expireIntegration;
    }

    public Double getAvailableIntegration() {
        return availableIntegration;
    }

    public void setAvailableIntegration(Double availableIntegration) {
        this.availableIntegration = availableIntegration;
    }
}


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


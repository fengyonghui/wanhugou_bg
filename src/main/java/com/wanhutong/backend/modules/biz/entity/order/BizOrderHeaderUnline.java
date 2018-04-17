/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 线下支付订单(1: 普通订单 ; 2:帐期采购 3:配资采购)Entity
 *
 * @author ZhangTengfei
 * @version 2018-04-17
 */
public class BizOrderHeaderUnline extends DataEntity<BizOrderHeaderUnline> {

    private static final long serialVersionUID = 1L;
    private BizOrderHeader orderHeader;        // 订单id
    private String imgUrl;        // 单据凭证
    private BigDecimal unlinePayMoney;  //线下付款金额
    private BigDecimal realMoney;       //实收金额

    public BizOrderHeader getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(BizOrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public BigDecimal getUnlinePayMoney() {
        return unlinePayMoney;
    }

    public void setUnlinePayMoney(BigDecimal unlinePayMoney) {
        this.unlinePayMoney = unlinePayMoney;
    }

    public BigDecimal getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(BigDecimal realMoney) {
        this.realMoney = realMoney;
    }
}
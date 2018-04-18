/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 线下支付订单(线下独有)Entity
 * @author ZhangTengfei
 * @version 2018-04-17
 */
public class BizOrderHeaderUnline extends DataEntity<BizOrderHeaderUnline> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// 订单ID
	private String imgUrl;		// 单据凭证图
	private BigDecimal unlinePayMoney;		// 线下付款金额
	private BigDecimal realMoney;		// 实收金额
	
	public BizOrderHeaderUnline() {
		super();
	}

	public BizOrderHeaderUnline(Integer id){
		super(id);
	}

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
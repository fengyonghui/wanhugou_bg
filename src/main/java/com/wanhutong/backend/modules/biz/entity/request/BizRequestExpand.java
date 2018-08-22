/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;

import java.math.BigDecimal;

/**
 * 备货单扩展Entity
 * @author TengFei.zhang
 * @version 2018-07-26
 */
public class BizRequestExpand extends DataEntity<BizRequestExpand> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * bizRequestHeader.id
	 */
	private BizRequestHeader requestHeader;
	/**
	 * 供应商备货，对应的供应商ID
	 */
	private BizVendInfo bizVendInfo;
	/**
	 * 当前支付单ID
	 */
	private BizPoPaymentOrder bizPoPaymentOrder;
	/**
	 * 与供应商结算的金额
	 */
	private BigDecimal balanceTotal;

	public BizRequestExpand() {
		super();
	}

	public BizRequestExpand(Integer id){
		super(id);
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}
	
	public BizVendInfo getBizVendInfo() {
		return bizVendInfo;
	}

	public void setBizVendInfo(BizVendInfo bizVendInfo) {
		this.bizVendInfo = bizVendInfo;
	}

	public BizPoPaymentOrder getBizPoPaymentOrder() {
		return bizPoPaymentOrder;
	}

	public void setBizPoPaymentOrder(BizPoPaymentOrder bizPoPaymentOrder) {
		this.bizPoPaymentOrder = bizPoPaymentOrder;
	}
	
	public BigDecimal getBalanceTotal() {
		return balanceTotal;
	}

	public void setBalanceTotal(BigDecimal balanceTotal) {
		this.balanceTotal = balanceTotal;
	}
	
}
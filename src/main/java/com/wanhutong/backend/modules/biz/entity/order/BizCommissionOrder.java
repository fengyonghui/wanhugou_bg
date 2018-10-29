/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 佣金付款订单关系表Entity
 * @author wangby
 * @version 2018-10-18
 */
public class BizCommissionOrder extends DataEntity<BizCommissionOrder> {
	
	private static final long serialVersionUID = 1L;
	private Integer orderId;		// 订单id,biz_order_header.id
	private Integer commId;		// 佣金付款单id,biz_commission_pay.id
	private BigDecimal commission;		// 佣金

	private BizCommission bizCommission;

	public BizCommissionOrder() {
		super();
	}

	public BizCommissionOrder(Integer id){
		super(id);
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getCommId() {
		return commId;
	}

	public void setCommId(Integer commId) {
		this.commId = commId;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BizCommission getBizCommission() {
		return bizCommission;
	}

	public void setBizCommission(BizCommission bizCommission) {
		this.bizCommission = bizCommission;
	}
}
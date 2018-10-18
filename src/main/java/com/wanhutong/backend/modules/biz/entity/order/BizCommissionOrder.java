/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 佣金付款订单关系表Entity
 * @author wangby
 * @version 2018-10-18
 */
public class BizCommissionOrder extends DataEntity<BizCommissionOrder> {
	
	private static final long serialVersionUID = 1L;
	private Integer orderId;		// 订单id,biz_order_header.id
	private Integer comPayId;		// 佣金付款单id,biz_commission_pay.id
	private Double commission;		// 佣金

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

	public Integer getComPayId() {
		return comPayId;
	}

	public void setComPayId(Integer comPayId) {
		this.comPayId = comPayId;
	}

	public Double getCommission() {
		return commission;
	}

	public void setCommission(Double commission) {
		this.commission = commission;
	}
	
}
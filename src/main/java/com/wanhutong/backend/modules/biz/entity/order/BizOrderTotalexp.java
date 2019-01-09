/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 服务费记录表Entity
 * @author wangby
 * @version 2018-12-11
 */
public class BizOrderTotalexp extends DataEntity<BizOrderTotalexp> {
	
	private static final long serialVersionUID = 1L;
	private Integer orderId;		// order_id
	private String amount;		// amount
	private Integer type;		// type
	private Integer uVersion;		// 版本控制
	
	public BizOrderTotalexp() {
		super();
	}

	public BizOrderTotalexp(Integer id){
		super(id);
	}

	@NotNull(message="order_id不能为空")
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@NotNull(message="type不能为空")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@NotNull(message="版本控制不能为空")
	public Integer getUVersion() {
		return uVersion;
	}

	public void setUVersion(Integer uVersion) {
		this.uVersion = uVersion;
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 代采订单约定付款时间表Entity
 * @author ZhangTengfei
 * @version 2018-05-30
 */
public class BizOrderAppointedTime extends DataEntity<BizOrderAppointedTime> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// 订单Id biz_order_header
	private Date appointedDate;		// 约定时间
	private BigDecimal appointedMoney;		// 约定金额
	private BigDecimal paymentMoney;		// 支付金额
	
	public BizOrderAppointedTime() {
		super();
	}

	public BizOrderAppointedTime(Integer id){
		super(id);
	}

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="约定时间不能为空")
	public Date getAppointedDate() {
		return appointedDate;
	}

	public void setAppointedDate(Date appointedDate) {
		this.appointedDate = appointedDate;
	}

	public BigDecimal getAppointedMoney() {
		return appointedMoney;
	}

	public void setAppointedMoney(BigDecimal appointedMoney) {
		this.appointedMoney = appointedMoney;
	}

	public BigDecimal getPaymentMoney() {
		return paymentMoney;
	}

	public void setPaymentMoney(BigDecimal paymentMoney) {
		this.paymentMoney = paymentMoney;
	}
}
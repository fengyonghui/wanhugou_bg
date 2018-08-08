/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 退款记录Entity
 * @author 王冰洋
 * @version 2018-07-13
 */
public class BizDrawBack extends DataEntity<BizDrawBack> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderDetail orderDetail;		// biz_order_detail
	private Integer bizStatus;		// biz_order_header.biz_status
	private Integer orderType;		// 订单类型：0订单，1备货清单
	private Integer statusType;		// 0-订单状态记录，1-退款状态记录
	private String code;		// 退款编号
	private Integer drawbackStatus;		// 退款状态- 0-申请 1审核通过 2驳回 3撤销申请退款
	private BigDecimal drawbackMoney;		// 申请退款金额
	private Date applyTime;		// 申请退款时间
	private String reasons;		// 退款原因
	
	public BizDrawBack() {
		super();
	}

	public BizDrawBack(Integer id){
		super(id);
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}
	
	@NotNull(message="订单类型：0订单，1备货清单不能为空")
	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	
	public Integer getStatusType() {
		return statusType;
	}

	public void setStatusType(Integer statusType) {
		this.statusType = statusType;
	}
	
	@Length(min=0, max=64, message="退款编号长度必须介于 0 和 64 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public Integer getDrawbackStatus() {
		return drawbackStatus;
	}

	public void setDrawbackStatus(Integer drawbackStatus) {
		this.drawbackStatus = drawbackStatus;
	}

	public BizOrderDetail getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(BizOrderDetail orderDetail) {
		this.orderDetail = orderDetail;
	}

	public BigDecimal getDrawbackMoney() {
		return drawbackMoney;
	}

	public void setDrawbackMoney(BigDecimal drawbackMoney) {
		this.drawbackMoney = drawbackMoney;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="申请退款时间不能为空")
	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}
	
	@Length(min=0, max=200, message="退款原因长度必须介于 0 和 200 之间")
	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
	
}
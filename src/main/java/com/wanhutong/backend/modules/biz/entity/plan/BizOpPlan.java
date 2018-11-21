/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.plan;

import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 运营计划Entity
 * @author 张腾飞
 * @version 2018-03-15
 */
public class BizOpPlan extends DataEntity<BizOpPlan> {
	
	private static final long serialVersionUID = 1L;
	private String objectId;		// id in table name
	private String objectName;		// table name
	private String year;		// 计划所在年份;在选择年计划时， 一定加入条件：and month=0 and day=0
	private String month;		// 计划所在月份; 选择月计划时，一定加入条件：and day=0
	private String day;		// 日计划;
	private BigDecimal amount;		// amount
	/**
	 * 新用户
	 */
	private Integer newUser;
	/**
	 * 服务费
	 */
	private Double serviceCharge;

	private String objectName1;  //office中的id对应名称
	private String objectName2;  //user 中的id对应名称
	private Office centerOffice;   //采购中心id
	private User user; //采购专员

	/**
	 * 联营订单总额
	 */
	private BigDecimal jointOrderAmount;
	/**
	 * 代采订单总额
	 */
	private BigDecimal purchaseOrderAmount;

	public BizOpPlan() {
		super();
	}

	public BizOpPlan(Integer id){
		super(id);
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getObjectName1() {
		return objectName1;
	}

	public void setObjectName1(String objectName1) {
		this.objectName1 = objectName1;
	}

	public String getObjectName2() {
		return objectName2;
	}

	public void setObjectName2(String objectName2) {
		this.objectName2 = objectName2;
	}

	public Office getCenterOffice() {
		return centerOffice;
	}

	public void setCenterOffice(Office centerOffice) {
		this.centerOffice = centerOffice;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getNewUser() {
		return newUser;
	}

	public void setNewUser(Integer newUser) {
		this.newUser = newUser;
	}

	public Double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public BigDecimal getJointOrderAmount() {
		return jointOrderAmount;
	}

	public void setJointOrderAmount(BigDecimal jointOrderAmount) {
		this.jointOrderAmount = jointOrderAmount;
	}

	public BigDecimal getPurchaseOrderAmount() {
		return purchaseOrderAmount;
	}

	public void setPurchaseOrderAmount(BigDecimal purchaseOrderAmount) {
		this.purchaseOrderAmount = purchaseOrderAmount;
	}
}
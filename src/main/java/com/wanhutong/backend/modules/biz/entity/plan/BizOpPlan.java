/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.plan;

import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

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
	private String amount;		// amount
	private String objectName1;  //office中的id对应名称
	private String objectName2;  //user 中的id对应名称
	private Office centerOffice;   //采购中心id
	private User user; //采购专员
	
	public BizOpPlan() {
		super();
	}

	public BizOpPlan(Integer id){
		super(id);
	}

	@Length(min=1, max=11, message="id in table name长度必须介于 1 和 11 之间")
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	@Length(min=1, max=255, message="table name长度必须介于 1 和 255 之间")
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	@Length(min=1, max=11, message="计划所在年份;在选择年计划时， 一定加入条件：and month=0 and day=0长度必须介于 1 和 11 之间")
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
	
	@Length(min=1, max=11, message="计划所在月份; 选择月计划时，一定加入条件：and day=0长度必须介于 1 和 11 之间")
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}
	
	@Length(min=1, max=11, message="日计划;长度必须介于 1 和 11 之间")
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
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
	
}
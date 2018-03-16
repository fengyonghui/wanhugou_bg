/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.plan;

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
	private String status;		// status
	private String uVersion;		// u_version
	
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
	
	@Length(min=1, max=4, message="status长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=1, max=4, message="u_version长度必须介于 1 和 4 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
	}
	
}
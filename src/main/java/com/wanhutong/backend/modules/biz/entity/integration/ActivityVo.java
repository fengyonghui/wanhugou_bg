/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.integration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分活动Entity
 * @author LX
 * @version 2018-09-16
 */
public class ActivityVo extends DataEntity<ActivityVo> {

	private static final long serialVersionUID = 1L;
	private String activityName;		// 活动名称
	private String officeIds;
	private BigDecimal integrationNum;// 每人赠送积分
    private Integer sendScope; //发送范围

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getOfficeIds() {
		return officeIds;
	}

	public void setOfficeIds(String officeIds) {
		this.officeIds = officeIds;
	}

	public BigDecimal getIntegrationNum() {
		return integrationNum;
	}

	public void setIntegrationNum(BigDecimal integrationNum) {
		this.integrationNum = integrationNum;
	}

	public Integer getSendScope() {
		return sendScope;
	}

	public void setSendScope(Integer sendScope) {
		this.sendScope = sendScope;
	}
}
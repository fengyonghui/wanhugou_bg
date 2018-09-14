/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.integration;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 积分活动Entity
 * @author LX
 * @version 2018-09-14
 */
public class BizIntegrationActivity extends DataEntity<BizIntegrationActivity> {
	
	private static final long serialVersionUID = 1L;
	private String activityName;		// 活动名称
	private Date sendTime;		// 发送时间
	private String sendScope;		// 发送范围，0是全部用户，-1下单用户，-2未下单用户，其他为指定用户
	private String activityTools;		// 优惠工具
	private Integer sendNum;		// 发送人数
	private Integer integrationNum;		// 每人赠送积分
	private String description;		// 备注说明
	private Integer status;		// status
	private String uVersion;		// 版本控制；重要
	
	public BizIntegrationActivity() {
		super();
	}

	public BizIntegrationActivity(Integer id){
		super(id);
	}

	@Length(min=0, max=50, message="活动名称长度必须介于 0 和 50 之间")
	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	@Length(min=0, max=100, message="发送范围，0是全部用户，-1下单用户，-2未下单用户，其他为指定用户长度必须介于 0 和 100 之间")
	public String getSendScope() {
		return sendScope;
	}

	public void setSendScope(String sendScope) {
		this.sendScope = sendScope;
	}
	
	@Length(min=0, max=10, message="优惠工具长度必须介于 0 和 10 之间")
	public String getActivityTools() {
		return activityTools;
	}

	public void setActivityTools(String activityTools) {
		this.activityTools = activityTools;
	}
	
	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}
	
	public Integer getIntegrationNum() {
		return integrationNum;
	}

	public void setIntegrationNum(Integer integrationNum) {
		this.integrationNum = integrationNum;
	}
	
	@Length(min=0, max=100, message="备注说明长度必须介于 0 和 100 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Length(min=1, max=4, message="版本控制；重要长度必须介于 1 和 4 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 排产计划Entity
 * @author 王冰洋
 * @version 2018-07-17
 */
public class BizSchedulingPlan extends DataEntity<BizSchedulingPlan> {
	
	private static final long serialVersionUID = 1L;
	private String objectName;		// 表名
	private String objectId;		// 表ID
	private Integer originalNum;		// 单子原始数量
	private Integer schedulingNum;		// 排产数量
	private Integer completeNum;		// 已完成数量
	
	public BizSchedulingPlan() {
		super();
	}

	public BizSchedulingPlan(Integer id){
		super(id);
	}

	@Length(min=1, max=100, message="表名长度必须介于 1 和 100 之间")
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	@Length(min=1, max=11, message="表ID长度必须介于 1 和 11 之间")
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	@NotNull(message="单子原始数量不能为空")
	public Integer getOriginalNum() {
		return originalNum;
	}

	public void setOriginalNum(Integer originalNum) {
		this.originalNum = originalNum;
	}
	
	@NotNull(message="排产数量不能为空")
	public Integer getSchedulingNum() {
		return schedulingNum;
	}

	public void setSchedulingNum(Integer schedulingNum) {
		this.schedulingNum = schedulingNum;
	}

	public Integer getCompleteNum() {
		return completeNum;
	}

	public void setCompleteNum(Integer completeNum) {
		this.completeNum = completeNum;
	}
	
}
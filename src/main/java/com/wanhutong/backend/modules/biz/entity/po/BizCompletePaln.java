/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 确认排产表Entity
 * @author 王冰洋
 * @version 2018-07-24
 */
public class BizCompletePaln extends DataEntity<BizCompletePaln> {
	
	private static final long serialVersionUID = 1L;
	private Integer schedulingId;		// 排产ID
	private Integer completeNum;		// 完成排产数量
	private Date planDate;		// 排产日期
	private Integer status;		// 记录状态 1: active ； 0：inactive

	
	public BizCompletePaln() {
		super();
	}

	public BizCompletePaln(Integer id){
		super(id);
	}

	@NotNull(message="排产ID不能为空")
	public Integer getSchedulingId() {
		return schedulingId;
	}

	public void setSchedulingId(Integer schedulingId) {
		this.schedulingId = schedulingId;
	}
	
	@NotNull(message="完成排产数量不能为空")
	public Integer getCompleteNum() {
		return completeNum;
	}

	public void setCompleteNum(Integer completeNum) {
		this.completeNum = completeNum;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}
	
	@NotNull(message="记录状态 1: active ； 0：inactive不能为空")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
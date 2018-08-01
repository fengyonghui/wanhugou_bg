/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;

/**
 * 排产计划Entity
 * @author 王冰洋
 * @version 2018-07-17
 */
public class BizSchedulingPlan extends DataEntity<BizSchedulingPlan> {
	
	private static final long serialVersionUID = 1L;
	private String objectName;		// 表名
	private Integer objectId;		// 表ID
	private Integer originalNum;		// 单子原始数量
	private Integer schedulingNum;		// 排产数量
	private Integer completeNum;		// 已完成数量
	/**
	 * 采购订单表Entity
	 */
	private BizPoDetail bizPoDetail;

	/**
	 * 备货清单详细信息Entity
	 */
	private BizRequestDetail bizRequestDetail;
	/**
	 * 该排产相关的总确认数
	 */
	private Integer sumCompleteNum;

    /**
     * 确认排产list
     */
    private List<BizCompletePaln> completePalnList;

	/**
	 * 排产日期
	 */
	private Date planDate;		// 排产日期

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

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public Integer getOriginalNum() {
		return originalNum;
	}

	public void setOriginalNum(Integer originalNum) {
		this.originalNum = originalNum;
	}

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

	public BizPoDetail getBizPoDetail() {
		return bizPoDetail;
	}

	public void setBizPoDetail(BizPoDetail bizPoDetail) {
		this.bizPoDetail = bizPoDetail;
	}

	public BizRequestDetail getBizRequestDetail() {
		return bizRequestDetail;
	}

	public void setBizRequestDetail(BizRequestDetail bizRequestDetail) {
		this.bizRequestDetail = bizRequestDetail;
	}

	public List<BizCompletePaln> getCompletePalnList() {
		return completePalnList;
	}

	public void setCompletePalnList(List<BizCompletePaln> completePalnList) {
		this.completePalnList = completePalnList;
	}

	public Integer getSumCompleteNum() {
		return sumCompleteNum;
	}

	public void setSumCompleteNum(Integer sumCompleteNum) {
		this.sumCompleteNum = sumCompleteNum;
	}

	public Date getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}
}
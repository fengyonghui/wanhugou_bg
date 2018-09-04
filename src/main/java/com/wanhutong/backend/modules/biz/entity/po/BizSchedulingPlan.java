/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.hibernate.validator.constraints.Length;

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
	private String remark;     //备注
//	private Integer schedulingNum;		// 排产数量
//	private Integer completeNum;		// 已完成数量
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
	//private Integer sumCompleteNum;

    /**
     * 确认排产list
     */
    private List<BizCompletePaln> completePalnList;

	/**
	 * 完成日期
	 */
	private Date planDate;		// 完成日期

	/**
	 * 排产是否已确认 0：未确认，1：已确认
	 */
	private Integer completeStatus;

	/**
	 * 备货清单entity
	 */
	private BizRequestHeader bizRequestHeader;

	/**
	 * 采购单排产状态 0,未排产  1,排产中  2,排产完成
	 */
	public Integer poSchType;

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

//	public Integer getSchedulingNum() {
//		return schedulingNum;
//	}
//
//	public void setSchedulingNum(Integer schedulingNum) {
//		this.schedulingNum = schedulingNum;
//	}

//	public Integer getCompleteNum() {
//		return completeNum;
//	}
//
//	public void setCompleteNum(Integer completeNum) {
//		this.completeNum = completeNum;
//	}

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

//	public Integer getSumCompleteNum() {
//		return sumCompleteNum;
//	}
//
//	public void setSumCompleteNum(Integer sumCompleteNum) {
//		this.sumCompleteNum = sumCompleteNum;
//	}

	public Date getPlanDate() {
		return planDate;
	}

	public void setPlanDate(Date planDate) {
		this.planDate = planDate;
	}

	public Integer getCompleteStatus() {
		return completeStatus;
	}

	public void setCompleteStatus(Integer completeStatus) {
		this.completeStatus = completeStatus;
	}

	public BizRequestHeader getBizRequestHeader() {
		return bizRequestHeader;
	}

	public void setBizRequestHeader(BizRequestHeader bizRequestHeader) {
		this.bizRequestHeader = bizRequestHeader;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getPoSchType() {
		return poSchType;
	}

	public void setPoSchType(Integer poSchType) {
		this.poSchType = poSchType;
	}
}
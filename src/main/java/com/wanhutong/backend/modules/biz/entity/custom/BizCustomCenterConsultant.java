/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.custom;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 客户专员管理Entity
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
public class BizCustomCenterConsultant extends DataEntity<BizCustomCenterConsultant> {

	private static final long serialVersionUID = 1L;
	private Office customs;		// 采购商.id ; sys_office.id = sys_user.company_id
	private Office centers;		// 采购中心ID
	private User consultants;		// 客户专员ID sys_user.id
	private String parentIds;

	private String queryCustomes;	//关联采购商列表查询状态

	private List<BizCustomCenterConsultant> bccList;	//用于存放采购中心和客户专员下的采购商

	/**
	 * 客户专员关联采购商显示详细地址
	 * */
	private CommonLocation bizLocation;
	private String ordrHeaderStartTime;//日期查询
	private String orderHeaderEedTime;//日期查询

	/**
	 * 订单数量
	 */
	private Integer orderCount;

	/**
	 * 批量移除经销店时，经销店id字符串
	 */
	private String custIds;

	public BizCustomCenterConsultant() {
		super();
	}

	public BizCustomCenterConsultant(Integer id){
		super(id);
	}

	public Office getCustoms() {
		return customs;
	}

	public void setCustoms(Office customs) {
		this.customs = customs;
	}

	public Office getCenters() {
		return centers;
	}

	public void setCenters(Office centers) {
		this.centers = centers;
	}

	public User getConsultants() {
		return consultants;
	}

	public void setConsultants(User consultants) {
		this.consultants = consultants;
	}

	public List<BizCustomCenterConsultant> getBccList() {
		return bccList;
	}

	public void setBccList(List<BizCustomCenterConsultant> bccList) {
		this.bccList = bccList;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getQueryCustomes() {
		return queryCustomes;
	}

	public void setQueryCustomes(String queryCustomes) {
		this.queryCustomes = queryCustomes;
	}

	public CommonLocation getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(CommonLocation bizLocation) {
		this.bizLocation = bizLocation;
	}

	public String getOrdrHeaderStartTime() {
		return ordrHeaderStartTime;
	}

	public void setOrdrHeaderStartTime(String ordrHeaderStartTime) {
		this.ordrHeaderStartTime = ordrHeaderStartTime;
	}

	public String getOrderHeaderEedTime() {
		return orderHeaderEedTime;
	}

	public void setOrderHeaderEedTime(String orderHeaderEedTime) {
		this.orderHeaderEedTime = orderHeaderEedTime;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public String getCustIds() {
		return custIds;
	}

	public void setCustIds(String custIds) {
		this.custIds = custIds;
	}
}
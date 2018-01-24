/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.custom;

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
	private User consultants;		// 采购顾问ID sys_user.id
//	private Integer statuss;		//用于修改状态为0

	private String status;

	private List<BizCustomCenterConsultant> bccList;	//用于存放采购中心和采购顾问下的采购商

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<BizCustomCenterConsultant> getBccList() {
		return bccList;
	}

	public void setBccList(List<BizCustomCenterConsultant> bccList) {
		this.bccList = bccList;
	}

//	public Integer getStatuss() {
//		return statuss;
//	}
//
//	public void setStatuss(Integer statuss) {
//		this.statuss = statuss;
//	}
}
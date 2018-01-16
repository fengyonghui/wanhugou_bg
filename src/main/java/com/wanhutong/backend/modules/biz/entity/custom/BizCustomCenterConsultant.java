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

	private List<Office>  officeList;	//用于存储所有采购商
	private String officeIds;		//全选的数据接收

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

	public List<Office> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Office> officeList) {
		this.officeList = officeList;
	}

	public String getOfficeIds() {
		return officeIds;
	}

	public void setOfficeIds(String officeIds) {
		this.officeIds = officeIds;
	}
}
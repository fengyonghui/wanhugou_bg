package com.wanhutong.backend.modules.sys.entity;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * * 
* <p>Title: BuyerAdviser</p>
* <p>Description: 采购商客户专员关联</p>
* <p>Company: WHT</p> 
* @date 2018年1月11日
 */
public class BuyerAdviser extends DataEntity<BuyerAdviser>{

	private static final long serialVersionUID = 1L;
	
	private Integer custId;//采购商
	
	private Integer centerId;//采购中心
	
	private Integer consultantId;//客户专员
	
	private String consultantName;

	private String status;

	public Integer getCustId() {
		return custId;
	}

	public void setCustId(Integer custId) {
		this.custId = custId;
	}

	public Integer getCenterId() {
		return centerId;
	}

	public void setCenterId(Integer centerId) {
		this.centerId = centerId;
	}

	public Integer getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Integer consultantId) {
		this.consultantId = consultantId;
	}

	public String getConsultantName() {
		return consultantName;
	}

	public void setConsultantName(String consultantName) {
		this.consultantName = consultantName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}

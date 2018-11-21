/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;


import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;

import java.math.BigDecimal;

/**
 * 服务费--配送方式Entity
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
public class BizServiceCharge extends DataEntity<BizServiceCharge> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 服务方式（字典表 service_cha）0 客户自提；1送货到家；2厂家直发
	 */
	private Byte serviceMode;

	/**
	 * 分类
	 */
	private BizVarietyInfo varietyInfo;

	/**
	 * 服务费
	 */
	private BigDecimal servicePrice;

	public BizServiceCharge() {
		super();
	}

	public BizServiceCharge(Integer id){
		super(id);
	}

	public Byte getServiceMode() {
		return serviceMode;
	}

	public void setServiceMode(Byte serviceMode) {
		this.serviceMode = serviceMode;
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}

	public BigDecimal getServicePrice() {
		return servicePrice;
	}

	public void setServicePrice(BigDecimal servicePrice) {
		this.servicePrice = servicePrice;
	}
}
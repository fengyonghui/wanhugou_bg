/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shelf;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购中心品类阶梯价Entity
 * @author ZhangTengfei
 * @version 2018-04-27
 */
public class BizCentVarietyFactor extends DataEntity<BizCentVarietyFactor> {
	
	private static final long serialVersionUID = 1L;
	private Office center;		// 采购中心ID
	private BizVarietyInfo varietyInfo;		// 产品分类Id
	private BizOpShelfInfo shelfInfo;		// 货架ID
	private Integer serviceFactor;		// 服务费系数
	private Integer minQty;		// 最小数量
	private Integer maxQty;		// 最大数量
	private String serviceFactors;
	private String	minQtys;
	private String maxQtys;

	public BizCentVarietyFactor() {
		super();
	}

	public BizCentVarietyFactor(Integer id){
		super(id);
	}

	public Office getCenter() {
		return center;
	}

	public void setCenter(Office center) {
		this.center = center;
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}

	public BizOpShelfInfo getShelfInfo() {
		return shelfInfo;
	}

	public void setShelfInfo(BizOpShelfInfo shelfInfo) {
		this.shelfInfo = shelfInfo;
	}

	public Integer getServiceFactor() {
		return serviceFactor;
	}

	public void setServiceFactor(Integer serviceFactor) {
		this.serviceFactor = serviceFactor;
	}

	public Integer getMinQty() {
		return minQty;
	}

	public void setMinQty(Integer minQty) {
		this.minQty = minQty;
	}

	public Integer getMaxQty() {
		return maxQty;
	}

	public void setMaxQty(Integer maxQty) {
		this.maxQty = maxQty;
	}

	public String getServiceFactors() {
		return serviceFactors;
	}

	public void setServiceFactors(String serviceFactors) {
		this.serviceFactors = serviceFactors;
	}

	public String getMinQtys() {
		return minQtys;
	}

	public void setMinQtys(String minQtys) {
		this.minQtys = minQtys;
	}

	public String getMaxQtys() {
		return maxQtys;
	}

	public void setMaxQtys(String maxQtys) {
		this.maxQtys = maxQtys;
	}
}
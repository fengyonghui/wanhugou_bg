/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shelf;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 品类阶梯价Entity
 * @author ZhangTengfei
 * @version 2018-04-27
 */
public class BizVarietyFactor extends DataEntity<BizVarietyFactor> {
	
	private static final long serialVersionUID = 1L;
	private BizVarietyInfo varietyInfo;		// 产品分类Id
	private Integer serviceFactor;		// 服务费系数
	private Integer minQty;		// 最小数量
	private Integer maxQty;		// 最大数量

	private Double salePrice;		//销售单价
	
	public BizVarietyFactor() {
		super();
	}

	public BizVarietyFactor(Integer id){
		super(id);
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
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

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}
}
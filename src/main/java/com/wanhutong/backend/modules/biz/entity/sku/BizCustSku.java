/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import com.wanhutong.backend.modules.sys.entity.Office;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;

/**
 * 采购商商品价格Entity
 * @author ZhangTengfei
 * @version 2018-05-15
 */
public class BizCustSku extends DataEntity<BizCustSku> {
	
	private static final long serialVersionUID = 1L;
	private Office customer;		// 采购商ID
	private BizSkuInfo skuInfo;		// 商品ID
	private BigDecimal unitPrice;		// 商品价格
	
	public BizCustSku() {
		super();
	}

	public BizCustSku(Integer id){
		super(id);
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
}
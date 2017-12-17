/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 属性表Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizProdPropertyInfo extends DataEntity<BizProdPropertyInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizProductInfo productInfo;		// biz_product_info.id
	private String propName;		// 属性名称
	private String propDescription;		// 属性描述

	private String prodPropertyValues;


	
	public BizProdPropertyInfo() {
		super();
	}

	public BizProdPropertyInfo(Integer id){
		super(id);
	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	@Length(min=1, max=20, message="属性名称长度必须介于 1 和 20 之间")
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
	
	@Length(min=0, max=200, message="属性描述长度必须介于 0 和 200 之间")
	public String getPropDescription() {
		return propDescription;
	}

	public void setPropDescription(String propDescription) {
		this.propDescription = propDescription;
	}

	public String getProdPropertyValues() {
		return prodPropertyValues;
	}

	public void setProdPropertyValues(String prodPropertyValues) {
		this.prodPropertyValues = prodPropertyValues;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 订单详情商品属性Entity
 * @author OuyangXiutian
 * @version 2018-01-25
 */
public class BizOrderSkuPropValue extends DataEntity<BizOrderSkuPropValue> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderDetail orderDetails;		// 订单详情ID
	private BizProdPropValue propInfos;		// spu属性ID（biz_prod_prop_value.prop_id）
	private String propName;		// sku属性名称
	private String propValue;		// sku属性值（biz_sku_prop_value.prop_value）

	
	public BizOrderSkuPropValue() {
		super();
	}

	public BizOrderSkuPropValue(Integer id){
		super(id);
	}

	public BizOrderDetail getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(BizOrderDetail orderDetails) {
		this.orderDetails = orderDetails;
	}

	public BizProdPropValue getPropInfos() {
		return propInfos;
	}

	public void setPropInfos(BizProdPropValue propInfos) {
		this.propInfos = propInfos;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * sku属性Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizSkuPropValue extends DataEntity<BizSkuPropValue> {
	
	private static final long serialVersionUID = 1L;
	private BizSkuInfo skuInfo;		// biz_sku_info.id
	private BizProdPropertyInfo prodPropertyInfo;		// prod_prop_id
	private String propName;		// prop_name
	private BizProdPropValue prodPropValue;		// biz_prod_prop_value.id
	private String propValue;		// biz_prod_prop_value
	private String code; //编码
	private String source;
	private PropertyInfo propertyInfo;
	private PropValue propValueObj;

	private Integer prodPropId;		//用于查询属性值
	
	public BizSkuPropValue() {
		super();
	}

	public BizSkuPropValue(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="prop_name长度必须介于 1 和 30 之间")
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	@Length(min=1, max=100, message="biz_prod_prop_value长度必须介于 1 和 100 之间")
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public BizProdPropertyInfo getProdPropertyInfo() {
		return prodPropertyInfo;
	}

	public void setProdPropertyInfo(BizProdPropertyInfo prodPropertyInfo) {
		this.prodPropertyInfo = prodPropertyInfo;
	}

	public BizProdPropValue getProdPropValue() {
		return prodPropValue;
	}

	public void setProdPropValue(BizProdPropValue prodPropValue) {
		this.prodPropValue = prodPropValue;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getProdPropId() {
		return prodPropId;
	}

	public void setProdPropId(Integer prodPropId) {
		this.prodPropId = prodPropId;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public PropValue getPropValueObj() {
		return propValueObj;
	}

	public void setPropValueObj(PropValue propValueObj) {
		this.propValueObj = propValueObj;
	}
}
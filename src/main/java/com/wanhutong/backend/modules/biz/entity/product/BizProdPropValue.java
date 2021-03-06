/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 记录产品所有属性值Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizProdPropValue extends DataEntity<BizProdPropValue> {
	
	private static final long serialVersionUID = 1L;
	private BizProdPropertyInfo prodPropertyInfo;		// biz_prod_property_info.id
	private BizCatePropertyInfo catePropertyInfo;		//biz_cate_property_info.id或sys_property_info.id
	private PropertyInfo propertyInfo;					//biz_cate_property_info.id或sys_property_info.id
	private String propName;							// 属性名称
	private BizCatePropValue catePropValue;			// biz_cate_prop_value.id
	private PropValue sysPropValue;   					//biz_cate_prop_value.id或sys_prop_value
	private String propValue;							// 属性值
	private String code; //值编码
	private String source;								// sys系统，cate分类

	private Integer propertyInfoId;

	private Integer propertyValueId;

	private Integer prodPropertyInfoId;

	
	public BizProdPropValue() {
		super();
	}

	public BizProdPropValue(Integer id){
		super(id);
	}

	
	@Length(min=0, max=30, message="属性名称长度必须介于 0 和 30 之间")
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	@Length(min=0, max=100, message="属性值长度必须介于 0 和 100 之间")
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	
	@Length(min=0, max=4, message="sys系统，cate分类长度必须介于 0 和 4 之间")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}


	public BizProdPropertyInfo getProdPropertyInfo() {
		return prodPropertyInfo;
	}

	public void setProdPropertyInfo(BizProdPropertyInfo prodPropertyInfo) {
		this.prodPropertyInfo = prodPropertyInfo;
	}

	public BizCatePropertyInfo getCatePropertyInfo() {
		return catePropertyInfo;
	}

	public void setCatePropertyInfo(BizCatePropertyInfo catePropertyInfo) {
		this.catePropertyInfo = catePropertyInfo;
	}

	public BizCatePropValue getCatePropValue() {
		return catePropValue;
	}

	public void setCatePropValue(BizCatePropValue catePropValue) {
		this.catePropValue = catePropValue;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public PropValue getSysPropValue() {
		return sysPropValue;
	}

	public void setSysPropValue(PropValue sysPropValue) {
		this.sysPropValue = sysPropValue;
	}

	public Integer getPropertyInfoId() {
		return propertyInfoId;
	}

	public void setPropertyInfoId(Integer propertyInfoId) {
		this.propertyInfoId = propertyInfoId;
	}

	public Integer getPropertyValueId() {
		return propertyValueId;
	}

	public void setPropertyValueId(Integer propertyValueId) {
		this.propertyValueId = propertyValueId;
	}

	public Integer getProdPropertyInfoId() {
		return prodPropertyInfoId;
	}

	public void setProdPropertyInfoId(Integer prodPropertyInfoId) {
		this.prodPropertyInfoId = prodPropertyInfoId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
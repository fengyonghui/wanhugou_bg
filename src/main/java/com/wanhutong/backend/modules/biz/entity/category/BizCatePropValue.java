/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import org.hibernate.validator.constraints.Length;

/**
 * 记录分类下所有属性值Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatePropValue extends DataEntity<BizCatePropValue> {
	
	private static final long serialVersionUID = 1L;
	private BizCatePropertyInfo catePropertyInfo;		// biz_cate_property_info.id
	private String propName;
	private String value;		// 记录该属性值
	private String source;

	private PropertyInfo propertyInfo;

	private Integer propertyInfoId;

	private PropValue propValue;

	private Integer propertyValueId;

	
	public BizCatePropValue() {
		super();
	}

	public BizCatePropValue(Integer id){
		super(id);
	}


	@Length(min=1, max=100, message="记录该属性值长度必须介于 1 和 100 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PropValue getPropValue() {
		return propValue;
	}

	public void setPropValue(PropValue propValue) {
		this.propValue = propValue;
	}

	public BizCatePropertyInfo getCatePropertyInfo() {
		return catePropertyInfo;
	}

	public void setCatePropertyInfo(BizCatePropertyInfo catePropertyInfo) {
		this.catePropertyInfo = catePropertyInfo;
	}

	public Integer getPropertyValueId() {
		return propertyValueId;
	}

	public void setPropertyValueId(Integer propertyValueId) {
		this.propertyValueId = propertyValueId;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public Integer getPropertyInfoId() {
		return propertyInfoId;
	}

	public void setPropertyInfoId(Integer propertyInfoId) {
		this.propertyInfoId = propertyInfoId;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
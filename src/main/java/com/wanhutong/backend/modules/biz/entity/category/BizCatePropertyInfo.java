/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 记录当前分类下的所有属性Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatePropertyInfo extends DataEntity<BizCatePropertyInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizCategoryInfo categoryInfo;		// biz_category_info.id
	private String name;		// 分类名称
	private String description;		// 分类描述

	private Integer propertyInfoId;

	private PropertyInfo propertyInfo;

	private List<BizCatePropValue>catePropValueList;

	private String catePropertyValues;

	
	public BizCatePropertyInfo() {
		super();
	}

	public BizCatePropertyInfo(Integer id){
		super(id);
	}


	@Length(min=1, max=30, message="分类名称长度必须介于 1 和 30 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Length(min=1, max=200, message="分类描述长度必须介于 1 和 200 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	public BizCategoryInfo getCategoryInfo() {
		return categoryInfo;
	}

	public void setCategoryInfo(BizCategoryInfo categoryInfo) {
		this.categoryInfo = categoryInfo;
	}

	public List<BizCatePropValue> getCatePropValueList() {
		return catePropValueList;
	}

	public void setCatePropValueList(List<BizCatePropValue> catePropValueList) {
		this.catePropValueList = catePropValueList;
	}

	public Integer getPropertyInfoId() {
		return propertyInfoId;
	}

	public void setPropertyInfoId(Integer propertyInfoId) {
		this.propertyInfoId = propertyInfoId;
	}

	public String getCatePropertyValues() {
		return catePropertyValues;
	}

	public void setCatePropertyValues(String catePropertyValues) {
		this.catePropertyValues = catePropertyValues;
	}
}
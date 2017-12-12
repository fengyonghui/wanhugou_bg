/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.DataEntity;
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
	private String discription;		// 分类描述

	private List<BizCatePropValue>catePropValueList;

	
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
	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
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
}
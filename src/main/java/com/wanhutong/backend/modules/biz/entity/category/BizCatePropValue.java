/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 记录分类下所有属性值Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatePropValue extends DataEntity<BizCatePropValue> {
	
	private static final long serialVersionUID = 1L;
	private BizCatePropertyInfo catePropertyInfo;		// biz_cate_property_info.id
	private String value;		// 记录该属性值

	
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

	public BizCatePropertyInfo getCatePropertyInfo() {
		return catePropertyInfo;
	}

	public void setCatePropertyInfo(BizCatePropertyInfo catePropertyInfo) {
		this.catePropertyInfo = catePropertyInfo;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 分类属性中间表Entity
 * @author ZhangTengfei
 * @version 2018-05-28
 */
public class BizVarietyAttr extends DataEntity<BizVarietyAttr> {
	
	private static final long serialVersionUID = 1L;
	private BizVarietyInfo varietyInfo;		// biz_variety_info.id ;分类ID
	private AttributeInfoV2 attributeInfo;		// sys_attribute_info.id ;系统属性ID
	private String attributeIds;		//多选的属性id
	
	public BizVarietyAttr() {
		super();
	}

	public BizVarietyAttr(Integer id){
		super(id);
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}

	public AttributeInfoV2 getAttributeInfo() {
		return attributeInfo;
	}

	public void setAttributeInfo(AttributeInfoV2 attributeInfo) {
		this.attributeInfo = attributeInfo;
	}

	public String getAttributeIds() {
		return attributeIds;
	}

	public void setAttributeIds(String attributeIds) {
		this.attributeIds = attributeIds;
	}
}
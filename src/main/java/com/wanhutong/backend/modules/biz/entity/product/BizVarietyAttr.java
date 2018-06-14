/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;

import java.util.List;

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
	private Integer required;		//是否是必选属性：0:不是必选；1:是必选

	private String requireds;		//多个required

	private List<Dict> dictList;        //字典表属性值
    private List<AttributeValueV2> attributeValueV2List;
	
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

	public Integer getRequired() {
		return required;
	}

	public String getRequireds() {
		return requireds;
	}

	public void setRequireds(String requireds) {
		this.requireds = requireds;
	}

	public void setRequired(Integer required) {
		this.required = required;
	}

	public String getAttributeIds() {
		return attributeIds;
	}

	public void setAttributeIds(String attributeIds) {
		this.attributeIds = attributeIds;
	}

    public List<Dict> getDictList() {
        return dictList;
    }

    public void setDictList(List<Dict> dictList) {
        this.dictList = dictList;
    }

    public List<AttributeValueV2> getAttributeValueV2List() {
        return attributeValueV2List;
    }

    public void setAttributeValueV2List(List<AttributeValueV2> attributeValueV2List) {
        this.attributeValueV2List = attributeValueV2List;
    }
}
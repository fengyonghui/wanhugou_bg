/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 系统属性Entity
 * @author liuying
 * @version 2017-12-11
 */
public class PropertyInfo extends DataEntity<PropertyInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 分类名称
	private String description;		// 分类描述

	private List<PropValue> propValueList;


	public PropertyInfo() {
		super();
	}

	public PropertyInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="分类名称长度必须介于 1 和 30 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	@Length(min=1, max=200, message="分类描述长度必须介于 1 和 200 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<PropValue> getPropValueList() {
		return propValueList;
	}

	public void setPropValueList(List<PropValue> propValueList) {
		this.propValueList = propValueList;
	}
}
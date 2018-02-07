/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 系统属性值Entity
 * @author liuying
 * @version 2017-12-11
 */
public class PropValue extends DataEntity<PropValue> {
	
	private static final long serialVersionUID = 1L;
	private PropertyInfo propertyInfo;		//
	private String value;		// 记录该属性值
	private String code;  //编码

	
	public PropValue() {
		super();
	}

	public PropValue(Integer id){
		super(id);
	}

	
	@Length(min=1, max=100, message="记录该属性值长度必须介于 1 和 100 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PropertyInfo getPropertyInfo() {
		return propertyInfo;
	}

	public void setPropertyInfo(PropertyInfo propertyInfo) {
		this.propertyInfo = propertyInfo;
	}

	@Length(min=1, max=10, message="记录该属性值长度必须介于 1 和 10 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
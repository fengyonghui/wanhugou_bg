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
 * 系统属性默认表Entity
 * @author liuying
 * @version 2017-12-14
 */
public class DefaultProp extends DataEntity<DefaultProp> {
	
	private static final long serialVersionUID = 1L;
	private String propKey;		// 系统属性键
	private String propValue;		// 系统属性值( 一般关联其他表的主键 )
	private String propDesc;		// 属性描述

	
	public DefaultProp() {
		super();
	}

	public DefaultProp(Integer id){
		super(id);
	}
	public DefaultProp(String propKey){
		this.propKey=propKey;
	}

	@Length(min=1, max=50, message="系统属性键长度必须介于 1 和 50 之间")
	public String getPropKey() {
		return propKey;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}
	
	@Length(min=1, max=11, message="系统属性值( 一般关联其他表的主键 )长度必须介于 1 和 11 之间")
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	
	@Length(min=1, max=50, message="属性描述长度必须介于 1 和 50 之间")
	public String getPropDesc() {
		return propDesc;
	}

	public void setPropDesc(String propDesc) {
		this.propDesc = propDesc;
	}
	

}
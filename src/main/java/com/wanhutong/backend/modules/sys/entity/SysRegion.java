/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity;

import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 角色区域表Entity
 * @author zx
 * @version 2017-12-11
 */
public class SysRegion extends DataEntity<SysRegion> {
	
	private static final long serialVersionUID = 1L;
	private String code;		// 地区编码
	private String pcode;		// 父编码
	private String name;		// 名称
	private String level;		// 级别
	private String openStatus;		// 开通区域 0：未开通 1：已开通
	
	public SysRegion() {
		super();
	}

	public SysRegion(Integer id){
		super(id);
	}

	@Length(min=1, max=64, message="地区编码长度必须介于 1 和 64 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=1, max=64, message="父编码长度必须介于 1 和 64 之间")
	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
	
	@Length(min=1, max=64, message="名称长度必须介于 1 和 64 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=8, message="级别长度必须介于 1 和 8 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	@Length(min=1, max=4, message="开通区域 0：未开通 1：已开通长度必须介于 1 和 4 之间")
	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}
	
}
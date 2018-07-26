/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 产品种类管理Entity
 * @author liuying
 * @version 2018-02-24
 */
public class BizVarietyInfo extends DataEntity<BizVarietyInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizCatelogInfo bizCatelogInfo;		// 目录分类 --大的一级分类
	private String name;		// name
	private String code;		// code
	private String description;		// 分类描述
	/**
	 * 品类主管
	 */
	private User user;

	
	public BizVarietyInfo() {
		super();
	}

	public BizVarietyInfo(Integer id){
		super(id);
	}


	@Length(min=0, max=100, message="name长度必须介于 0 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=20, message="code长度必须介于 0 和 20 之间")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Length(min=0, max=512, message="分类描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BizCatelogInfo getBizCatelogInfo() {
		return bizCatelogInfo;
	}

	public void setBizCatelogInfo(BizCatelogInfo bizCatelogInfo) {
		this.bizCatelogInfo = bizCatelogInfo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
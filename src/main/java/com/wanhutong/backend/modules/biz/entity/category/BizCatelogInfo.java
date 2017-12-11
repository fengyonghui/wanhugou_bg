/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.DataEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 目录分类表Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatelogInfo extends DataEntity<BizCatelogInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 产品目录名称
	private String description;		// 产品描述

	
	public BizCatelogInfo() {
		super();
	}

	public BizCatelogInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="产品目录名称长度必须介于 1 和 30 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=300, message="产品描述长度必须介于 0 和 300 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

}
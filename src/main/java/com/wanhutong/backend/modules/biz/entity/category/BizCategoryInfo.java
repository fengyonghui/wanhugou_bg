/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 垂直商品类目表Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCategoryInfo extends TreeEntity<BizCategoryInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizCatelogInfo catelogInfo;		// 目录分类 --大的一级分类

	private String description;		// 分类描述

	public BizCategoryInfo() {
		super();
	}

	public BizCategoryInfo(Integer id){
		super(id);
	}


	@Length(min=0, max=512, message="分类描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public BizCategoryInfo getParent() {
		return parent;
	}
	@Override
	public void setParent(BizCategoryInfo parent) {
		this.parent = parent;
	}

}
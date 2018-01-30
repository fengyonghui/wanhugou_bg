/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.cms;

import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 页面栏目设置Entity
 * @author OuyangXiutian
 * @version 2018-01-30
 */
public class BizCmsColumInfo extends DataEntity<BizCmsColumInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizCmsPageInfo pageInfo;		// biz_page_info.id
	private Integer type;		// 1. banner 2，货架；++自定义
	private String title;		// 栏目标题名称
	private BizOpShelfInfo shelfInfo;		// biz_op_shelf_info.id;  default:-1 没有货架
	private Integer setOrder;		// 栏目排序
	private String description;		// 栏目描述

	public BizCmsColumInfo() {
		super();
	}

	public BizCmsColumInfo(Integer id){
		super(id);
	}

	public BizCmsPageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(BizCmsPageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BizOpShelfInfo getShelfInfo() {
		return shelfInfo;
	}

	public void setShelfInfo(BizOpShelfInfo shelfInfo) {
		this.shelfInfo = shelfInfo;
	}

	public Integer getSetOrder() {
		return setOrder;
	}

	public void setSetOrder(Integer setOrder) {
		this.setOrder = setOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
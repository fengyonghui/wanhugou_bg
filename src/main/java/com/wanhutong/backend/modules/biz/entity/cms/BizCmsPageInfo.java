/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.cms;

import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 定义产品页面Entity
 * @author OuyangXiutian
 * @version 2018-01-30
 */
public class BizCmsPageInfo extends DataEntity<BizCmsPageInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizPlatformInfo platInfo;		// biz_platform_info.id
	private String name;		// 页面名称
	private String description;		// 页面描述

	public BizCmsPageInfo() {
		super();
	}

	public BizCmsPageInfo(Integer id){
		super(id);
	}

	public BizPlatformInfo getPlatInfo() {
		return platInfo;
	}

	public void setPlatInfo(BizPlatformInfo platInfo) {
		this.platInfo = platInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
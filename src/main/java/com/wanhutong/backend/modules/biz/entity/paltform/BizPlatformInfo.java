/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.paltform;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * Android, iOS, PC, 线下Entity
 * @author OuyangXiutian
 * @version 2017-12-21
 */
public class BizPlatformInfo extends DataEntity<BizPlatformInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 产品平台名称
	private String description;		// 产品平台描述
	private Date onlineDate;		// 上线日期
	private String lastVersion;		// 最后版本
	
	public BizPlatformInfo() {
		super();
	}

	public BizPlatformInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="产品平台名称长度必须介于 1 和 30 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=200, message="产品平台描述长度必须介于 0 和 200 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public Date getOnlineDate() {
		return onlineDate;
	}

	public void setOnlineDate(Date onlineDate) {
		this.onlineDate = onlineDate;
	}

	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}
}
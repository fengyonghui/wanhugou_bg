/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.config.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 动态配置文件Entity
 * @author Ma.Qiang
 * @version 2018-04-28
 */
public class CommonDynamicConfig extends DataEntity<CommonDynamicConfig> {
	
	private static final long serialVersionUID = 1L;
	private String confname;		// 配置文件名称
	private String content;		// 配置文件内容
	private Date createTime;		// 创建时间
	private String status;		// 状态：1:可用  0:不可用
	private String version;		// 版本号
	private Date updated;		// 更新时间
	
	public CommonDynamicConfig() {
		super();
	}

	public CommonDynamicConfig(Integer id){
		super(id);
	}

	@Length(min=1, max=256, message="配置文件名称长度必须介于 1 和 256 之间")
	public String getConfname() {
		return confname;
	}

	public void setConfname(String confname) {
		this.confname = confname;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Length(min=1, max=4, message="状态：1:可用  0:不可用长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Length(min=1, max=11, message="版本号长度必须介于 1 和 11 之间")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="更新时间不能为空")
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
}
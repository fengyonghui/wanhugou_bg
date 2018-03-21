/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.attribute;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 标签属性Entity
 * @author zx
 * @version 2018-03-21
 */
public class AttributeInfo extends DataEntity<AttributeInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 标签名称
	private String dictType;		// sys_dict.type; 非空，可选值由字典表取； 空值:此标签需要输入；
	private String level;		// 0:系统标签; 1:产品标签; 2:商品标签
	private String status;		// 1:active 0:inactive
	private User createId;		// create_id
	private Date createTime;		// create_time
	private String uVersion;		// u_version
	private User updateId;		// update_id
	private Date updateTime;		// update_time
	
	public AttributeInfo() {
		super();
	}

	public AttributeInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=255, message="标签名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@type; 非空，可选值由字典表取； 空值:此标签需要输入；长度必须介于 1 和 25 之间")
	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}
	
	@Length(min=1, max=4, message="0:系统标签; 1:产品标签; 2:商品标签长度必须介于 1 和 4 之间")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	@Length(min=1, max=4, message="1:active 0:inactive长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@NotNull(message="create_id不能为空")
	public User getCreateId() {
		return createId;
	}

	public void setCreateId(User createId) {
		this.createId = createId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="create_time不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Length(min=1, max=4, message="u_version长度必须介于 1 和 4 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
	}
	
	@NotNull(message="update_id不能为空")
	public User getUpdateId() {
		return updateId;
	}

	public void setUpdateId(User updateId) {
		this.updateId = updateId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="update_time不能为空")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.message;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 站内信发送用户类型表Entity
 * @author wangby
 * @version 2018-11-22
 */
public class BizMessageOfficeType extends DataEntity<BizMessageOfficeType> {
	
	private static final long serialVersionUID = 1L;
	private Integer messageId;		// message_id
	private Integer officeType;		// 发送用户类型
	private Integer uVersion;		// u_version

	private BizMessageInfo bizMessageInfo;
	
	public BizMessageOfficeType() {
		super();
	}

	public BizMessageOfficeType(Integer id){
		super(id);
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getOfficeType() {
		return officeType;
	}

	public void setOfficeType(Integer officeType) {
		this.officeType = officeType;
	}

	public Integer getUVersion() {
		return uVersion;
	}

	public void setUVersion(Integer uVersion) {
		this.uVersion = uVersion;
	}

	public BizMessageInfo getBizMessageInfo() {
		return bizMessageInfo;
	}

	public void setBizMessageInfo(BizMessageInfo bizMessageInfo) {
		this.bizMessageInfo = bizMessageInfo;
	}
}
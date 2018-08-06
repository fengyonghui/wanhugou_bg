/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.message;

import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 站内信关系Entity
 * @author Ma.Qiang
 * @version 2018-07-27
 */
public class BizMessageUser extends DataEntity<BizMessageUser> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// user_id
	private String messageId;		// message_id
	private String bizStatus;		// biz_status
	
	public BizMessageUser() {
		super();
	}

	public BizMessageUser(Integer id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=11, message="message_id长度必须介于 0 和 11 之间")
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	@Length(min=0, max=4, message="biz_status长度必须介于 0 和 4 之间")
	public String getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(String bizStatus) {
		this.bizStatus = bizStatus;
	}
	
}
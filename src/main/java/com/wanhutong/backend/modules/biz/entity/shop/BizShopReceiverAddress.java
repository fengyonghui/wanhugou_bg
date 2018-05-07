/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shop;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 收货地址Entity
 * @author Oy
 * @version 2018-04-10
 */
public class BizShopReceiverAddress extends DataEntity<BizShopReceiverAddress> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// sys_user.id
	private CommonLocation bizLocation;		// common_location.id
	private String receiver;		// 联系人
	private String phone;		// 联系电话
	private Integer defaultStatus;		// 1: 默认； 0：非默认
	
	public BizShopReceiverAddress() {
		super();
	}

	public BizShopReceiverAddress(Integer id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=10, message="联系人长度必须介于 1 和 10 之间")
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	@Length(min=1, max=20, message="联系电话长度必须介于 1 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public CommonLocation getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(CommonLocation bizLocation) {
		this.bizLocation = bizLocation;
	}

	public Integer getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(Integer defaultStatus) {
		this.defaultStatus = defaultStatus;
	}
}
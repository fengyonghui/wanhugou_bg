/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shop;

import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 收藏微店Entity
 * @author Oytang
 * @version 2018-04-10
 */
public class BizWeShopMark extends DataEntity<BizWeShopMark> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// sys_user.id
	private Office shopCust;		// sys_office.id
	private String shopName;		// 商铺名
	
	public BizWeShopMark() {
		super();
	}

	public BizWeShopMark(Integer id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=300, message="商铺名长度必须介于 0 和 300 之间")
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Office getShopCust() {
		return shopCust;
	}

	public void setShopCust(Office shopCust) {
		this.shopCust = shopCust;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shelf;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 货架用户中间表Entity
 * @author 张腾飞
 * @version 2018-01-11
 */
public class BizShelfUser extends DataEntity<BizShelfUser> {
	
	private static final long serialVersionUID = 1L;
	private BizOpShelfInfo shelfInfo;		// 货架ID，biz_op_shelf_info
	private User user;		// 用户ID,sys_user.id
	
	public BizShelfUser() {
		super();
	}

	public BizShelfUser(Integer id){
		super(id);
	}

	public BizOpShelfInfo getShelfInfo() {
		return shelfInfo;
	}

	public void setShelfInfo(BizOpShelfInfo shelfInfo) {
		this.shelfInfo = shelfInfo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
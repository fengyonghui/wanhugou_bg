/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.variety;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 品类与用户 关联Entity
 * @author Oy
 * @version 2018-05-31
 */
public class BizVarietyUserInfo extends DataEntity<BizVarietyUserInfo> {
	
	private static final long serialVersionUID = 1L;
	private BizVarietyInfo varietyInfo;		// 类别 biz_variety_info
	private User user;		// 用户 sys_user

	public BizVarietyUserInfo() {
		super();
	}

	public BizVarietyUserInfo(Integer id){
		super(id);
	}
	
	@NotNull(message="用户 sys_user不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}
}
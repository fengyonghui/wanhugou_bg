/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.vend;

import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 供应商收藏Entity
 * @author zx
 * @version 2018-03-02
 */
public class BizVendMark extends DataEntity<BizVendMark> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户ID
	private Office vendor;		// sys_office.id &amp; type= vendor（供应商）
	private String vendorName;		// 供应商名称
	
	public BizVendMark() {
		super();
	}

	public BizVendMark(Integer id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Office getVendor() {
		return vendor;
	}

	public void setVendor(Office vendor) {
		this.vendor = vendor;
	}

	@Length(min=0, max=300, message="供应商名称长度必须介于 0 和 300 之间")
	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	
}
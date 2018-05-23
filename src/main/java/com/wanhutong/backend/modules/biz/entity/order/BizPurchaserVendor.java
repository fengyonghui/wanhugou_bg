/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.modules.sys.entity.Office;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购商供应商关联关系Entity
 * @author ZhangTengfei
 * @version 2018-05-16
 */
public class BizPurchaserVendor extends DataEntity<BizPurchaserVendor> {
	
	private static final long serialVersionUID = 1L;
	private Office vendor;		// 供应商id sys_office.id
	private Office purchaser;		// 采购商id  sys_office.id
	
	public BizPurchaserVendor() {
		super();
	}

	public BizPurchaserVendor(Integer id){
		super(id);
	}

	public Office getVendor() {
		return vendor;
	}

	public void setVendor(Office vendor) {
		this.vendor = vendor;
	}

	public Office getPurchaser() {
		return purchaser;
	}

	public void setPurchaser(Office purchaser) {
		this.purchaser = purchaser;
	}

	
}
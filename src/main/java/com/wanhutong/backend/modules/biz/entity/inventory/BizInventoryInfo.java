/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 仓库信息表Entity
 * @author zhangtengfei
 * @version 2017-12-28
 */
public class BizInventoryInfo extends DataEntity<BizInventoryInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 仓库名称
	private String description;		// 仓库描述
	private CommonLocation bizLocation;		// common_location.id
	private Office customer;	//采购中心ID，sys_office.id
    /**
     * 用于备货单收货
     */
    private BizRequestHeader reqHeader;

	public CommonLocation getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(CommonLocation bizLocation) {
		this.bizLocation = bizLocation;
	}

	public BizInventoryInfo() {
		super();
	}

	public BizInventoryInfo(Integer id){
		super(id);
	}

	@Length(min=1, max=20, message="仓库名称长度必须介于 1 和 20 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=0, max=200, message="仓库描述长度必须介于 0 和 200 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public BizRequestHeader getReqHeader() {
		return reqHeader;
	}

	public void setReqHeader(BizRequestHeader reqHeader) {
		this.reqHeader = reqHeader;
	}
}
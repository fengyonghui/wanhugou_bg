/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.office;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 地址管理(公司+详细地址)Entity
 * @author OuyangXiutian
 * @version 2017-12-23
 */
public class SysOfficeAddress extends DataEntity<SysOfficeAddress> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// sys_office.id
	private CommonLocation addrLocation;		// common_location.id
	private Integer type;		// 1: 收货地址；2 公司地址；
	private Integer deFault;		// 1: 默认； 0：非默认
	private Integer status;		// 1:active; 0:inactive

	public SysOfficeAddress() {
		super();
	}

	public SysOfficeAddress(Integer id){
		super(id);
	}

//	@id不能为空")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public CommonLocation getAddrLocation() {
		return addrLocation;
	}

	public void setAddrLocation(CommonLocation addrLocation) {
		this.addrLocation = addrLocation;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getDeFault() {
		return deFault;
	}

	public void setDeFault(Integer deFault) {
		this.deFault = deFault;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
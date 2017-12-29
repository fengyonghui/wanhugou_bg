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
	private CommonLocation bizLocation;		// common_location.id
	private Integer type;		// 1: 收货地址；2 公司地址；
	private Integer deFault;		// 1: 默认； 0：非默认
	private Integer ohId;		//用于添加地址传参
	private String flag;		//用于传参标记

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Integer getOhId() {
		return ohId;
	}

	public void setOhId(Integer ohId) {
		this.ohId = ohId;
	}

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

	public CommonLocation getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(CommonLocation bizLocation) {
		this.bizLocation = bizLocation;
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

}
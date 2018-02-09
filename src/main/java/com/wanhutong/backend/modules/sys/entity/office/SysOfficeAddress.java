/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.entity.office;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;

/**
 * 地址管理(公司+详细地址)Entity
 * @author OuyangXiutian
 * @version 2017-12-23
 */
public class SysOfficeAddress extends DataEntity<SysOfficeAddress> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// sys_office.id
	private CommonLocation bizLocation;		// common_location.id
	private String receiver;		//联系人
	private String phone;		//联系电话
	private Integer type;		// 1: 收货地址；2 公司地址；
	private Integer deFaultStatus;		// 1: 默认； 0：非默认
	private Integer ohId;		//用于添加地址传参
	private String flag;		//用于传参标记
	private String sign;     //用于获取机构类型


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

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
	
	public Integer getDeFaultStatus() {
		return deFaultStatus;
	}
	
	public void setDeFaultStatus(Integer deFaultStatus) {
		this.deFaultStatus = deFaultStatus;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
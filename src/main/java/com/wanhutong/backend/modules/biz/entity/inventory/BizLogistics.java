/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 物流商类Entity
 * @author 张腾飞
 * @version 2018-02-21
 */
public class BizLogistics extends DataEntity<BizLogistics> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 物流商名称
	private String carrier;		// 承运人
	private Byte settlementStatus;		// 物流结算方式：1、现结；2、账期;
	private String phone;		// 物流商电话
	
	public BizLogistics() {
		super();
	}

	public BizLogistics(Integer id){
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Length(min=1, max=20, message="承运人长度必须介于 1 和 20 之间")
	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Byte getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(Byte settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	@Length(min=0, max=20, message="物流商电话长度必须介于 0 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	
}
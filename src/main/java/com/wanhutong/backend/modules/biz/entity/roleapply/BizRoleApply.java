/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.roleapply;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 零售申请表Entity
 * @author wangby
 * @version 2018-10-30
 */
public class BizRoleApply extends DataEntity<BizRoleApply> {
	
	private static final long serialVersionUID = 1L;
	private Integer applyOfficeId;		// 申请人的officeId  sys_office.id
	private Integer roleId;		// 当前角色
	private Integer applyRoleId;		// 申请变更Id
	private Integer applyStatus;		// 审核状态 0：未审核1：已审核
	private Integer applyResult;		// 审批结果 1同意 2驳回
	private String realName;		// 真实姓名
	private String idCardNumber;		// 身份证号
	private String creditCardNumber;		// 银行卡号
	private String depositBank;		// 开户银行
	
	public BizRoleApply() {
		super();
	}

	public BizRoleApply(Integer id){
		super(id);
	}

	public Integer getApplyOfficeId() {
		return applyOfficeId;
	}

	public void setApplyOfficeId(Integer applyOfficeId) {
		this.applyOfficeId = applyOfficeId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getApplyRoleId() {
		return applyRoleId;
	}

	public void setApplyRoleId(Integer applyRoleId) {
		this.applyRoleId = applyRoleId;
	}

	public Integer getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(Integer applyStatus) {
		this.applyStatus = applyStatus;
	}
	
	public Integer getApplyResult() {
		return applyResult;
	}

	public void setApplyResult(Integer applyResult) {
		this.applyResult = applyResult;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getDepositBank() {
		return depositBank;
	}

	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}
	
}
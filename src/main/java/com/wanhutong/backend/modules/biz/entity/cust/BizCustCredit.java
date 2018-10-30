/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.cust;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.math.BigDecimal;

/**
 * 用户钱包Entity
 * @author Ouyang
 * @version 2018-03-09
 */
public class BizCustCredit extends DataEntity<BizCustCredit> {
	
	private static final long serialVersionUID = 1L;
	private Office customer;		// sys_office.id &amp; type = customer
	private String payPwd;		// 支付密码
	private String level;		// 1: 普通会员； 2:合伙会员
	private BigDecimal credit = new BigDecimal(0);//客户信用值
	private BigDecimal wallet = new BigDecimal(0);//客户钱包
	private BigDecimal money = new BigDecimal(0);//万户币
	private String aliAccount;		// 支付宝账号
	private String aliName;		// 支付宝用户姓名

	private String custFalg;	//标识符

	private String CgsType;	//查询类型

	public BizCustCredit() {
		super();
	}

	public BizCustCredit(Integer id){
		super(id);
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public String getPayPwd() {
		return payPwd;
	}

	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getWallet() {
		return wallet;
	}

	public void setWallet(BigDecimal wallet) {
		this.wallet = wallet;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getAliAccount() {
		return aliAccount;
	}

	public void setAliAccount(String aliAccount) {
		this.aliAccount = aliAccount;
	}

	public String getAliName() {
		return aliName;
	}

	public void setAliName(String aliName) {
		this.aliName = aliName;
	}

	public String getCgsType() {
		return CgsType;
	}

	public void setCgsType(String cgsType) {
		CgsType = cgsType;
	}

	public String getCustFalg() {
		return custFalg;
	}

	public void setCustFalg(String custFalg) {
		this.custFalg = custFalg;
	}
}
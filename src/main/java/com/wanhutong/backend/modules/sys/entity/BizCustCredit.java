package com.wanhutong.backend.modules.sys.entity;

import java.math.BigDecimal;
import com.wanhutong.backend.common.persistence.DataEntity;
/**
 * * 
* <p>Title: BizCustCredit</p>
* <p>Description: 用户钱包</p>
* <p>Company: WHT</p> 
* @date 2018年1月12日
 */

public class BizCustCredit extends DataEntity<BizCustCredit> {

	private static final long serialVersionUID = 1L;

	private Integer officeId;
	
	private String payPwd;
	
	private String level;//'1: 普通会员； 2:合伙会员'
	
	private BigDecimal credit = new BigDecimal(0);//客户信用值
	
	private BigDecimal wallet = new BigDecimal(0);//客户钱包
	
	private BigDecimal money = new BigDecimal(0);//万户币
	
	private String status;
	
	public Integer getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Integer officeId) {
		super.setId(officeId);
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}

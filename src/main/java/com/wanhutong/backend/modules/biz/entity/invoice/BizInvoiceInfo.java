/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.invoice;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;

/**
 * 记录客户发票信息(发票开户行,税号)Entity
 * @author OuyangXiutian
 * @version 2017-12-29
 */
public class BizInvoiceInfo extends DataEntity<BizInvoiceInfo> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// sys_office.id
	private String invName;		// 发票抬头或名称
	private String taxNo;		// 税号
	private String bankName;	// 开户行
	private CommonLocation bizLocation;		// 邮寄地址common_location.id
	private String tel;		// 电话
	private String account;		// 发票账号
	
	public BizInvoiceInfo() {
		super();
	}

	public BizInvoiceInfo(Integer id){
		super(id);
	}
	
	public Office getOffice() {
		return office;
	}
	
	public void setOffice(Office office) {
		this.office = office;
	}
	
	public String getInvName() {
		return invName;
	}
	
	public void setInvName(String invName) {
		this.invName = invName;
	}
	
	public String getTaxNo() {
		return taxNo;
	}
	
	public void setTaxNo(String taxNo) {
		this.taxNo = taxNo;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public CommonLocation getBizLocation() {
		return bizLocation;
	}
	
	public void setBizLocation(CommonLocation bizLocation) {
		this.bizLocation = bizLocation;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
}
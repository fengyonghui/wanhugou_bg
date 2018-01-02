/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.invoice;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Office;

/**
 * 发票抬头，发票内容，发票类型Entity
 * @author OuyangXiutian
 * @version 2017-12-29
 */
public class BizInvoiceHeader extends DataEntity<BizInvoiceHeader> {
	
	private static final long serialVersionUID = 1L;
	private Office custOff;		// sys_office.id &amp; type = customer
	private String invTitle;		// 发票抬头
	private Integer invType;		// 发票类型 1: 普票 2:专票
	private String invContent;		// 发票内容
	private Double invTotal;		// 发票数额
	
	public BizInvoiceHeader() {
		super();
	}

	public BizInvoiceHeader(Integer id){
		super(id);
	}
	
	public Office getCustOff() {
		return custOff;
	}
	
	public void setCustOff(Office custOff) {
		this.custOff = custOff;
	}
	
	public String getInvTitle() {
		return invTitle;
	}
	
	public void setInvTitle(String invTitle) {
		this.invTitle = invTitle;
	}
	
	public Integer getInvType() {
		return invType;
	}
	
	public void setInvType(Integer invType) {
		this.invType = invType;
	}
	
	public String getInvContent() {
		return invContent;
	}
	
	public void setInvContent(String invContent) {
		this.invContent = invContent;
	}
	
	public Double getInvTotal() {
		return invTotal;
	}
	
	public void setInvTotal(Double invTotal) {
		this.invTotal = invTotal;
	}
}
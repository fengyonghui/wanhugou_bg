/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.invoice;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.util.List;

/**
 * 发票抬头，发票内容，发票类型Entity
 * @author OuyangXiutian
 * @version 2017-12-29
 */
public class BizInvoiceHeader extends DataEntity<BizInvoiceHeader> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// sys_office.id &amp; type = customer
	private String invTitle;		// 发票抬头
	private Integer invType;		// 发票类型 1: 普票 2:专票
	private String invContent;		// 发票内容
	private Double invTotal;		// 发票数额

	private List<BizInvoiceDetail> bizInvoiceDetailList;	//当前发票抬头下有多少发票详情

	
	public BizInvoiceHeader() {
		super();
	}

	public BizInvoiceHeader(Integer id){
		super(id);
	}
	
	public Office getOffice() {
		return office;
	}
	
	public void setOffice(Office office) {
		this.office = office;
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

	public List<BizInvoiceDetail> getBizInvoiceDetailList() {
		return bizInvoiceDetailList;
	}

	public void setBizInvoiceDetailList(List<BizInvoiceDetail> bizInvoiceDetailList) {
		this.bizInvoiceDetailList = bizInvoiceDetailList;
	}
}
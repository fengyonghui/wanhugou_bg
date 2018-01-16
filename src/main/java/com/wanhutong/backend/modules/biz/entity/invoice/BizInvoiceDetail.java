/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.invoice;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;

import java.util.List;

/**
 * 发票详情(发票行号,order_header.id)Entity
 * @author OuyangXiutian
 * @version 2017-12-29
 */
public class BizInvoiceDetail extends DataEntity<BizInvoiceDetail> {
	
	private static final long serialVersionUID = 1L;
	private Integer lineNo;		// 发票行号
	private BizInvoiceHeader invoiceHeader;	//发票 biz_invoice_header.id
	private BizOrderHeader orderHead;		// biz_order_header.id
	private Double invAmt;		// 发票数额；一般等于total_order
	
	private Integer maxLineNo;		//用于最大的行号
	private List<BizOrderHeader> orderHeaderList;	//当前发票下的订单
	
	public List<BizOrderHeader> getOrderHeaderList() {
		return orderHeaderList;
	}
	
	public void setOrderHeaderList(List<BizOrderHeader> orderHeaderList) {
		this.orderHeaderList = orderHeaderList;
	}
	
	public Integer getMaxLineNo() {
		return maxLineNo;
	}
	
	public void setMaxLineNo(Integer maxLineNo) {
		this.maxLineNo = maxLineNo;
	}
	
	public BizInvoiceDetail() {
		super();
	}

	public BizInvoiceDetail(Integer id){
		super(id);
	}
	
	public BizOrderHeader getOrderHead() {
		return orderHead;
	}
	
	public void setOrderHead(BizOrderHeader orderHead) {
		this.orderHead = orderHead;
	}
	
	public Integer getLineNo() {
		return lineNo;
	}
	
	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}
	
	public Double getInvAmt() {
		return invAmt;
	}
	
	public void setInvAmt(Double invAmt) {
		this.invAmt = invAmt;
	}

	public BizInvoiceHeader getInvoiceHeader() {
		return invoiceHeader;
	}

	public void setInvoiceHeader(BizInvoiceHeader invoiceHeader) {
		this.invoiceHeader = invoiceHeader;
	}
}
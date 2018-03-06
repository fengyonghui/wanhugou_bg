/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;

/**
 * 发货单和订单详情关系Entity
 * @author 张腾飞
 * @version 2018-03-06
 */
public class BizDetailInvoice extends DataEntity<BizDetailInvoice> {
	
	private static final long serialVersionUID = 1L;
	private BizInvoice invoice;		// 发货单ID，biz_invoice.id
	private BizOrderHeader orderHeader;		// 销售单ID，biz_order_header.id
	private BizRequestHeader requestHeader;		// 备货单详情ID，biz_request_header.id
	
	public BizDetailInvoice() {
		super();
	}

	public BizDetailInvoice(Integer id){
		super(id);
	}


	public BizInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(BizInvoice invoice) {
		this.invoice = invoice;
	}

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}
}
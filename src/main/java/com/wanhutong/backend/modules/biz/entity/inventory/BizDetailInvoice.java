/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;

/**
 * 发货单和订单详情关系Entity
 * @author 张腾飞
 * @version 2018-03-06
 */
public class BizDetailInvoice extends DataEntity<BizDetailInvoice> {
	
	private static final long serialVersionUID = 1L;
	private BizInvoice invoice;		// 发货单ID，biz_invoice.id
	private BizOrderDetail orderDetail;		// 销售单详情ID，biz_order_detail.id
	private BizRequestDetail requestDetail;		// 备货单详情ID，biz_request_detail.id
	
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

	public BizOrderDetail getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(BizOrderDetail orderDetail) {
		this.orderDetail = orderDetail;
	}

	public BizRequestDetail getRequestDetail() {
		return requestDetail;
	}

	public void setRequestDetail(BizRequestDetail requestDetail) {
		this.requestDetail = requestDetail;
	}
}
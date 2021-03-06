/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;


import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;

/**
 * 订单备货单出库关系Entity
 * @author ZhangTengfei
 * @version 2018-09-20
 */
public class BizInventoryOrderRequest extends DataEntity<BizInventoryOrderRequest> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 库存
	 */
	private BizInventorySku invSku;

	/**
	 * 订单详情
	 */
	private BizOrderDetail orderDetail;

	/**
	 * 备货单详情
	 */
	private BizRequestDetail requestDetail;

	/**
	 * 调拨单详情
	 */
	private BizSkuTransferDetail transferDetail;

	/**
	 * 出库数量
	 */
	private Integer outQty;
	
	public BizInventoryOrderRequest() {
		super();
	}

	public BizInventoryOrderRequest(Integer id){
		super(id);
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

	public BizInventorySku getInvSku() {
		return invSku;
	}

	public void setInvSku(BizInventorySku invSku) {
		this.invSku = invSku;
	}

	public BizSkuTransferDetail getTransferDetail() {
		return transferDetail;
	}

	public void setTransferDetail(BizSkuTransferDetail transferDetail) {
		this.transferDetail = transferDetail;
	}

	public Integer getOutQty() {
		return outQty;
	}

	public void setOutQty(Integer outQty) {
		this.outQty = outQty;
	}
}
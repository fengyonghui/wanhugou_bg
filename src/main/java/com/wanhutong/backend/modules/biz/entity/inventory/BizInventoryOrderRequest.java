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
	 * 订单详情
	 */
	private BizOrderDetail orderDetail;

	/**
	 * 备货单详情
	 */
	private BizRequestDetail requestDetail;
	
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
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import java.io.Serializable;

/**
 * 供货记录表Entity
 * @author 张腾飞
 * @version 2018-01-03
 */
public class BizOutTreasuryEntity implements Serializable {

	/**
	 * 订单详情ID
	 */
	private Integer orderDetailId;

	/**
	 * 备货单详情ID
	 */
	private Integer reqDetailId;

	/**
	 * 库存ID
	 */
	private Integer invSkuId;

	/**
	 * 本次出库数量
	 */
	private Integer outQty;

	/**
	 * 出库单
	 */
	private String sendNo;

	/**
	 * 库存版本
	 */
	private Integer uVersion;

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getReqDetailId() {
		return reqDetailId;
	}

	public void setReqDetailId(Integer reqDetailId) {
		this.reqDetailId = reqDetailId;
	}

	public Integer getInvSkuId() {
		return invSkuId;
	}

	public void setInvId(Integer invSkuId) {
		this.invSkuId = invSkuId;
	}

	public Integer getOutQty() {
		return outQty;
	}

	public void setOutQty(Integer outQty) {
		this.outQty = outQty;
	}

	public String getSendNo() {
		return sendNo;
	}

	public void setSendNo(String sendNo) {
		this.sendNo = sendNo;
	}

	public Integer getuVersion() {
		return uVersion;
	}

	public void setuVersion(Integer uVersion) {
		this.uVersion = uVersion;
	}
}
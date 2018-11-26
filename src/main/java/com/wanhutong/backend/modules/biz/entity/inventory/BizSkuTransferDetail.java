/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.User;

import java.util.Date;
import java.util.List;

/**
 * 库存调拨详情Entity
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
public class BizSkuTransferDetail extends DataEntity<BizSkuTransferDetail> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 调拨单ID transfer.id
	 */
	private BizSkuTransfer transfer;

	/**
	 * 商品ID biz_sku_info.id
	 */
	private BizSkuInfo skuInfo;

	/**
	 * 商品调拨数量
	 */
	private Integer transQty;

	/**
	 * 出库人
	 */
	private User fromInvOp;

	/**
	 * 出库时间
	 */
	private Date fromInvOpTime;

	/**
	 * 入库人
	 */
	private User toInvOp;

	/**
	 * 入库时间
	 */
	private Date toInvOpTime;

	/**
	 * 已出库数量
	 */
	private Integer outQty;

	/**
	 * 已入库数量
	 */
	private Integer inQty;

	/**
	 * 已卖出的数量
	 */
	private Integer sentQty;

	/**
	 * 详情商品颜色
	 */
	private String color;

	/**
	 * 详情商品尺寸
	 */
	private String size;

	/**
	 * 出库的备货单详情
	 */
	private List<BizRequestDetail> requestDetailList;

	/**
	 * 库存
	 */
	private BizInventorySku inventorySku;

	public BizSkuTransfer getTransfer() {
		return transfer;
	}

	public void setTransfer(BizSkuTransfer transfer) {
		this.transfer = transfer;
	}

	public BizSkuTransferDetail() {
		super();
	}

	public BizSkuTransferDetail(Integer id){
		super(id);
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public Integer getTransQty() {
		return transQty;
	}

	public void setTransQty(Integer transQty) {
		this.transQty = transQty;
	}
	
	public User getFromInvOp() {
		return fromInvOp;
	}

	public void setFromInvOp(User fromInvOp) {
		this.fromInvOp = fromInvOp;
	}
	
	public Date getFromInvOpTime() {
		return fromInvOpTime;
	}

	public void setFromInvOpTime(Date fromInvOpTime) {
		this.fromInvOpTime = fromInvOpTime;
	}
	
	public User getToInvOp() {
		return toInvOp;
	}

	public void setToInvOp(User toInvOp) {
		this.toInvOp = toInvOp;
	}
	
	public Date getToInvOpTime() {
		return toInvOpTime;
	}

	public void setToInvOpTime(Date toInvOpTime) {
		this.toInvOpTime = toInvOpTime;
	}
	
	public Integer getOutQty() {
		return outQty;
	}

	public void setOutQty(Integer outQty) {
		this.outQty = outQty;
	}
	
	public Integer getInQty() {
		return inQty;
	}

	public void setInQty(Integer inQty) {
		this.inQty = inQty;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<BizRequestDetail> getRequestDetailList() {
		return requestDetailList;
	}

	public void setRequestDetailList(List<BizRequestDetail> requestDetailList) {
		this.requestDetailList = requestDetailList;
	}

	public Integer getSentQty() {
		return sentQty;
	}

	public void setSentQty(Integer sentQty) {
		this.sentQty = sentQty;
	}

	public BizInventorySku getInventorySku() {
		return inventorySku;
	}

	public void setInventorySku(BizInventorySku inventorySku) {
		this.inventorySku = inventorySku;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 供货记录表Entity
 * @author 张腾飞
 * @version 2018-01-03
 */
public class BizSendGoodsRecord extends DataEntity<BizSendGoodsRecord> {
	
	private static final long serialVersionUID = 1L;
	private BizSkuInfo skuInfo;		// 商品ID，biz_sku_info.id
	private BizInventoryInfo invInfo;		//发货仓库，biz_inventory_info.id
	private BizOrderHeader bizOrderHeader;		// order_id
	private BizRequestHeader bizRequestHeader;	//request_id
	private String orderNum;		//订单号
	private Integer sendNum;		// 供货数量
	private Office customer;		// 采购商ID sys_office.id &amp;  type='customer'
	private Date sendDate;		// 供货时间
	private Integer bizStatus;		//供货方状态    0：采购中心 ，1：供货中心
	private BizRequestDetail bizRequestDetail;
	private BizOrderDetail bizOrderDetail;
	private List<BizSendGoodsRecord> bizSendGoodsRecordList;

	public BizSendGoodsRecord() {
		super();
	}

	public BizSendGoodsRecord(Integer id){
		super(id);
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public BizOrderHeader getBizOrderHeader() {
		return bizOrderHeader;
	}

	public void setBizOrderHeader(BizOrderHeader bizOrderHeader) {
		this.bizOrderHeader = bizOrderHeader;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="供货时间不能为空")
	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public BizRequestHeader getBizRequestHeader() {
		return bizRequestHeader;
	}

	public void setBizRequestHeader(BizRequestHeader bizRequestHeader) {
		this.bizRequestHeader = bizRequestHeader;
	}

	public BizOrderDetail getBizOrderDetail() {
		return bizOrderDetail;
	}

	public void setBizOrderDetail(BizOrderDetail bizOrderDetail) {
		this.bizOrderDetail = bizOrderDetail;
	}

	public BizRequestDetail getBizRequestDetail() {
		return bizRequestDetail;
	}

	public void setBizRequestDetail(BizRequestDetail bizRequestDetail) {
		this.bizRequestDetail = bizRequestDetail;
	}

	public List<BizSendGoodsRecord> getBizSendGoodsRecordList() {
		return bizSendGoodsRecordList;
	}

	public void setBizSendGoodsRecordList(List<BizSendGoodsRecord> bizSendGoodsRecordList) {
		this.bizSendGoodsRecordList = bizSendGoodsRecordList;
	}

	public BizInventoryInfo getInvInfo() {
		return invInfo;
	}

	public void setInvInfo(BizInventoryInfo invInfo) {
		this.invInfo = invInfo;
	}

}
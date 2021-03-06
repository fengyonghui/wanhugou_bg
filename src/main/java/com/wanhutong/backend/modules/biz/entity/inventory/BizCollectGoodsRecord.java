/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import java.util.List;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 收货记录表Entity
 * @author 张腾飞
 * @version 2018-01-03
 */
public class BizCollectGoodsRecord extends DataEntity<BizCollectGoodsRecord> {
	
	private static final long serialVersionUID = 1L;
	private BizInventoryInfo invInfo;		// 仓库ID，biz_inventory_info.id
	private Integer invOldNum;		//收货之前的库存数
	private BizSkuInfo skuInfo;		// 商品ID，biz_sku_info.id
	private BizOrderHeader bizOrderHeader;		// 销售单ID，biz_order_header.id
	private Byte orderType;		//类型 0：备货单；1：调拨单
	private BizRequestHeader bizRequestHeader;	//备货单ID, biz_request_header.id
	private String orderNum;		//订单号
	private Integer receiveNum;		// 收货数量
	private Office vender;		// 供应商ID sys_office.id &amp;  type=vend
	private Office customer;		//采购商ID sys_office.id &amp;  type=custmer
	private Date receiveDate;		// 收货时间
	private BizRequestDetail bizRequestDetail;
	private BizOrderDetail bizOrderDetail;
	private List<BizCollectGoodsRecord> bizCollectGoodsRecordList;

	/**
	 * 入库单号
	 */
	private String collectNo;

	/**
	 * 变更的数量
	 * */
	private Integer changeNumber;
	/**
	 * 变更记录
	 * */
	private String changeState;
	/**
	 * 变更记录 列表查询字段
	 * */
	private String skuInfoName;
	private String skuInfoPartNo;
	private String invInfoName;
	private Date createDateStart;
	private Date createDateEnd;
	private String skuInfoItemNo;

	/**
	 * 用于页面判断
	 */
	private String bizStatu;

	/**
	 * 采购中心名
	 */
	private String centName;

	/**
	 * 供应商名
	 */
	private String vendorName;

	/**
	 * 库存
	 */
	private BizInventorySku inventorySku;

	/**
	 * 出库数量
	 */
	private Integer outQty;

	/**
	 * 备货单
	 */
	private BizRequestHeader requestHeader;

	/**
	 * 调拨单
	 */
	private BizSkuTransfer bizSkuTransfer;

	/**
	 * 调拨单详情
	 */
	private BizSkuTransferDetail transferDetail;

	/**
	 * 收货开始时间
	 */
	private Date receiveStartTime;

	/**
	 * 收货结束时间
	 */
	private Date receiveEndTime;

	public BizCollectGoodsRecord() {
		super();
	}

	public BizCollectGoodsRecord(Integer id){
		super(id);
	}

	public BizInventoryInfo getInvInfo() {
		return invInfo;
	}

	public void setInvInfo(BizInventoryInfo invInfo) {
		this.invInfo = invInfo;
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

	public BizRequestHeader getBizRequestHeader() {
		return bizRequestHeader;
	}

	public void setBizRequestHeader(BizRequestHeader bizRequestHeader) {
		this.bizRequestHeader = bizRequestHeader;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getReceiveNum() {
		return receiveNum;
	}

	public void setReceiveNum(Integer receiveNum) {
		this.receiveNum = receiveNum;
	}

	public Office getVender() {
		return vender;
	}

	public void setVender(Office vender) {
		this.vender = vender;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public BizRequestDetail getBizRequestDetail() {
		return bizRequestDetail;
	}

	public void setBizRequestDetail(BizRequestDetail bizRequestDetail) {
		this.bizRequestDetail = bizRequestDetail;
	}

	public BizOrderDetail getBizOrderDetail() {
		return bizOrderDetail;
	}

	public void setBizOrderDetail(BizOrderDetail bizOrderDetail) {
		this.bizOrderDetail = bizOrderDetail;
	}

	public List<BizCollectGoodsRecord> getBizCollectGoodsRecordList() {
		return bizCollectGoodsRecordList;
	}

	public void setBizCollectGoodsRecordList(List<BizCollectGoodsRecord> bizCollectGoodsRecordList) {
		this.bizCollectGoodsRecordList = bizCollectGoodsRecordList;
	}

	public Integer getInvOldNum() {
		return invOldNum;
	}

	public void setInvOldNum(Integer invOldNum) {
		this.invOldNum = invOldNum;
	}

	public Integer getChangeNumber() {
		return changeNumber;
	}

	public void setChangeNumber(Integer changeNumber) {
		this.changeNumber = changeNumber;
	}

	public String getChangeState() {
		return changeState;
	}

	public void setChangeState(String changeState) {
		this.changeState = changeState;
	}

	public String getSkuInfoName() {
		return skuInfoName;
	}

	public void setSkuInfoName(String skuInfoName) {
		this.skuInfoName = skuInfoName;
	}

	public String getSkuInfoPartNo() {
		return skuInfoPartNo;
	}

	public void setSkuInfoPartNo(String skuInfoPartNo) {
		this.skuInfoPartNo = skuInfoPartNo;
	}

	public String getInvInfoName() {
		return invInfoName;
	}

	public void setInvInfoName(String invInfoName) {
		this.invInfoName = invInfoName;
	}

	public Date getCreateDateStart() {
		return createDateStart;
	}

	public void setCreateDateStart(Date createDateStart) {
		this.createDateStart = createDateStart;
	}

	public Date getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(Date createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public String getSkuInfoItemNo() {
		return skuInfoItemNo;
	}

	public void setSkuInfoItemNo(String skuInfoItemNo) {
		this.skuInfoItemNo = skuInfoItemNo;
	}

	public String getBizStatu() {
		return bizStatu;
	}

	public void setBizStatu(String bizStatu) {
		this.bizStatu = bizStatu;
	}

	public String getCollectNo() {
		return collectNo;
	}

	public void setCollectNo(String collectNo) {
		this.collectNo = collectNo;
	}

	public String getCentName() {
		return centName;
	}

	public void setCentName(String centName) {
		this.centName = centName;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public BizInventorySku getInventorySku() {
		return inventorySku;
	}

	public void setInventorySku(BizInventorySku inventorySku) {
		this.inventorySku = inventorySku;
	}

	public Integer getOutQty() {
		return outQty;
	}

	public void setOutQty(Integer outQty) {
		this.outQty = outQty;
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}

	public Byte getOrderType() {
		return orderType;
	}

	public void setOrderType(Byte orderType) {
		this.orderType = orderType;
	}

	public BizSkuTransfer getBizSkuTransfer() {
		return bizSkuTransfer;
	}

	public void setBizSkuTransfer(BizSkuTransfer bizSkuTransfer) {
		this.bizSkuTransfer = bizSkuTransfer;
	}

	public BizSkuTransferDetail getTransferDetail() {
		return transferDetail;
	}

	public void setTransferDetail(BizSkuTransferDetail transferDetail) {
		this.transferDetail = transferDetail;
	}
	public enum OrderType {
		RE(0),
		TR(1),
		;
		private Integer type;

		OrderType(Integer type){this.type = type;}

		public Integer getType() {
			return type;
		}
	}

	public Date getReceiveStartTime() {
		return receiveStartTime;
	}

	public void setReceiveStartTime(Date receiveStartTime) {
		this.receiveStartTime = receiveStartTime;
	}

	public Date getReceiveEndTime() {
		return receiveEndTime;
	}

	public void setReceiveEndTime(Date receiveEndTime) {
		this.receiveEndTime = receiveEndTime;
	}
}
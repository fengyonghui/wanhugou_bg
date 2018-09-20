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
	private Double changeNumber;
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

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="收货时间不能为空")
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

	public Double getChangeNumber() {
		return changeNumber;
	}

	public void setChangeNumber(Double changeNumber) {
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
}
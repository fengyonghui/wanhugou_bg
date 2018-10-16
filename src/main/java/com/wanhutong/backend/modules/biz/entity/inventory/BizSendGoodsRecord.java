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
	private Integer invOldNum;		//发货之前的库存数
	private BizOrderHeader bizOrderHeader;		// order_id
	private BizRequestHeader bizRequestHeader;	//request_id
	private String orderNum;		//订单号
	private Integer sendNum;		// 供货数量
	private Office customer;		// 采购商ID sys_office.id &amp;  type='customer'
	private String imgUrl;		//物流信息图
	private Double valuePrice;		//货值
	private Double operation;		//操作费
	private Double freight;		//运费
	private String carrier;		// 承运人
	private Byte settlementStatus;		// 物流结算方式：1、现结；2、账期;
	private Date sendDate;		// 供货时间
	private Integer bizStatus;		//供货方状态    0：采购中心 ，1：供货中心
	private BizLogistics bizLogistics;		//物流商
	private BizRequestDetail bizRequestDetail;
	private BizOrderDetail bizOrderDetail;
	private List<BizSendGoodsRecord> bizSendGoodsRecordList;
	/**
	 * 是否生效
	 */
	private Integer effect;

	/**
	 * 出库单号
	 */
	private String sendNo;

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Double getValuePrice() {
		return valuePrice;
	}

	public void setValuePrice(Double valuePrice) {
		this.valuePrice = valuePrice;
	}

	public Double getOperation() {
		return operation;
	}

	public void setOperation(Double operation) {
		this.operation = operation;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	@Length(min=1, max=20, message="承运人长度必须介于 1 和 20 之间")
	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public Byte getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(Byte settlementStatus) {
		this.settlementStatus = settlementStatus;
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

	public BizLogistics getBizLogistics() {
		return bizLogistics;
	}

	public void setBizLogistics(BizLogistics bizLogistics) {
		this.bizLogistics = bizLogistics;
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

	public Integer getInvOldNum() {
		return invOldNum;
	}

	public void setInvOldNum(Integer invOldNum) {
		this.invOldNum = invOldNum;
	}

	public Integer getEffect() {
		return effect;
	}

	public void setEffect(Integer effect) {
		this.effect = effect;
	}

	public String getSendNo() {
		return sendNo;
	}

	public void setSendNo(String sendNo) {
		this.sendNo = sendNo;
	}
}
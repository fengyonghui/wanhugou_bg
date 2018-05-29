/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 发货单Entity
 * @author 张腾飞
 * @version 2018-03-05
 */
public class BizInvoice extends DataEntity<BizInvoice> {
	
	private static final long serialVersionUID = 1L;
	private String sendNumber;		// 发货单号-由系统生成；唯一
	private Office logistics;		// 物流商ID，biz_logistics.id
	private String imgUrl;		// 物流信息图(img_server+img_path)
	private Double valuePrice;		// 货值
	private Double operation;		// 操作费
	private Double freight;		// 运费
	private String carrier;		// 承运人
	private Integer settlementStatus;		// 物流结算方式：1、现结；2、账期;
	private Integer bizStatus;		//发货地：0、采购中心； 1、供货中心；
	private Integer ship;		//发货单来源：0、销售订单；1、备货单
	private Date sendDate;		// 供货时间
	private List<BizOrderHeader> orderHeaderList;
	private List<BizRequestHeader> requestHeaderList;
	private String requestHeaders;		//发货备货单ID
	private String orderHeaders;		//发货订单ID
	private String sendNums;		//供货数
	private String reqDetails;		//发货备货单详情ID
	private String ordDetails;		//发货订单详情ID
	private String trackingNumber;		//物流单号

	public BizInvoice() {
		super();
	}

	public BizInvoice(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="发货单号-由系统生成；唯一长度必须介于 1 和 30 之间")
	public String getSendNumber() {
		return sendNumber;
	}

	public void setSendNumber(String sendNumber) {
		this.sendNumber = sendNumber;
	}

	@Length(min=0, max=100, message="物流信息图(img_server+img_path)长度必须介于 0 和 100 之间")
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	@Length(min=1, max=20, message="承运人长度必须介于 1 和 20 之间")
	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="供货时间不能为空")
	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Office getLogistics() {
		return logistics;
	}

	public void setLogistics(Office logistics) {
		this.logistics = logistics;
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

	public Integer getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(Integer settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public List<BizOrderHeader> getOrderHeaderList() {
		return orderHeaderList;
	}

	public void setOrderHeaderList(List<BizOrderHeader> orderHeaderList) {
		this.orderHeaderList = orderHeaderList;
	}

	public List<BizRequestHeader> getRequestHeaderList() {
		return requestHeaderList;
	}

	public void setRequestHeaderList(List<BizRequestHeader> requestHeaderList) {
		this.requestHeaderList = requestHeaderList;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public String getOrderHeaders() {
		return orderHeaders;
	}

	public void setOrderHeaders(String orderHeaders) {
		this.orderHeaders = orderHeaders;
	}

	public String getSendNums() {
		return sendNums;
	}

	public void setSendNums(String sendNums) {
		this.sendNums = sendNums;
	}

	public String getReqDetails() {
		return reqDetails;
	}

	public void setReqDetails(String reqDetails) {
		this.reqDetails = reqDetails;
	}

	public String getOrdDetails() {
		return ordDetails;
	}

	public void setOrdDetails(String ordDetails) {
		this.ordDetails = ordDetails;
	}

	public Integer getShip() {
		return ship;
	}

	public void setShip(Integer ship) {
		this.ship = ship;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.Map;

/**
 * 销售采购备货中间表Entity
 * @author 张腾飞
 * @version 2018-01-09
 */
public class BizPoOrderReq extends DataEntity<BizPoOrderReq> {

	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// 销售订单
	private BizRequestHeader requestHeader;		// 备货单
	private BizPoHeader poHeader;		// 采购单
	private Integer poLineNo;
	private Integer soLineNo;
	private Integer soQty;
	private Byte soType;
	private Integer soId;
	private BizOrderDetail orderDetail;
	private BizRequestDetail requestDetail;
	private String orderNumStr;
	private Integer isPrew;	//1.是预览数据 0.正常数据



	
	public BizPoOrderReq() {
		super();
	}

	public BizPoOrderReq(Integer id){
		super(id);
	}

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}

	public BizPoHeader getPoHeader() {
		return poHeader;
	}

	public void setPoHeader(BizPoHeader poHeader) {
		this.poHeader = poHeader;
	}

	public Byte getSoType() {
		return soType;
	}

	public void setSoType(Byte soType) {
		this.soType = soType;
	}

	public Integer getPoLineNo() {
		return poLineNo;
	}

	public void setPoLineNo(Integer poLineNo) {
		this.poLineNo = poLineNo;
	}

	public Integer getSoLineNo() {
		return soLineNo;
	}

	public void setSoLineNo(Integer soLineNo) {
		this.soLineNo = soLineNo;
	}

	public Integer getSoQty() {
		return soQty;
	}

	public void setSoQty(Integer soQty) {
		this.soQty = soQty;
	}

	public Integer getSoId() {
		return soId;
	}

	public void setSoId(Integer soId) {
		this.soId = soId;
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

	public String getOrderNumStr() {
		return orderNumStr;
	}

	public void setOrderNumStr(String orderNumStr) {
		this.orderNumStr = orderNumStr;
	}

	public Integer getIsPrew() {
		return isPrew;
	}

	public void setIsPrew(Integer isPrew) {
		this.isPrew = isPrew;
	}
}
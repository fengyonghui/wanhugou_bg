/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 订单详情(销售订单)Entity
 * @author OuyangXiutian
 * @version 2017-12-22
 */
public class BizOrderDetail extends DataEntity<BizOrderDetail> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// biz_order_header.id
	private String lineNo;		// 订单详情行号
	private String pLineNo;		// bom产品 kit
	private String skuNo;		// biz_sku_info.id
	private String partNo;		// 商品编号
	private String skuName;		// 商品名称
	private String unitPrice;		// 商品单价
	private String ordQty;		// 采购数量
	
	public BizOrderDetail() {
		super();
	}

	public BizOrderDetail(Integer id){
		super(id);
	}

//	@id长度必须介于 1 和 11 之间")
	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	@Length(min=1, max=11, message="订单详情行号长度必须介于 1 和 11 之间")
	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}
	
	@Length(min=0, max=11, message="bom产品 kit长度必须介于 0 和 11 之间")
	public String getPLineNo() {
		return pLineNo;
	}

	public void setPLineNo(String pLineNo) {
		this.pLineNo = pLineNo;
	}
	
//	@id长度必须介于 1 和 11 之间")
	public String getSkuNo() {
		return skuNo;
	}

	public void setSkuNo(String skuNo) {
		this.skuNo = skuNo;
	}
	
	@Length(min=0, max=30, message="商品编号长度必须介于 0 和 30 之间")
	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	
	@Length(min=1, max=30, message="商品名称长度必须介于 1 和 30 之间")
	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}
	
	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	@Length(min=1, max=11, message="采购数量长度必须介于 1 和 11 之间")
	public String getOrdQty() {
		return ordQty;
	}

	public void setOrdQty(String ordQty) {
		this.ordQty = ordQty;
	}

	public String getpLineNo() {
		return pLineNo;
	}

	public void setpLineNo(String pLineNo) {
		this.pLineNo = pLineNo;
	}
}
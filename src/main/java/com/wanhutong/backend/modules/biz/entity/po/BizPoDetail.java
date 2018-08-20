/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购订单信息信息Entity
 * @author liuying
 * @version 2017-12-30
 */
public class BizPoDetail extends DataEntity<BizPoDetail> {
	
	private static final long serialVersionUID = 1L;
	private BizPoHeader poHeader;		// biz_po_header.id
	private Integer lineNo;		// 订单详情行号
	private Integer pLineNo;		// bom产品 kit
	private BizSkuInfo skuInfo;		// biz_sku_info.id
	private String partNo;		// 商品编号
	private String skuName;		// 商品名称
	private Double unitPrice;		// 商品单价
	private Integer ordQty;		// 采购数量
	private Integer sendQty;		//采购单供货数量

	/**
	 * 总的已排产量
	 */
	private Integer sumSchedulingNum = 0;

	/**
	 * 总的已确认数量
	 */
	private Integer sumCompleteNum = 0;

	/**
	 * 排产计划
	 */
	private BizSchedulingPlan bizSchedulingPlan;

	/**
	 * 订单按商品排产是，总的已确认量
	 */
	private Integer sumCompleteDetailNum;

	public BizPoDetail() {
		super();
	}

	public BizPoDetail(Integer id){
		super(id);
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

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BizPoHeader getPoHeader() {
		return poHeader;
	}

	public void setPoHeader(BizPoHeader poHeader) {
		this.poHeader = poHeader;
	}

	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer lineNo) {
		this.lineNo = lineNo;
	}

	public Integer getpLineNo() {
		return pLineNo;
	}

	public void setpLineNo(Integer pLineNo) {
		this.pLineNo = pLineNo;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public Integer getOrdQty() {
		return ordQty;
	}

	public void setOrdQty(Integer ordQty) {
		this.ordQty = ordQty;
	}

	public Integer getSendQty() {
		return sendQty;
	}

	public void setSendQty(Integer sendQty) {
		this.sendQty = sendQty;
	}

	public Integer getSumSchedulingNum() {
		return sumSchedulingNum;
	}

	public void setSumSchedulingNum(Integer sumSchedulingNum) {
		this.sumSchedulingNum = sumSchedulingNum;
	}

	public Integer getSumCompleteNum() {
		return sumCompleteNum;
	}

	public void setSumCompleteNum(Integer sumCompleteNum) {
		this.sumCompleteNum = sumCompleteNum;
	}

	public BizSchedulingPlan getBizSchedulingPlan() {
		return bizSchedulingPlan;
	}

	public void setBizSchedulingPlan(BizSchedulingPlan bizSchedulingPlan) {
		this.bizSchedulingPlan = bizSchedulingPlan;
	}

	public Integer getSumCompleteDetailNum() {
		return sumCompleteDetailNum;
	}

	public void setSumCompleteDetailNum(Integer sumCompleteDetailNum) {
		this.sumCompleteDetailNum = sumCompleteDetailNum;
	}
}
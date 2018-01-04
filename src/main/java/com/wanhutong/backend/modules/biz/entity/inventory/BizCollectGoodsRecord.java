/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 收货记录表Entity
 * @author 张腾飞
 * @version 2018-01-03
 */
public class BizCollectGoodsRecord extends DataEntity<BizCollectGoodsRecord> {
	
	private static final long serialVersionUID = 1L;
	private BizInventoryInfo invInfo;		// 仓库ID，biz_inventory_info.id
	private BizSkuInfo skuInfo;		// 商品ID，biz_sku_info.id
	private BizOrderHeader orderHeader;		// 订单ID，
	private String receiveNum;		// 收货数量
	private Office vender;		// 供应商ID sys_office.id &amp;  type=vend
	private Date receiveDate;		// 收货时间
	
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

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	@Length(min=1, max=11, message="收货数量长度必须介于 1 和 11 之间")
	public String getReceiveNum() {
		return receiveNum;
	}

	public void setReceiveNum(String receiveNum) {
		this.receiveNum = receiveNum;
	}

	public Office getVender() {
		return vender;
	}

	public void setVender(Office vender) {
		this.vender = vender;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="收货时间不能为空")
	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

}
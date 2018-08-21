/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 商品出厂价日志表Entity
 * @author Oy
 * @version 2018-04-23
 */
public class BizSkuViewLog extends DataEntity<BizSkuViewLog> {
	
	private static final long serialVersionUID = 1L;
	private BizSkuInfo skuInfo;		// sku商品编号
	private String itemNo;		// sku商品货号
	private Double frontBuyPrice;		// 商品修改前结算价格
	private Double changePrice;		//价格变化
	private Double afterBuyPrice;		// 商品修改后结算价格
	
	public BizSkuViewLog() {
		super();
	}

	public BizSkuViewLog(Integer id){
		super(id);
	}
	
	@Length(min=0, max=50, message="sku商品货号长度必须介于 0 和 50 之间")
	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}


	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}


	public Double getFrontBuyPrice() {
		return frontBuyPrice;
	}

	public void setFrontBuyPrice(Double frontBuyPrice) {
		this.frontBuyPrice = frontBuyPrice;
	}

	public Double getAfterBuyPrice() {
		return afterBuyPrice;
	}

	public void setAfterBuyPrice(Double afterBuyPrice) {
		this.afterBuyPrice = afterBuyPrice;
	}

	public Double getChangePrice() {
		return changePrice;
	}

	public void setChangePrice(Double changePrice) {
		this.changePrice = changePrice;
	}
}
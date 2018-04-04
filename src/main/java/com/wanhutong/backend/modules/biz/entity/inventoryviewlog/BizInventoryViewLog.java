/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventoryviewlog;

import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 库存盘点记录Entity
 * @author zx
 * @version 2018-03-27
 *
 * invInfo : 仓库
 * invType : 库存类型：1常规；2残损；3专属
 * sku ： 商品
 * stockQty ：库存数量
 * stockChangeQty ： 改变数量
 */
public class BizInventoryViewLog extends DataEntity<BizInventoryViewLog> {
	
	private static final long serialVersionUID = 1L;
	private BizInventoryInfo invInfo;
	private Integer invType;
	private BizSkuInfo skuInfo;
	private Integer stockQty;
	private Integer stockChangeQty;
	
	public BizInventoryViewLog() {
		super();
	}

	public BizInventoryViewLog(Integer id){
		super(id);
	}

	public BizInventoryInfo getInvInfo() {
		return invInfo;
	}

	public void setInvInfo(BizInventoryInfo invInfo) {
		this.invInfo = invInfo;
	}

	public Integer getInvType() {
		return invType;
	}

	public void setInvType(Integer invType) {
		this.invType = invType;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public Integer getStockQty() {
		return stockQty;
	}

	public void setStockQty(Integer stockQty) {
		this.stockQty = stockQty;
	}

	public Integer getStockChangeQty() {
		return stockChangeQty;
	}

	public void setStockChangeQty(Integer stockChangeQty) {
		this.stockChangeQty = stockChangeQty;
	}
}
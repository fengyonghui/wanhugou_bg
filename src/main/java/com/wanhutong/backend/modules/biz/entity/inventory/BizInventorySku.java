/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

/**
 * 商品库存详情Entity
 * @author 张腾飞
 * @version 2017-12-29
 */
public class BizInventorySku extends DataEntity<BizInventorySku> {

	private static final long serialVersionUID = 1L;
	private BizInventoryInfo invInfo;        // 仓库ID，biz_inventory_info.id
	private BizSkuInfo skuInfo;        // biz_sku_info.id
	private Integer invType;        // 库存类型：1常规；2残损；3专属
	private Integer stockQty;        // 库存数量--入库时数量增加，出库时减少
	private Integer stockOrdQty;       // 销售订单数量
	private Integer transInQty;        // 调入数量--仓库间商品调拨，调拨单已审批
	private Integer transOutQty;        // 调出数量--仓库间商品调拨，调拨单对方已出库
	private Office customer;        // 专属库存的客户id； sys_office.id &amp; type = 'customer'
//	private String sOrdQty;

	public BizInventorySku() {
		super();
	}

	public BizInventorySku(Integer id) {
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

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	@Length(min = 1, max = 4, message = "库存类型：1常规；2残损；3专属长度必须介于 1 和 4 之间")
	public Integer getInvType() {
		return invType;
	}

	public void setInvType(Integer invType) {
		this.invType = invType;
	}

	@Length(min = 1, max = 11, message = "库存数量--入库时数量增加，出库时减少长度必须介于 1 和 11 之间")
	public Integer getStockQty() {
		return stockQty;
	}

	public void setStockQty(Integer stockQty) {
		this.stockQty = stockQty;
	}

	@Length(min = 1, max = 11, message = "销售订单数量长度必须介于 1 和 11 之间")
	public Integer getStockOrdQty() {
		return stockOrdQty;
	}

	public void setStockOrdQty(Integer stockOrdQty) {
		this.stockOrdQty = stockOrdQty;
	}

	@Length(min = 1, max = 11, message = "调入数量--仓库间商品调拨，调拨单已审批长度必须介于 1 和 11 之间")
	public Integer getTransInQty() {
		return transInQty;
	}

	public void setTransInQty(Integer transInQty) {
		this.transInQty = transInQty;
	}

	@Length(min = 1, max = 11, message = "调出数量--仓库间商品调拨，调拨单对方已出库长度必须介于 1 和 11 之间")
	public Integer getTransOutQty() {
		return transOutQty;
	}

	public void setTransOutQty(Integer transOutQty) {
		this.transOutQty = transOutQty;
	}

}
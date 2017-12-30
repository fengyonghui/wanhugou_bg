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
	private BizInventoryInfo invId;        // 仓库ID，biz_inventory_info.id
	private BizSkuInfo skuId;        // biz_sku_info.id
	private String invType;        // 库存类型：1常规；2残损；3专属
	private String stockQty;        // 库存数量--入库时数量增加，出库时减少
	private String sOrdQty;        // 销售订单数量
	private String transInQty;        // 调入数量--仓库间商品调拨，调拨单已审批
	private String transOutQty;        // 调出数量--仓库间商品调拨，调拨单对方已出库
	private Office custId;        // 专属库存的客户id； sys_office.id &amp; type = 'customer'

	public BizInventorySku() {
		super();
	}

	public BizInventorySku(Integer id) {
		super(id);
	}

	public BizInventoryInfo getInvId() {
		return invId;
	}

	public void setInvId(BizInventoryInfo invId) {
		this.invId = invId;
	}

	public BizSkuInfo getSkuId() {
		return skuId;
	}

	public void setSkuId(BizSkuInfo skuId) {
		this.skuId = skuId;
	}

	public Office getCustId() {
		return custId;
	}

	public void setCustId(Office custId) {
		this.custId = custId;
	}

	@Length(min = 1, max = 4, message = "库存类型：1常规；2残损；3专属长度必须介于 1 和 4 之间")
	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	@Length(min = 1, max = 11, message = "库存数量--入库时数量增加，出库时减少长度必须介于 1 和 11 之间")
	public String getStockQty() {
		return stockQty;
	}

	public void setStockQty(String stockQty) {
		this.stockQty = stockQty;
	}

	@Length(min = 1, max = 11, message = "销售订单数量长度必须介于 1 和 11 之间")
	public String getSOrdQty() {
		return sOrdQty;
	}

	public void setSOrdQty(String sOrdQty) {
		this.sOrdQty = sOrdQty;
	}

	@Length(min = 1, max = 11, message = "调入数量--仓库间商品调拨，调拨单已审批长度必须介于 1 和 11 之间")
	public String getTransInQty() {
		return transInQty;
	}

	public void setTransInQty(String transInQty) {
		this.transInQty = transInQty;
	}

	@Length(min = 1, max = 11, message = "调出数量--仓库间商品调拨，调拨单对方已出库长度必须介于 1 和 11 之间")
	public String getTransOutQty() {
		return transOutQty;
	}

	public void setTransOutQty(String transOutQty) {
		this.transOutQty = transOutQty;
	}

}
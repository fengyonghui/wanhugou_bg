/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.dto;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 商品库存详情Entity
 * @author 张腾飞
 * @version 2017-12-29
 */
public class BizInventorySkus extends DataEntity<BizInventorySkus> {

	private static final long serialVersionUID = 1L;
	private String invInfoIds;        // 仓库ID，biz_inventory_info.id
	private String skuInfoIds;        // biz_sku_info.id
	private String invTypes;        // 库存类型：1常规；2残损；3专属


	public BizInventorySkus() {
		super();
	}

	public BizInventorySkus(Integer id) {
		super(id);
	}

	public String getInvInfoIds() {
		return invInfoIds;
	}

	public void setInvInfoIds(String invInfoIds) {
		this.invInfoIds = invInfoIds;
	}

	public String getSkuInfoIds() {
		return skuInfoIds;
	}

	public void setSkuInfoIds(String skuInfoIds) {
		this.skuInfoIds = skuInfoIds;
	}

	public String getInvTypes() {
		return invTypes;
	}

	public void setInvTypes(String invTypes) {
		this.invTypes = invTypes;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shop;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;

/**
 * 商品购物车Entity
 * @author OuyangXiutian
 * @version 2018-01-03
 */
public class BizShopCart extends DataEntity<BizShopCart> {
	
	private static final long serialVersionUID = 1L;
	private BizOpShelfInfo skuShelfinfo;		// 商品货架ID
	private Office office;		// 采购商ID
	private User user;		// 采购商或采购顾问ID
	private Integer skuQty;		// sku数量
	
	public BizShopCart() {
		super();
	}

	public BizShopCart(Integer id){
		super(id);
	}
	
	
	public BizOpShelfInfo getSkuShelfinfo() {
		return skuShelfinfo;
	}
	
	public void setSkuShelfinfo(BizOpShelfInfo skuShelfinfo) {
		this.skuShelfinfo = skuShelfinfo;
	}
	
	public Office getOffice() {
		return office;
	}
	
	public void setOffice(Office office) {
		this.office = office;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Integer getSkuQty() {
		return skuQty;
	}
	
	public void setSkuQty(Integer skuQty) {
		this.skuQty = skuQty;
	}
}
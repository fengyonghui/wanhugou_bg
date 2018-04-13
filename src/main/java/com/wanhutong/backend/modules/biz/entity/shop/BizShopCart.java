/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.shop;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;

/**
 * 商品购物车Entity
 * @author OuyangXiutian
 * @version 2018-01-03
 */
public class BizShopCart extends DataEntity<BizShopCart> {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 1: B端 ; 2:C端
	 * */
	private Integer custType;
	private BizOpShelfSku skuShelfinfo;		// 货架商品ID
	private Office office;		// 采购商ID
	private User user;		// 采购商或采购顾问ID
	private Integer skuQty;		// sku数量
	/**
	 * C端删除标记
	 * */
	private String cendDele;

	public BizShopCart() {
		super();
	}

	public BizShopCart(Integer id){
		super(id);
	}

	public BizOpShelfSku getSkuShelfinfo() {
		return skuShelfinfo;
	}

	public void setSkuShelfinfo(BizOpShelfSku skuShelfinfo) {
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

	public Integer getCustType() {
		return custType;
	}

	public void setCustType(Integer custType) {
		this.custType = custType;
	}

	public String getCendDele() {
		return cendDele;
	}

	public void setCendDele(String cendDele) {
		this.cendDele = cendDele;
	}
}
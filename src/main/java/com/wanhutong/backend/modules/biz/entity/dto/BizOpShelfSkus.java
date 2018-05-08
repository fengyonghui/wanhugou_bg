/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 商品上架管理Entity
 * @author liuying
 * @version 2017-12-19
 */
public class BizOpShelfSkus extends DataEntity<BizOpShelfSkus> {

	private static final long serialVersionUID = 1L;
	private BizOpShelfInfo opShelfInfo;		// 货架ID
	private String productIds; //prod_id  上架产品【SPU】ID
	private Office centerOffice;		// 采购中心ID（sys_office.id） 0:代表平台商品
	private User shelfUser;		// 上架人
	private String shelfQtys;		// 上架数量
	private String orgPrices;		// 原价
	private String salePrices;		// 销售单价-现价
	private String minQtys;		// 此单价所对应的最低销售数量；
	private String maxQtys;		// 此单价所对应的最高销售数量；9999：不限制
	private String shelfTimes;		// 上架时间
	private User unshelfUser;		// 下架人
	private String unshelfTimes;		// 下架时间
	private String prioritys;		// 显示次序
	private String skuInfoIds;
	private String shelfs;		//货架ID


	private int shelfSign; //货架删除返回标志

	/**
	 * C端商品上下架
	 * */
	private String cendShelf;


	public BizOpShelfSkus() {
		super();
	}

	public BizOpShelfSkus(Integer id){
		super(id);
	}
//	@NotNull(message="货架不能为空")
	public BizOpShelfInfo getOpShelfInfo() {
		return opShelfInfo;
	}

	public void setOpShelfInfo(BizOpShelfInfo opShelfInfo) {
		this.opShelfInfo = opShelfInfo;
	}

	public String getProductIds() {
		return productIds;
	}

	public void setProductIds(String productIds) {
		this.productIds = productIds;
	}

	public Office getCenterOffice() {
		return centerOffice;
	}

	public void setCenterOffice(Office centerOffice) {
		this.centerOffice = centerOffice;
	}

	public User getShelfUser() {
		return shelfUser;
	}

	public void setShelfUser(User shelfUser) {
		this.shelfUser = shelfUser;
	}
	@NotNull(message="上架数量不能为空")
	public String getShelfQtys() {
		return shelfQtys;
	}

	public void setShelfQtys(String shelfQtys) {
		this.shelfQtys = shelfQtys;
	}
	@NotNull(message="原始价不能为空")
	public String getOrgPrices() {
		return orgPrices;
	}

	public void setOrgPrices(String orgPrices) {
		this.orgPrices = orgPrices;
	}
	@NotNull(message="售价不能为空")
	public String getSalePrices() {
		return salePrices;
	}

	public void setSalePrices(String salePrices) {
		this.salePrices = salePrices;
	}

	public String getMinQtys() {
		return minQtys;
	}

	public void setMinQtys(String minQtys) {
		this.minQtys = minQtys;
	}

	public String getMaxQtys() {
		return maxQtys;
	}

	public void setMaxQtys(String maxQtys) {
		this.maxQtys = maxQtys;
	}
	@NotNull(message="上架时间不能为空")
	public String getShelfTimes() {
		return shelfTimes;
	}

	public void setShelfTimes(String shelfTimes) {
		this.shelfTimes = shelfTimes;
	}

	public User getUnshelfUser() {
		return unshelfUser;
	}

	public void setUnshelfUser(User unshelfUser) {
		this.unshelfUser = unshelfUser;
	}

	public String getUnshelfTimes() {
		return unshelfTimes;
	}

	public void setUnshelfTimes(String unshelfTimes) {
		this.unshelfTimes = unshelfTimes;
	}

	public String getPrioritys() {
		return prioritys;
	}

	public void setPrioritys(String prioritys) {
		this.prioritys = prioritys;
	}

	public int getShelfSign() {
		return shelfSign;
	}

	public void setShelfSign(int shelfSign) {
		this.shelfSign = shelfSign;
	}

	public String getSkuInfoIds() {
		return skuInfoIds;
	}

	public void setSkuInfoIds(String skuInfoIds) {
		this.skuInfoIds = skuInfoIds;
	}

	public String getCendShelf() {
		return cendShelf;
	}

	public void setCendShelf(String cendShelf) {
		this.cendShelf = cendShelf;
	}

	public String getShelfs() {
		return shelfs;
	}

	public void setShelfs(String shelfs) {
		this.shelfs = shelfs;
	}
}
/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.common.persistence.TreeEntity;

/**
 * 商品skuEntity
 * @author zx
 * @version 2017-12-07
 */
public class BizSkuInfo extends DataEntity<BizSkuInfo> {
	
	private static final long serialVersionUID = 1L;
	
//	private BizSkuInfo bizSkuInfo;
	private BizProductInfo productInfo;		// 所属产品id
	private Integer skuType;		// 试销品、主销品、热销品、尾销品
	private String name;		// 商品名称
	private String partNo;    //商品编码
	private Double basePrice; //基础售价
	private Double buyPrice;   //采购价格
	private List bizSkuInfo;

	private String skuPropertyInfos;

	private String photos;

	private Map<String,BizProdPropertyInfo> prodPropMap;

	private List<String> skuIds;


	private int sign;//sku删除返回的标志
	public BizSkuInfo() {
		super();
	}

	public BizSkuInfo(Integer id){
		super(id);
	}

//	public BizSkuInfo getBizSkuInfo() {
//		return bizSkuInfo;
//	}
//
//	public void setBizSkuInfo(BizSkuInfo bizSkuInfo) {
//		this.bizSkuInfo = bizSkuInfo;
//	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public Integer getSkuType() {
		return skuType;
	}

	public void setSkuType(Integer skuType) {
		this.skuType = skuType;
	}

	@Length(min=1, max=100, message="商品名称长度必须介于 1 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public List getBizSkuInfo() {
		return bizSkuInfo;
	}

	public void setBizSkuInfo(List bizSkuInfo) {
		this.bizSkuInfo = bizSkuInfo;
	}

	public int getSign() {
		return sign;
	}

	public void setSign(int sign) {
		this.sign = sign;
	}

	public String getSkuPropertyInfos() {
		return skuPropertyInfos;
	}

	public void setSkuPropertyInfos(String skuPropertyInfos) {
		this.skuPropertyInfos = skuPropertyInfos;
	}

	public Map<String, BizProdPropertyInfo> getProdPropMap() {
		return prodPropMap;
	}

	public void setProdPropMap(Map<String, BizProdPropertyInfo> prodPropMap) {
		this.prodPropMap = prodPropMap;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public List<String> getSkuIds() {
		return skuIds;
	}

	public void setSkuIds(List<String> skuIds) {
		this.skuIds = skuIds;
	}
}
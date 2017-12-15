/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.List;
import java.util.Map;

/**
 * 产品信息表Entity
 * @author zx
 * @version 2017-12-13
 */
public class BizProductInfo extends DataEntity<BizProductInfo> {

	private static final long serialVersionUID = 1L;
	private String name;		// 商品名称
	private String prodCode;		// 商品代码--厂家定的-或自己定的
	private BizCatePropValue catePropValue;		// biz_cate_prop_value.id, 对应品牌分类的属性值ID
	private String brandName;		// 品牌名称，冗余字段，提升查询效率
	private String description;		// 商品描述
	private Office office;		// sys_office.id &amp; type= vendor
	private String minPrice;		// 最低售价
	private String maxPrice;		// 最高售价
	private String skuInfos; //多种商品信息
	private Map<String,BizSkuInfo> skuInfoMap;

	private String cateIds; //多种分类

	private List<BizCategoryInfo> categoryInfoList = Lists.newArrayList();

	public List<Integer> getCateIdList() {
		List<Integer> cateIdList = Lists.newArrayList();
		for (BizCategoryInfo categoryInfo : categoryInfoList) {
			cateIdList.add(categoryInfo.getId());
		}
		return cateIdList;
	}

	public void setCateIdList(List<String> cateIdList) {
		categoryInfoList = Lists.newArrayList();
		for (String cateId : cateIdList) {
			BizCategoryInfo categoryInfo = new BizCategoryInfo();
			categoryInfo.setId(Integer.valueOf(cateId));
			categoryInfoList.add(categoryInfo);
		}
	}

	public String getCateIds() {
		cateIds=StringUtils.join(getCateIdList(), ",");
		return cateIds;
	}

	public void setCateIds(String cateIds) {

		categoryInfoList = Lists.newArrayList();
		if (cateIds != null){
			String[] ids = StringUtils.split(cateIds, ",");
			setCateIdList(Lists.newArrayList(ids));
		}

	}
	public BizProductInfo() {
		super();
	}

	public BizProductInfo(Integer id){
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=10, message="商品代码--厂家定的-或自己定的长度必须介于 1 和 10 之间")
	public String getProdCode() {
		return prodCode;
	}

	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	
	@Length(min=1, max=50, message="品牌名称，冗余字段，提升查询效率长度必须介于 1 和 50 之间")
	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(String minPrice) {
		this.minPrice = minPrice;
	}
	
	public String getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(String maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getSkuInfos() {
		return skuInfos;
	}

	public void setSkuInfos(String skuInfos) {
		this.skuInfos = skuInfos;
	}

	public Map<String, BizSkuInfo> getSkuInfoMap() {
		return skuInfoMap;
	}

	public void setSkuInfoMap(Map<String, BizSkuInfo> skuInfoMap) {
		this.skuInfoMap = skuInfoMap;
	}

	public BizCatePropValue getCatePropValue() {
		return catePropValue;
	}

	public void setCatePropValue(BizCatePropValue catePropValue) {
		this.catePropValue = catePropValue;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public List<BizCategoryInfo> getCategoryInfoList() {
		return categoryInfoList;
	}

	public void setCategoryInfoList(List<BizCategoryInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}


}
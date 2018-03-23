/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品信息表Entity
 * @author zx
 * @version 2017-12-13
 */
public class BizProductInfo extends DataEntity<BizProductInfo> {


//            vari_id
//    name
//            prod_code
//    brand_id
//            brand_name
//    marketing_date
//            img_url
//    description
//            vendor_id
//    vendor_name
//            min_price
//    max_price
//            status
//    create_id
//            create_time
//    u_version
//            update_time
//    update_id
//            item_no

	private static final long serialVersionUID = 1L;
	private CommonImg commonImg;
	private String name;		// 商品名称
	private BizVarietyInfo bizVarietyInfo; //
	private String prodCode;		// 商品代码--厂家定的-或自己定的
	private Dict dict;		// biz_cate_prop_value.id, 对应品牌分类的属性值ID
	private String brandName;		// 品牌名称，冗余字段，提升查询效率
	private String description;		// 商品描述
	private Office office;		// sys_office.id &amp; type= vendor
	private Double minPrice;		// 最低售价
	private Double maxPrice;		// 最高售价
	private String skuInfos; //多种商品信息
	private String imgUrl;   //图片地址
	private String photoDetails;
	private String vendorName;  //采购商名称
	private Date marketingDate ; //上市时间
	private String skuPartNo;  //sku编码，用于查询

	private String cateIds; //多种分类

	private String cateNames;//多个分类名称

	private String propNames;

	private String propOwnValues;

	private String photos; //产品图片

	private String photoLists; //产品列表图

	private SkuProd skuProd;

	private String itemNo;

	private List<String> skuAttrStrList;

	private List<BizCategoryInfo> categoryInfoList = Lists.newArrayList();
	private List<CommonImg> commonImgList = Lists.newArrayList();


	private String prodPropertyInfos;

	private Map<String,BizProdPropertyInfo> propertyMap;

	private Map<String,BizProdPropertyInfo> prodPropertyMap;

	private String source;


	public List<String> getSkuAttrStrList() {
		return skuAttrStrList;
	}

	public void setSkuAttrStrList(List<String> skuAttrStrList) {
		this.skuAttrStrList = skuAttrStrList;
	}

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
//	private Map<String,BizSkuInfo> skuInfoMap;
	private List<BizSkuInfo> skuInfosList;

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

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Date getMarketingDate() {
		return marketingDate;
	}

	public void setMarketingDate(Date marketingDate) {
		this.marketingDate = marketingDate;
	}

	public BizProductInfo() {
		super();
	}

	public BizProductInfo(Integer id){
		super(id);
	}


	public String getProdPropertyInfos() {
		return prodPropertyInfos;
	}

	public void setProdPropertyInfos(String prodPropertyInfos) {
		this.prodPropertyInfos = prodPropertyInfos;
	}

	public Map<String, BizProdPropertyInfo> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, BizProdPropertyInfo> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


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

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getSkuInfos() {
		return skuInfos;
	}

	public void setSkuInfos(String skuInfos) {
		this.skuInfos = skuInfos;
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

	public List<CommonImg> getCommonImgList() {
		return commonImgList;
	}

	public void setCommonImgList(List<CommonImg> commonImgList) {
		this.commonImgList = commonImgList;
	}

	public List<BizSkuInfo> getSkuInfosList() {
		return skuInfosList;
	}

	public void setSkuInfosList(List<BizSkuInfo> skuInfosList) {
		this.skuInfosList = skuInfosList;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, BizProdPropertyInfo> getProdPropertyMap() {
		return prodPropertyMap;
	}

	public void setProdPropertyMap(Map<String, BizProdPropertyInfo> prodPropertyMap) {
		this.prodPropertyMap = prodPropertyMap;
	}

	public String getPropNames() {
		return propNames;
	}

	public void setPropNames(String propNames) {
		this.propNames = propNames;
	}

	public CommonImg getCommonImg() {
		return commonImg;
	}

	public void setCommonImg(CommonImg commonImg) {
		this.commonImg = commonImg;
	}


	public String getPropOwnValues() {
		return propOwnValues;
	}

	public void setPropOwnValues(String propOwnValues) {
		this.propOwnValues = propOwnValues;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPhotoDetails() {
		return photoDetails;
	}

	public void setPhotoDetails(String photoDetails) {
		this.photoDetails = photoDetails;
	}

	public SkuProd getSkuProd() {
		return skuProd;
	}

	public void setSkuProd(SkuProd skuProd) {
		this.skuProd = skuProd;
	}

	public String getPhotoLists() {
		return photoLists;
	}

	public void setPhotoLists(String photoLists) {
		this.photoLists = photoLists;
	}

	public String getSkuPartNo() {
		return skuPartNo;
	}

	public void setSkuPartNo(String skuPartNo) {
		this.skuPartNo = skuPartNo;
	}

	public String getCateNames() {
		return cateNames;
	}

	public void setCateNames(String cateNames) {
		this.cateNames = cateNames;
	}

	public BizVarietyInfo getBizVarietyInfo() {
		return bizVarietyInfo;
	}

	public void setBizVarietyInfo(BizVarietyInfo bizVarietyInfo) {
		this.bizVarietyInfo = bizVarietyInfo;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public Dict getDict() {
		return dict;
	}

	public void setDict(Dict dict) {
		this.dict = dict;
	}


}
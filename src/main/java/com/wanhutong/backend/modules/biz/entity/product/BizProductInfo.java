/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.product;
import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.SkuProd;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValue;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品信息表Entity
 * @author zx
 * @version 2017-12-13
 */
public class BizProductInfo extends DataEntity<BizProductInfo> {

	private static final long serialVersionUID = 1L;
	private CommonImg commonImg;
	private String name;		// 商品名称
	private BizVarietyInfo bizVarietyInfo; //
	private String prodCode;		// 商品代码--厂家定的-或自己定的
	private Dict dict;		// biz_cate_prop_value.id, 对应品牌分类的属性值ID
	private String brandId;		// 品牌名ID.
	private PropValue propValue;		// biz_cate_prop_value.id, 对应品牌分类的属性值ID
	private String brandName;		// 品牌名称，冗余字段，提升查询效率
	private String description;		// 商品描述
	private Office office;		// sys_office.id &amp; type= vendor
	private Double minPrice;		// 最低售价
	private Double maxPrice;		// 最高售价
	private String skuInfos; //多种商品信息
	private String imgUrl;   //图片地址
	private String photoDetails;
	private String vendorName;  //供应商名称
	private Date marketingDate ; //上市时间
	private Byte prodType;		//产品类型
	private String skuPartNo;  //sku编码，用于查询

	private String dicts;		//多个产品特有属性

	private String cateIds; //多种分类

	private String cateNames;//多个分类名称

	private String propNames;

	private String propOwnValues;

	private String photos; //产品图片

	private String photoLists; //产品列表图

	private SkuProd skuProd;

	/**
	 * 浏览量
	 */
	private Integer viewCount;
	/**
	 * 订单量
	 */
	private Integer orderCount;

	private String itemNo;
	private String itemNoComplete;
	private String imgPhotosSorts;		//主图顺序
    private String imgDetailSorts;      //列表图顺序
    private String detailVideo;      // 列表视频
    private String bannerVideo;      // banner视频
	/**
	 * 业务状态
	 */
	private Integer bizStatus;

	/**
	 * sku 组合字符串集合
	 */
	private List<String> skuAttrStrList;

	/**
	 *	材质
	 */
	private AttributeValueV2 materialAttributeValue;

	/**
	 * 标签ID集合
	 */
	private String tagStr;

	/**
	 * 材质集合
	 */
	private String textureStr;

	/**
	 * 日期查询
	 * */
	private Date createDateStart;
	private Date createDateEnd;

	/**
	 * 品类主管
	 * */
	private User user;
	private Integer prodVice;//点击量
	private String skuItemNo;//商品货号

	private List<BizCategoryInfo> categoryInfoList = Lists.newArrayList();
	private List<CommonImg> commonImgList = Lists.newArrayList();


	private String prodPropertyInfos;

	private Map<String,BizProdPropertyInfo> propertyMap;

	private Map<String,BizProdPropertyInfo> prodPropertyMap;

	private String source;

	private String searchItemNo;

	public String getItemNoComplete() {
		return itemNoComplete;
	}

	public void setItemNoComplete(String itemNoComplete) {
		this.itemNoComplete = itemNoComplete;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public AttributeValueV2 getMaterialAttributeValue() {
		return materialAttributeValue;
	}

	public void setMaterialAttributeValue(AttributeValueV2 materialAttributeValue) {
		this.materialAttributeValue = materialAttributeValue;
	}

	public String getTextureStr() {
		return textureStr;
	}

	public void setTextureStr(String textureStr) {
		this.textureStr = textureStr;
	}

	public String getTagStr() {
		return tagStr;
	}

	public void setTagStr(String tagStr) {
		this.tagStr = tagStr;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

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

	public PropValue getPropValue() {
		return propValue;
	}

	public void setPropValue(PropValue propValue) {
		this.propValue = propValue;
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


	/**
	 * 供应商上传产品的审核状态
	 */
	public enum BizStatus {
//		产品状态：1、未审核；2、审核通过；3、审核失败
		UNAUDITED(1, "未审核"),
		AUDIT_PASS(2, "审核通过"),
		AUDIT_FAILED (3, "审核失败"),
		;
		private int status;
		private String desc;

		BizStatus(int status, String desc) {
			this.status = status;
			this.desc = desc;
		}

		public int getStatus() {
			return status;
		}

		public String getDesc() {
			return desc;
		}
	}

	public Date getCreateDateStart() {
		return createDateStart;
	}

	public void setCreateDateStart(Date createDateStart) {
		this.createDateStart = createDateStart;
	}

	public Date getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(Date createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public Byte getProdType() {
		return prodType;
	}

	public void setProdType(Byte prodType) {
		this.prodType = prodType;
	}

	public String getImgPhotosSorts() {
		return imgPhotosSorts;
	}

	public void setImgPhotosSorts(String imgPhotosSorts) {
		this.imgPhotosSorts = imgPhotosSorts;
	}

    public String getImgDetailSorts() {
        return imgDetailSorts;
    }

    public void setImgDetailSorts(String imgDetailSorts) {
        this.imgDetailSorts = imgDetailSorts;
    }

	public String getDicts() {
		return dicts;
	}

	public void setDicts(String dicts) {
		this.dicts = dicts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getProdVice() {
		return prodVice;
	}

	public void setProdVice(Integer prodVice) {
		this.prodVice = prodVice;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public String getSkuItemNo() {
		return skuItemNo;
	}

	public void setSkuItemNo(String skuItemNo) {
		this.skuItemNo = skuItemNo;
	}

	public String getDetailVideo() {
		return detailVideo;
	}

	public void setDetailVideo(String detailVideo) {
		this.detailVideo = detailVideo;
	}

	public String getBannerVideo() {
		return bannerVideo;
	}

	public void setBannerVideo(String bannerVideo) {
		this.bannerVideo = bannerVideo;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public String getSearchItemNo() {
		return searchItemNo;
	}

	public void setSearchItemNo(String searchItemNo) {
		this.searchItemNo = searchItemNo;
	}
}
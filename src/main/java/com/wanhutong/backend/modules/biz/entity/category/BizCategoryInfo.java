/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import com.wanhutong.backend.common.persistence.TreeEntity;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Map;

/**
 * 垂直商品类目表Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCategoryInfo extends TreeEntity<BizCategoryInfo>  {
	
	private static final long serialVersionUID = 1L;
	private BizCatelogInfo catelogInfo;		// 目录分类 --大的一级分类
	private String description;		// 分类描述
	private Byte status;//是否可用
	private Integer cid;//用于参数传递
	private Integer brandId; // 根据品牌查分类
	private String name;	//分类名称

	private String catePhoto; //分类图片
	private Integer imgId;

	private List<BizCatePropertyInfo> catePropertyInfoList;

	private Map<String,BizCatePropertyInfo> propertyMap;

	private String catePropertyInfos;

	public BizCategoryInfo() {
		super();
	}

	public BizCategoryInfo(Integer id){
		super(id);
	}


	@Length(min=0, max=512, message="分类描述长度必须介于 0 和 512 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public BizCategoryInfo getParent() {
		return parent;
	}
	@Override
	public void setParent(BizCategoryInfo parent) {
		this.parent = parent;
	}

	public BizCatelogInfo getCatelogInfo() {
		return catelogInfo;
	}

	public void setCatelogInfo(BizCatelogInfo catelogInfo) {
		this.catelogInfo = catelogInfo;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public List<BizCatePropertyInfo> getCatePropertyInfoList() {
		return catePropertyInfoList;
	}

	public void setCatePropertyInfoList(List<BizCatePropertyInfo> catePropertyInfoList) {
		this.catePropertyInfoList = catePropertyInfoList;
	}

	public String getCatePhoto() {
		return catePhoto;
	}

	public void setCatePhoto(String catePhoto) {
		this.catePhoto = catePhoto;
	}

	public Integer getImgId() {
		return imgId;
	}

	public void setImgId(Integer imgId) {
		this.imgId = imgId;
	}

	public String getCatePropertyInfos() {
		return catePropertyInfos;
	}

	public void setCatePropertyInfos(String catePropertyInfos) {
		this.catePropertyInfos = catePropertyInfos;
	}

	public Map<String, BizCatePropertyInfo> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, BizCatePropertyInfo> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
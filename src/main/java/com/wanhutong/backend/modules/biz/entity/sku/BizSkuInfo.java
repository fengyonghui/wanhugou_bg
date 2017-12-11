/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
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
	
	private BizSkuInfo bizSkuInfo;
	private String prodId;		// 所属产品id
	private String skuType;		// 试销品、主销品、热销品、尾销品
	private String name;		// 商品名称
	private String status;		// status
//	private User createId;		// create_id
//	private Date createTime;		// create_time
//	private String uVersion;		// u_version
//	private User updateId;		// update_id
//	private Date updateTime;		// update_time
	
	public BizSkuInfo() {
		super();
	}

	public BizSkuInfo(Integer id){
		super(id);
	}

	public BizSkuInfo getBizSkuInfo() {
		return bizSkuInfo;
	}
	
	public void setBizSkuInfo(BizSkuInfo bizSkuInfo) {
		this.bizSkuInfo = bizSkuInfo;
	}
	
	@Length(min=1, max=11, message="所属产品id长度必须介于 1 和 11 之间")
	public String getProdId() {
		return prodId;
	}
	
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	
	@Length(min=1, max=4, message="试销品、主销品、热销品、尾销品长度必须介于 1 和 4 之间")
	public String getSkuType() {
		return skuType;
	}

	public void setSkuType(String skuType) {
		this.skuType = skuType;
	}
	
	@Length(min=1, max=100, message="商品名称长度必须介于 1 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=1, message="status长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
//	@NotNull(message="create_id不能为空")
//	public User getCreateId() {
//		return createId;
//	}
//
//	public void setCreateId(User createId) {
//		this.createId = createId;
//	}
//	
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//	@NotNull(message="create_time不能为空")
//	public Date getCreateTime() {
//		return createTime;
//	}
//
//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//	}
//	
//	@Length(min=1, max=4, message="u_version长度必须介于 1 和 4 之间")
//	public String getUVersion() {
//		return uVersion;
//	}
//
//	public void setUVersion(String uVersion) {
//		this.uVersion = uVersion;
//	}
//	
//	@NotNull(message="update_id不能为空")
//	public User getUpdateId() {
//		return updateId;
//	}
//
//	public void setUpdateId(User updateId) {
//		this.updateId = updateId;
//	}
//	
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//	@NotNull(message="update_time不能为空")
//	public Date getUpdateTime() {
//		return updateTime;
//	}
//
//	public void setUpdateTime(Date updateTime) {
//		this.updateTime = updateTime;
//	}
	
}
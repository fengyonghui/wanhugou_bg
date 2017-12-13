/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.product.entity;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 产品信息表Entity
 * @author zx
 * @version 2017-12-13
 */
public class BizProductInfo extends DataEntity<BizProductInfo> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 商品名称
	private String prodCode;		// 商品代码--厂家定的-或自己定的
	private String brandId;		// biz_cate_prop_value.id, 对应品牌分类的属性值ID
	private String brandName;		// 品牌名称，冗余字段，提升查询效率
	private String description;		// 商品描述
	private String vendorId;		// sys_office.id &amp; type= vendor
	private String minPrice;		// 最低售价
	private String maxPrice;		// 最高售价
	private String status;		// 记录状态 1:active 2:inactive
	private User createId;		// 创建人
	private Date createTime;		// 创建时间
	private String uVersion;		// u_version
	private Date updateTime;		// 最后更新时间
	private User updateId;		// 更新人ID
	
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
	
//	@id, 对应品牌分类的属性值ID长度必须介于 1 和 11 之间")
	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
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
	
//	@id &amp; type= vendor长度必须介于 1 和 11 之间")
	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
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
	
	@Length(min=1, max=1, message="记录状态 1:active 2:inactive长度必须介于 1 和 1 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@NotNull(message="创建人不能为空")
	public User getCreateId() {
		return createId;
	}

	public void setCreateId(User createId) {
		this.createId = createId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Length(min=1, max=4, message="u_version长度必须介于 1 和 4 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="最后更新时间不能为空")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@NotNull(message="更新人ID不能为空")
	public User getUpdateId() {
		return updateId;
	}

	public void setUpdateId(User updateId) {
		this.updateId = updateId;
	}
	
}
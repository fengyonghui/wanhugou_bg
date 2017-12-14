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
 * 产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizProdCate extends DataEntity<BizProdCate> {
	
	private static final long serialVersionUID = 1L;
	private String prodId;		// biz_product_info.id
	private String catId;		// biz_category_info.id
//	private String status;		// 记录状态 1: active 0: inactive
//	private User createId;		// create_id
//	private Date createTime;		// create_time
//	private String uVersion;		// u_version
//	private User updateId;		// update_id
//	private Date updateTime;		// update_time
	
	public BizProdCate() {
		super();
	}

	public BizProdCate(Integer id){
		super(id);
	}

//	@id长度必须介于 1 和 11 之间")
	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	
//	@id长度必须介于 1 和 11 之间")
	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}
	
//	@Length(min=1, max=4, message="记录状态 1: active 0: inactive长度必须介于 1 和 4 之间")
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
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
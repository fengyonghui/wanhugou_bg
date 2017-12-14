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
 * 记录产品所有属性值Entity
 * @author zx
 * @version 2017-12-14
 */
public class BizProdPropValue extends DataEntity<BizProdPropValue> {
	
	private static final long serialVersionUID = 1L;
	private String propId;		// biz_prod_property_info.id
	private String catePropId;		// biz_cate_property_info.id
	private String propName;		// 属性名称
	private String catValueId;		// biz_cate_prop_value.id
	private String propValue;		// 属性值
	private String source;		// sys系统，cate分类
	private User createId;		// sys_user.id
	private Date createTime;		// create_time
	private String status;		// status
	private User updateId;		// update_id
	private Date updateTime;		// update_time
	private String uVersion;		// u_version
	
	public BizProdPropValue() {
		super();
	}

	public BizProdPropValue(Integer id){
		super(id);
	}

	@id长度必须介于 1 和 11 之间")
	public String getPropId() {
		return propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
	}
	
	@id长度必须介于 0 和 11 之间")
	public String getCatePropId() {
		return catePropId;
	}

	public void setCatePropId(String catePropId) {
		this.catePropId = catePropId;
	}
	
	@Length(min=0, max=30, message="属性名称长度必须介于 0 和 30 之间")
	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}
	
	@id长度必须介于 1 和 11 之间")
	public String getCatValueId() {
		return catValueId;
	}

	public void setCatValueId(String catValueId) {
		this.catValueId = catValueId;
	}
	
	@Length(min=0, max=100, message="属性值长度必须介于 0 和 100 之间")
	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	
	@Length(min=0, max=4, message="sys系统，cate分类长度必须介于 0 和 4 之间")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	@id不能为空")
	public User getCreateId() {
		return createId;
	}

	public void setCreateId(User createId) {
		this.createId = createId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="create_time不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Length(min=1, max=4, message="status长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@NotNull(message="update_id不能为空")
	public User getUpdateId() {
		return updateId;
	}

	public void setUpdateId(User updateId) {
		this.updateId = updateId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="update_time不能为空")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	@Length(min=1, max=4, message="u_version长度必须介于 1 和 4 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
	}
	
}
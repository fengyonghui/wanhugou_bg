/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.category;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 记录分类下所有属性值Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatePropValue extends DataEntity<BizCatePropValue> {
	
	private static final long serialVersionUID = 1L;
	private String propId;		// biz_cate_property_info.id
	private String value;		// 记录该属性值
	private User createId;		// create_id
	private Date createTime;		// create_time
	private String status;		// 1:active 0:inactive
	
	public BizCatePropValue() {
		super();
	}

	public BizCatePropValue(Integer id){
		super(id);
	}

	@id长度必须介于 1 和 11 之间")
	public String getPropId() {
		return propId;
	}

	public void setPropId(String propId) {
		this.propId = propId;
	}
	
	@Length(min=1, max=100, message="记录该属性值长度必须介于 1 和 100 之间")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@NotNull(message="create_id不能为空")
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
	
	@Length(min=1, max=4, message="1:active 0:inactive长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
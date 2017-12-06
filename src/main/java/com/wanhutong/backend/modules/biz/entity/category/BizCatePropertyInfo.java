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
 * 记录当前分类下的所有属性Entity
 * @author liuying
 * @version 2017-12-06
 */
public class BizCatePropertyInfo extends DataEntity<BizCatePropertyInfo> {
	
	private static final long serialVersionUID = 1L;
	private String catId;		// biz_category_info.id
	private String name;		// 分类名称
	private String discription;		// 分类描述
	private User createId;		// create_id
	private Date createTime;		// create_time
	
	public BizCatePropertyInfo() {
		super();
	}

	public BizCatePropertyInfo(Integer id){
		super(id);
	}

	@id长度必须介于 1 和 11 之间")
	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}
	
	@Length(min=1, max=30, message="分类名称长度必须介于 1 和 30 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Length(min=1, max=200, message="分类描述长度必须介于 1 和 200 之间")
	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
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
	
}
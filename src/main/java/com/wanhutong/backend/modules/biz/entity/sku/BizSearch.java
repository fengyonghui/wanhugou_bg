/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.sku;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 找货定制Entity
 * @author liuying
 * @version 2018-01-20
 */
public class BizSearch extends DataEntity<BizSearch> {
	
	private static final long serialVersionUID = 1L;
	private String partNo;		// 商品编码
	private String cateId;		// 分类Id biz_category_info
	private String cateName;		// 找货名称
	private String qualityId;		// 材质属性Id sys_property_info  sys_prop_value
	private String color;		// 颜色
	private String standard;		// 规格
	private String businessStatus;		// 业务状态 0：关闭 1：开放 2：取消
	private Date sendTime;		// 期望到货时间
	private User user;		// 用户ID
	private String minPrice;		// 期望最低售价
	private String maxPrice;		// 期望最高价
	private String amount;		// 数量
	private String comment;		// 备注
	private String status;		// 数据状态 0：无效 1：有效
	private User createId;		// 创建人
	private Date createTime;		// 创建时间
	private User updateId;		// 修改人
	private Date updateTime;		// 修改时间
	
	public BizSearch() {
		super();
	}

	public BizSearch(Integer id){
		super(id);
	}

	@Length(min=0, max=30, message="商品编码长度必须介于 0 和 30 之间")
	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	
	@Length(min=1, max=11, message="分类Id biz_category_info长度必须介于 1 和 11 之间")
	public String getCateId() {
		return cateId;
	}

	public void setCateId(String cateId) {
		this.cateId = cateId;
	}
	
	@Length(min=1, max=100, message="找货名称长度必须介于 1 和 100 之间")
	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
	
	@Length(min=1, max=11, message="材质属性Id sys_property_info  sys_prop_value长度必须介于 1 和 11 之间")
	public String getQualityId() {
		return qualityId;
	}

	public void setQualityId(String qualityId) {
		this.qualityId = qualityId;
	}
	
	@Length(min=0, max=10, message="颜色长度必须介于 0 和 10 之间")
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Length(min=0, max=20, message="规格长度必须介于 0 和 20 之间")
	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}
	
	@Length(min=1, max=4, message="业务状态 0：关闭 1：开放 2：取消长度必须介于 1 和 4 之间")
	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	
	@Length(min=1, max=11, message="数量长度必须介于 1 和 11 之间")
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	@Length(min=0, max=255, message="备注长度必须介于 0 和 255 之间")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Length(min=1, max=4, message="数据状态 0：无效 1：有效长度必须介于 1 和 4 之间")
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
	
	@NotNull(message="修改人不能为空")
	public User getUpdateId() {
		return updateId;
	}

	public void setUpdateId(User updateId) {
		this.updateId = updateId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="修改时间不能为空")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
}
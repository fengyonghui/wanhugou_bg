/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.farm;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 单表生成Entity
 * @author ThinkGem
 * @version 2016-07-08
 */
public class BizFarm extends DataEntity<BizFarm> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 养殖场名称
	private Integer locationId;		// 地址id
	private Date establishedDate;		// 建立时间
	private Integer bizType;		// 经营性质
	private String remark;		// 实际经营状态
	private String status;		// 状态
	private Date createTime;		// create_time
	private Integer uVersion;		// u_version
	private Date beginEstablishedDate;		// 开始 建立时间
	private Date endEstablishedDate;		// 结束 建立时间
	
	public BizFarm() {
		super();
	}

	public BizFarm(Integer id){
		super(id);
	}

	@Length(min=1, max=255, message="养殖场名称长度必须介于 1 和 255 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEstablishedDate() {
		return establishedDate;
	}

	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate;
	}
	
	@NotNull(message="经营性质不能为空")
	public Integer getBizType() {
		return bizType;
	}

	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	
	@Length(min=0, max=255, message="实际经营状态长度必须介于 0 和 255 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Length(min=1, max=4, message="状态长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="create_time不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@NotNull(message="u_version不能为空")
	public Integer getUVersion() {
		return uVersion;
	}

	public void setUVersion(Integer uVersion) {
		this.uVersion = uVersion;
	}
	
	public Date getBeginEstablishedDate() {
		return beginEstablishedDate;
	}

	public void setBeginEstablishedDate(Date beginEstablishedDate) {
		this.beginEstablishedDate = beginEstablishedDate;
	}
	
	public Date getEndEstablishedDate() {
		return endEstablishedDate;
	}

	public void setEndEstablishedDate(Date endEstablishedDate) {
		this.endEstablishedDate = endEstablishedDate;
	}
		
}
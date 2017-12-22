/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.entity.location;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 地理位置信息Entity
 * @author OuyangXiutian
 * @version 2017-12-21
 */
public class CommonLocation extends DataEntity<CommonLocation> {
	
	private static final long serialVersionUID = 1L;
	private String provinceId;		// 省份
	private String cityId;		// 城市
	private String regionId;		// 区域
	private String pcrName;		// 省市区对应名称
	private String address;		// 详细地址
	private String zipCode;		// 邮编
	private String longitude;		// 经度
	private String latitude;		// 纬度
	private String status;		// 0，无效；1；有效
	private User createId;		// create_id
	private Date createTime;		// create_time
	private String uVersion;		// u_version
	private User updateId;		// update_id
	private Date updateTime;		// update_time
	
	public CommonLocation() {
		super();
	}

	public CommonLocation(Integer id){
		super(id);
	}

	@Length(min=1, max=6, message="省份长度必须介于 1 和 6 之间")
	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	
	@Length(min=1, max=6, message="城市长度必须介于 1 和 6 之间")
	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	@Length(min=0, max=6, message="区域长度必须介于 0 和 6 之间")
	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
	@Length(min=1, max=60, message="省市区对应名称长度必须介于 1 和 60 之间")
	public String getPcrName() {
		return pcrName;
	}

	public void setPcrName(String pcrName) {
		this.pcrName = pcrName;
	}
	
	@Length(min=0, max=255, message="详细地址长度必须介于 0 和 255 之间")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	@Length(min=0, max=10, message="邮编长度必须介于 0 和 10 之间")
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	@Length(min=1, max=4, message="0，无效；1；有效长度必须介于 1 和 4 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
	
	@Length(min=1, max=3, message="u_version长度必须介于 1 和 3 之间")
	public String getUVersion() {
		return uVersion;
	}

	public void setUVersion(String uVersion) {
		this.uVersion = uVersion;
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
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.entity.location;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
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
	private SysRegion province;		// 省份
	private SysRegion city;		// 城市
	private SysRegion region;		// 区域
	private String pcrName;		// 省市区对应名称
	private String address;		// 详细地址
	private String zipCode;		// 邮编
	private Double longitude;		// 经度
	private Double latitude;		// 纬度

	private Integer selectedRegionId;


	public Integer getSelectedRegionId() {
		return selectedRegionId;
	}

	public void setSelectedRegionId(Integer selectedRegionId) {
		this.selectedRegionId = selectedRegionId;
	}


	
	public CommonLocation() {
		super();
	}

	public CommonLocation(Integer id){
		super(id);
	}

	public SysRegion getProvince() {
		return province;
	}

	public void setProvince(SysRegion province) {
		this.province = province;
	}

	public SysRegion getCity() {
		return city;
	}

	public void setCity(SysRegion city) {
		this.city = city;
	}

	public SysRegion getRegion() {
		return region;
	}

	public void setRegion(SysRegion region) {
		this.region = region;
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

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getFullAddress(){
		StringBuilder sb = new StringBuilder();
		sb.append(getPcrName());
		if(address!=null && !"".equals(address)){
			sb.append(address);
		}
		return sb.toString();
	}
	public String getPcrName() {
		StringBuilder sb = new StringBuilder();
		if (province!=null && StringUtils.isNotBlank(province.getName())){
			sb.append(province.getName());
		}
		if (city!=null && StringUtils.isNotBlank(city.getName())){
			sb.append(city.getName());
		}
		if (region!=null && StringUtils.isNotBlank(region.getName())){
			sb.append(region.getName());
		}
		return sb.toString();
	}

	
}
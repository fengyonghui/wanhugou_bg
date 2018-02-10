/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 订单地址Entity
 * @author OuyangXiutian
 * @version 2018-01-22
 */
public class BizOrderAddress extends DataEntity<BizOrderAddress> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeaderID;	//biz_order_header.id
	private Integer type;	//1：是订单收货地址，2：是订单交货地址
	private SysRegion province;	// 省份
	private SysRegion city;		// 城市
	private SysRegion region;		// 区域
	private String pcrName;		// 省市区对应名称
	private String address;		// 详细地址
	private String receiver;		// 收货人姓名
	private String phone;		// 收货人联系电话
	private Date appointedTime;		//制定的时间


	private Integer selectedRegionId;

	public BizOrderAddress() {
		super();
	}

	public BizOrderAddress(Integer id){
		super(id);
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

	public void setPcrName(String pcrName) {
		this.pcrName = pcrName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Integer getSelectedRegionId() {
		return selectedRegionId;
	}

	public void setSelectedRegionId(Integer selectedRegionId) {
		this.selectedRegionId = selectedRegionId;
	}

	public BizOrderHeader getOrderHeaderID() {
		return orderHeaderID;
	}

	public void setOrderHeaderID(BizOrderHeader orderHeaderID) {
		this.orderHeaderID = orderHeaderID;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getAppointedTime() {
		return appointedTime;
	}

	public void setAppointedTime(Date appointedTime) {
		this.appointedTime = appointedTime;
	}
}
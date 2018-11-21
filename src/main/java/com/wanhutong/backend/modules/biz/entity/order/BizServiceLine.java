/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.SysRegion;

/**
 * 服务费物流线路Entity
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
public class BizServiceLine extends DataEntity<BizServiceLine> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 服务费用ID
	 */
	private BizServiceCharge serviceCharge;

	/**
	 * 订单类型
	 */
	private Byte orderType;

	/**
	 * 省
	 */
	private SysRegion province;

	/**
	 * 市
	 */
	private SysRegion city;

	/**
	 * 区
	 */
	private SysRegion region;

	/**
	 * 到达地 省
	 */
	private SysRegion toProvince;

	/**
	 * 到达地 市
	 */
	private SysRegion toCity;

	/**
	 * 到达地 区
	 */
	private SysRegion toRegion;

	/**
	 * 是否启用（0不启用，1启用），启用才可以正常使用
	 */
	private Byte usable;

	public BizServiceLine() {
		super();
	}

	public BizServiceLine(Integer id){
		super(id);
	}

	public BizServiceCharge getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(BizServiceCharge serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public Byte getOrderType() {
		return orderType;
	}

	public void setOrderType(Byte orderType) {
		this.orderType = orderType;
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

	public Byte getUsable() {
		return usable;
	}

	public void setUsable(Byte usable) {
		this.usable = usable;
	}

	public SysRegion getToProvince() {
		return toProvince;
	}

	public void setToProvince(SysRegion toProvince) {
		this.toProvince = toProvince;
	}

	public SysRegion getToCity() {
		return toCity;
	}

	public void setToCity(SysRegion toCity) {
		this.toCity = toCity;
	}

	public SysRegion getToRegion() {
		return toRegion;
	}

	public void setToRegion(SysRegion toRegion) {
		this.toRegion = toRegion;
	}
}
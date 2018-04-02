/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.dto;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 统计总的会员数，采购中心数，订单数量，总额、已收货款，商品数量 平均客单价Entity
 * @author 张腾飞
 * @version 2018-03-13
 */
public class BizTotalStatisticsDto {

	private static final long serialVersionUID = 1L;
	private Integer custCount;        // 会员数
	private Integer centCount;        // 采购中心数
	/*private Integer supplyCount;		//网供数量
	private Integer capitalCount;		//配资中心数量*/
	private String unit;		//单位
	private Integer orderCount;        // 订单数
	private BigDecimal totalMoney;      //总额
	private BigDecimal receiveMoney;		//已收货款
	private Integer skuCount;		//商品数量
	private BigDecimal avgPrice;		//平均客单价
	private String count;		//用来存所有的数量


	public BizTotalStatisticsDto() {
		super();
	}

	public Integer getCustCount() {
		return custCount;
	}

	public void setCustCount(Integer custCount) {
		this.custCount = custCount;
	}

	public Integer getCentCount() {
		return centCount;
	}

	public void setCentCount(Integer centCount) {
		this.centCount = centCount;
	}

	public Integer getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(Integer orderCount) {
		this.orderCount = orderCount;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Integer getSkuCount() {
		return skuCount;
	}

	public void setSkuCount(Integer skuCount) {
		this.skuCount = skuCount;
	}

	public BigDecimal getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(BigDecimal avgPrice) {
		this.avgPrice = avgPrice;
	}

	public BigDecimal getReceiveMoney() {
		return receiveMoney;
	}

	public void setReceiveMoney(BigDecimal receiveMoney) {
		this.receiveMoney = receiveMoney;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCount() {
		if (StringUtils.isNotBlank(count) && count.length() > 5) {
			DecimalFormat df = new DecimalFormat("#,###.00");
			return df.format(Double.valueOf(count));
		}
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	/*	public Integer getSupplyCount() {
		return supplyCount;
	}

	public void setSupplyCount(Integer supplyCount) {
		this.supplyCount = supplyCount;
	}

	public Integer getCapitalCount() {
		return capitalCount;
	}

	public void setCapitalCount(Integer capitalCount) {
		this.capitalCount = capitalCount;
	}*/
}
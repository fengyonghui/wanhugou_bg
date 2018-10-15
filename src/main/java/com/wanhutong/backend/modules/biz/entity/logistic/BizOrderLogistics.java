/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.logistic;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 运单配置Entity
 * @author wangby
 * @version 2018-09-26
 */
public class BizOrderLogistics extends DataEntity<BizOrderLogistics> {
	
	private static final long serialVersionUID = 1L;
	private Integer orderId;		// order_id
	private String logisticsLines;		// logistics_lines
	private String logisticsCompany;		// 物流公司
	private String logisticsLinesCode;		// 线路code
	private String logisticsLinesNum;		// 线路编号
	private Integer category;		// 线路类别（1=发货线路，2=到货线路）
	private String stopPointCode;		// stop_point_code
	private Double logisticsMoney;		// logistics_money
	private Integer logisticStatus;		// 1.为已选  0. 备选
	private String startPointCode;		// start_point_code
	
	public BizOrderLogistics() {
		super();
	}

	public BizOrderLogistics(Integer id){
		super(id);
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getLogisticsLines() {
		return logisticsLines;
	}

	public void setLogisticsLines(String logisticsLines) {
		this.logisticsLines = logisticsLines;
	}

	public String getLogisticsCompany() {
		return logisticsCompany;
	}

	public void setLogisticsCompany(String logisticsCompany) {
		this.logisticsCompany = logisticsCompany;
	}

	public String getLogisticsLinesCode() {
		return logisticsLinesCode;
	}

	public void setLogisticsLinesCode(String logisticsLinesCode) {
		this.logisticsLinesCode = logisticsLinesCode;
	}

	public String getLogisticsLinesNum() {
		return logisticsLinesNum;
	}

	public void setLogisticsLinesNum(String logisticsLinesNum) {
		this.logisticsLinesNum = logisticsLinesNum;
	}
	
	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getStopPointCode() {
		return stopPointCode;
	}

	public void setStopPointCode(String stopPointCode) {
		this.stopPointCode = stopPointCode;
	}

	public Double getLogisticsMoney() {
		return logisticsMoney;
	}

	public void setLogisticsMoney(Double logisticsMoney) {
		this.logisticsMoney = logisticsMoney;
	}

	public Integer getLogisticStatus() {
		return logisticStatus;
	}

	public void setLogisticStatus(Integer logisticStatus) {
		this.logisticStatus = logisticStatus;
	}

	public String getStartPointCode() {
		return startPointCode;
	}

	public void setStartPointCode(String startPointCode) {
		this.startPointCode = startPointCode;
	}
	
}
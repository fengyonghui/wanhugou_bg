/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务费设置Entity
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
public class BizFreightConfig extends DataEntity<BizFreightConfig> {
	
	private static final long serialVersionUID = 1L;

	/**
	 * sys_office.id 对应采购中心的id
	 */
	private Office office;

	/**
	 * 1: 客户自提 2:送货到家 3:厂家直发
	 */
	private Byte type;

	/**
	 * biz_variety_info.id 对应的品类id， 0：对应其他品类
	 */
	private BizVarietyInfo varietyInfo;

	/**
	 * 距离采购中心的公里数。范围的最小值。
	 */
	private String minDistance;

	/**
	 * 距离采购中心的公里数。范围的最大值。
	 */
	private String maxDistance;

	/**
	 * 运费
	 */
	private BigDecimal feeCharge;

	/**
	 * 1: 无法计算距离时，用此默认设置； 0：飞默认
	 */
	private Byte defaultStatus;

	/**
	 * 用于页面传值
	 */
	private List<BizFreightConfig> freightConfigList;

	public BizFreightConfig() {
		super();
	}

	public BizFreightConfig(Integer id){
		super(id);
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	public String getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(String minDistance) {
		this.minDistance = minDistance;
	}
	
	public String getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(String maxDistance) {
		this.maxDistance = maxDistance;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}

	public BigDecimal getFeeCharge() {
		return feeCharge;
	}

	public void setFeeCharge(BigDecimal feeCharge) {
		this.feeCharge = feeCharge;
	}

	public Byte getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(Byte defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public List<BizFreightConfig> getFreightConfigList() {
		return freightConfigList;
	}

	public void setFreightConfigList(List<BizFreightConfig> freightConfigList) {
		this.freightConfigList = freightConfigList;
	}
}
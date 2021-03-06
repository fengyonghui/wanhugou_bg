/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.integration;

import com.wanhutong.backend.modules.sys.entity.Office;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 积分流水Entity
 * @author LX
 * @version 2018-09-16
 */
public class BizMoneyRecode extends DataEntity<BizMoneyRecode> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// 采购商id
	private BigDecimal money;		// 流水数量
	private Integer orderId;		// 订单id
	private Integer statusCode;		// 10获得-注册成功11获得-下单支付12获得-活动赠送20订单抵扣冻结21使用22订单取消抵扣30过期
	private String statusName;		// 流水类型
	private String comment;		// 流水说明
	private Integer status;		// 1有效0无效
	private Integer uVersion;		// u_version
	private Date beginCreateDate;		// 开始 生成时间
	private Date endCreateDate;		// 结束 生成时间
	private List<Integer> officeIds;
	//可用
	private BigDecimal newMoney;
	//获得
	private BigDecimal gainIntegration;
	//使用
	private BigDecimal usedIntegration;
	//过期
	private BigDecimal expireIntegration;
	private Date createDate;
	private Date updateDate;
	private Integer createId;
	private Integer updateId;

	public BigDecimal getGainIntegration() {
		return gainIntegration;
	}

	public void setGainIntegration(BigDecimal gainIntegration) {
		this.gainIntegration = gainIntegration;
	}

	public BigDecimal getUsedIntegration() {
		return usedIntegration;
	}

	public void setUsedIntegration(BigDecimal usedIntegration) {
		this.usedIntegration = usedIntegration;
	}

	public BigDecimal getExpireIntegration() {
		return expireIntegration;
	}

	public void setExpireIntegration(BigDecimal expireIntegration) {
		this.expireIntegration = expireIntegration;
	}

	public Integer getCreateId() {
		return createId;
	}

	public void setCreateId(Integer createId) {
		this.createId = createId;
	}

	public Integer getUpdateId() {
		return updateId;
	}

	public void setUpdateId(Integer updateId) {
		this.updateId = updateId;
	}

	@Override
	public Date getCreateDate() {
		return createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public BigDecimal getNewMoney() {
		return newMoney;
	}

	public void setNewMoney(BigDecimal newMoney) {
		this.newMoney = newMoney;
	}

	public List<Integer> getOfficeIds() {
		return officeIds;
	}

	public void setOfficeIds(List<Integer> officeIds) {
		this.officeIds = officeIds;
	}

	public BizMoneyRecode() {
		super();
	}

	public BizMoneyRecode(Integer id){
		super(id);
	}

	@NotNull(message="采购商id不能为空")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	
	@NotNull(message="10获得-注册成功11获得-下单支付12获得-活动赠送20订单抵扣冻结21使用22订单取消抵扣30过期不能为空")
	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	@Length(min=1, max=11, message="流水类型长度必须介于 1 和 11 之间")
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
	@Length(min=0, max=200, message="流水说明长度必须介于 0 和 50 之间")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@NotNull(message="1有效0无效不能为空")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@NotNull(message="u_version不能为空")
	public Integer getUVersion() {
		return uVersion;
	}

	public void setUVersion(Integer uVersion) {
		this.uVersion = uVersion;
	}
	
	public Date getBeginCreateDate() {
		return beginCreateDate;
	}

	public void setBeginCreateDate(Date beginCreateDate) {
		this.beginCreateDate = beginCreateDate;
	}
	
	public Date getEndCreateDate() {
		return endCreateDate;
	}

	public void setEndCreateDate(Date endCreateDate) {
		this.endCreateDate = endCreateDate;
	}
		
}
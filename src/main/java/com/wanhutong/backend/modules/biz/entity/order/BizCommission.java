/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 佣金付款表Entity
 * @author wangby
 * @version 2018-10-18
 */
public class BizCommission extends DataEntity<BizCommission> {
	
	private static final long serialVersionUID = 1L;
	private Integer totalCommission;		// 总的待付款金额
	private Integer payTotal;		// 实际付款金额
	private String imgUrl;		// 付款凭证图片
	private Date deadline;		// 最后付款时间
	private Date payTime;		// 支付时间
	private String remark;		// 备注
	private Integer bizStatus;		// 当前业务状态
	
	public BizCommission() {
		super();
	}

	public BizCommission(Integer id){
		super(id);
	}

	public Integer getTotalCommission() {
		return totalCommission;
	}

	public void setTotalCommission(Integer totalCommission) {
		this.totalCommission = totalCommission;
	}

	public Integer getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(Integer payTotal) {
		this.payTotal = payTotal;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}
	
}
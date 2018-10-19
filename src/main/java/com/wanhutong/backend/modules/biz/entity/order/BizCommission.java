/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;

/**
 * 佣金付款表Entity
 * @author wangby
 * @version 2018-10-18
 */
public class BizCommission extends DataEntity<BizCommission> {
	
	private static final long serialVersionUID = 1L;
	private BigDecimal totalCommission;		// 总的待付款金额
	private BigDecimal payTotal;		// 实际付款金额
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

	private List<CommonImg> imgList;

	private CommonProcessEntity commonProcess;

	public BigDecimal getTotalCommission() {
		return totalCommission;
	}

	public void setTotalCommission(BigDecimal totalCommission) {
		this.totalCommission = totalCommission;
	}

	public BigDecimal getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(BigDecimal payTotal) {
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

	public enum BizStatus {
		NO_PAY(0, "未支付"),
		ALL_PAY(1, "已支付"),
		;
		private int status;
		private String desc;

		BizStatus(int status, String desc) {
			this.status = status;
			this.desc = desc;
		}

		public int getStatus() {
			return status;
		}

		public String getDesc() {
			return desc;
		}
	}

	public List<CommonImg> getImgList() {
		return imgList;
	}

	public void setImgList(List<CommonImg> imgList) {
		this.imgList = imgList;
	}

	public CommonProcessEntity getCommonProcess() {
		return commonProcess;
	}

	public void setCommonProcess(CommonProcessEntity commonProcess) {
		this.commonProcess = commonProcess;
	}
}
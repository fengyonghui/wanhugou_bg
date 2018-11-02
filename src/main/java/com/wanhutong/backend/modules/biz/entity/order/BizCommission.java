/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomerInfo;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Office;

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

	/**
	 * 当前业务状态，0：未支付， 1：已支付
	 */
	private Integer bizStatus;

	public BizCommission() {
		super();
	}

	public BizCommission(Integer id){
		super(id);
	}

	private List<CommonImg> imgList;

	private CommonProcessEntity commonProcess;

	private Integer sellerId;

	private String str;

    private Office customer;

	/**
	 * 订单id
	 */
	private String orderIds;

	/**
	 * 订单总价
	 */
	private BigDecimal totalDetail;

	/**
	 * 根据代销商姓名搜索
	 */
	private String customerName;

	/**
	 *
	 */
	private String payee;

    /**
     * 根据订单号搜索付款单
     */
	private String orderNum;

	private String orderNumsStr;

	private List<BizCommissionOrder> bizCommissionOrderList;

	private BizCustomerInfo customerInfo;

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

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

	public BigDecimal getTotalDetail() {
		return totalDetail;
	}

	public void setTotalDetail(BigDecimal totalDetail) {
		this.totalDetail = totalDetail;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public BizCustomerInfo getCustomerInfo() {
		return customerInfo;
	}

	public void setCustomerInfo(BizCustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public List<BizCommissionOrder> getBizCommissionOrderList() {
		return bizCommissionOrderList;
	}

	public void setBizCommissionOrderList(List<BizCommissionOrder> bizCommissionOrderList) {
		this.bizCommissionOrderList = bizCommissionOrderList;
	}

	public String getOrderNumsStr() {
		return orderNumsStr;
	}

	public void setOrderNumsStr(String orderNumsStr) {
		this.orderNumsStr = orderNumsStr;
	}
}
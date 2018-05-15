/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.pay;

import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length;//(min=1, max=4, message="支付类型：wx(微信) alipay");
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 交易记录Entity
 * @author OuyangXiutian
 * @version 2018-01-20
 */
public class BizPayRecord extends DataEntity<BizPayRecord> {
	
	private static final long serialVersionUID = 1L;
	private String payNum;		// 支付编号
	private String orderNum;	//订单编号
	private String outTradeNo;		// 支付宝或微信的业务流水号
	private Double payMoney;		// 支付金额
	private BigDecimal originalAmount = new BigDecimal(0);	//原金额
	private BigDecimal cashAmount = new BigDecimal(0);	//现金额
	private Integer payer;		// 支付人
	private Office customer;		// 客户ID
	private Integer bizStatus;		// 支付状态
	private String account;		// 支付账号
	private String toAccount;		// 支付到账户
	private Integer recordType;		// 交易类型：充值、体现、支付
	private String recordTypeName;		// 交易类型名称
	private Integer payType;		// 支付类型：wx(微信) alipay(支付宝)
	private String payTypeName;		// 支付类型名称
	private String tradeReason;		// 交易作用/原因
	private Date trandStartTime;   //交易开始时间
	private Date trandEndTime;     //交易结束时间

	private Integer reqId;      //备货清单Id

	/**
	 * 采购中心
	 * */
	private BizCustomCenterConsultant custConsultant;
	/**
	 * C端删除标记
	 * */
	private String cendDele;
	/**
	 * C端列表查询标识
	 * */
	private String listPayQuery;

	public BizPayRecord() {
		super();
	}

	public BizPayRecord(Integer id){
		super(id);
	}

	@Length(min=1, max=50, message="订单编号长度必须介于 1 和 50 之间")
	public String getPayNum() {
		return payNum;
	}

	public void setPayNum(String payNum) {
		this.payNum = payNum;
	}
	
	@Length(min=0, max=50, message="支付宝或微信的业务流水号长度必须介于 0 和 50 之间")
	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public Integer getPayer() {
		return payer;
	}

	public void setPayer(Integer payer) {
		this.payer = payer;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getToAccount() {
		return toAccount;
	}

	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}

	@Length(min=1, max=10, message="交易类型名称长度必须介于 1 和 10 之间")
	public String getRecordTypeName() {
		return recordTypeName;
	}

	public void setRecordTypeName(String recordTypeName) {
		this.recordTypeName = recordTypeName;
	}

	@Length(min=1, max=20, message="支付类型名称长度必须介于 1 和 20 之间")
	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
	}
	
	@Length(min=0, max=50, message="交易作用/原因长度必须介于 0 和 50 之间")
	public String getTradeReason() {
		return tradeReason;
	}

	public void setTradeReason(String tradeReason) {
		this.tradeReason = tradeReason;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public Integer getRecordType() {
		return recordType;
	}

	public void setRecordType(Integer recordType) {
		this.recordType = recordType;
	}

	public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public Date getTrandStartTime() {
		return trandStartTime;
	}

	public void setTrandStartTime(Date trandStartTime) {
		this.trandStartTime = trandStartTime;
	}

	public Date getTrandEndTime() {
		return trandEndTime;
	}

	public void setTrandEndTime(Date trandEndTime) {
		this.trandEndTime = trandEndTime;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(BigDecimal cashAmount) {
		this.cashAmount = cashAmount;
	}

	public BizCustomCenterConsultant getCustConsultant() {
		return custConsultant;
	}

	public void setCustConsultant(BizCustomCenterConsultant custConsultant) {
		this.custConsultant = custConsultant;
	}

	public String getCendDele() {
		return cendDele;
	}

	public void setCendDele(String cendDele) {
		this.cendDele = cendDele;
	}

	public String getListPayQuery() {
		return listPayQuery;
	}

	public void setListPayQuery(String listPayQuery) {
		this.listPayQuery = listPayQuery;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Integer getReqId() {
		return reqId;
	}

	public void setReqId(Integer reqId) {
		this.reqId = reqId;
	}
}
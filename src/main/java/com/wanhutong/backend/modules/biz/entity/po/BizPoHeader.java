/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购订单表Entity
 * @author liuying
 * @version 2017-12-30
 */
public class BizPoHeader extends DataEntity<BizPoHeader> {
	
	private static final long serialVersionUID = 1L;
	private String orderNum;		// 订单编号-由系统生成；唯一
	private Office vendOffice;		// 供应商ID sys_office.id &amp;  type=vend
	private Double totalDetail;		// 订单详情总价
	private Double totalExp;		// 订单总费用
	private Double freight;		// 运费
	private Byte invStatus;		// 0 不开发票 1 未开发票 3 已开发票
	private Byte bizStatus;		// 业务状态 0未支付；1首付款支付 2全部支付3已发货 4已收货 5 已完成
	private BizPlatformInfo plateformInfo;		// 订单来源； biz_platform_info.id
	private List<BizPoDetail> poDetailList;
	private String orderIds;
	private String reqIds;

	
	public BizPoHeader() {
		super();
	}

	public BizPoHeader(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="订单编号-由系统生成；唯一长度必须介于 1 和 30 之间")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Office getVendOffice() {
		return vendOffice;
	}

	public void setVendOffice(Office vendOffice) {
		this.vendOffice = vendOffice;
	}

	public Double getTotalDetail() {
		return totalDetail;
	}

	public void setTotalDetail(Double totalDetail) {
		this.totalDetail = totalDetail;
	}

	public Double getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(Double totalExp) {
		this.totalExp = totalExp;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Byte getInvStatus() {
		return invStatus;
	}

	public void setInvStatus(Byte invStatus) {
		this.invStatus = invStatus;
	}

	public Byte getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Byte bizStatus) {
		this.bizStatus = bizStatus;
	}

	public BizPlatformInfo getPlateformInfo() {
		return plateformInfo;
	}

	public void setPlateformInfo(BizPlatformInfo plateformInfo) {
		this.plateformInfo = plateformInfo;
	}

	public List<BizPoDetail> getPoDetailList() {
		return poDetailList;
	}

	public void setPoDetailList(List<BizPoDetail> poDetailList) {
		this.poDetailList = poDetailList;
	}

	public String getOrderIds() {
		return orderIds;
	}

	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

	public String getReqIds() {
		return reqIds;
	}

	public void setReqIds(String reqIds) {
		this.reqIds = reqIds;
	}
}
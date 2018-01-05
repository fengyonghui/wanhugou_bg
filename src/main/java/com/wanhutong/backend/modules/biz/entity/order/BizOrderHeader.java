/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Entity
 * @author OuyangXiutian
 * @version 2017-12-20
 */
public class BizOrderHeader extends DataEntity<BizOrderHeader> {
	
	private static final long serialVersionUID = 1L;
	private String orderNum;		// 订单编号-由系统生成；唯一
	private Integer orderType;		// 1: 普通订单 ; 2:帐期采购 3:配资采购
	private Office customer;		// 客户ID sys_office.id &amp;  type=customer
	private Double totalDetail;		// 订单详情总价
	private Double totalExp;		// 订单总费用
	private Double freight;		// 运费
	private Integer invStatus;		// 0 不开发票 1 未开发票 3 已开发票
	private Integer bizStatus;		// 业务状态 0未支付；1首付款支付 2全部支付 3同意发货 4已发货 5客户已收货 6 已完成
	private BizPlatformInfo platformInfo;		// 订单来源； biz_platform_info.id
	private CommonLocation bizLocation;		// 订单收货地址： common_location.id
	private List<BizOrderDetail> orderDetailList;	//查询有多少订单

	private Integer bizStatusStart;
	private Integer bizStatusEnd;

	public List<BizOrderDetail> getOrderDetailList() {
		return orderDetailList;
	}

	public void setOrderDetailList(List<BizOrderDetail> orderDetailList) {
		this.orderDetailList = orderDetailList;
	}

	public BizOrderHeader() {
		super();
	}

	public BizOrderHeader(Integer id){
		super(id);
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public Office getCustomer() {
		return customer;
	}

	public void setCustomer(Office customer) {
		this.customer = customer;
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

    public BizPlatformInfo getPlatformInfo() {
        return platformInfo;
    }

    public void setPlatformInfo(BizPlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }

    public CommonLocation getBizLocation() {
        return bizLocation;
    }

    public void setBizLocation(CommonLocation bizLocation) {
        this.bizLocation = bizLocation;
    }

    public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getInvStatus() {
		return invStatus;
	}

	public void setInvStatus(Integer invStatus) {
		this.invStatus = invStatus;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public Integer getBizStatusStart() {
		return bizStatusStart;
	}

	public void setBizStatusStart(Integer bizStatusStart) {
		this.bizStatusStart = bizStatusStart;
	}

	public Integer getBizStatusEnd() {
		return bizStatusEnd;
	}

	public void setBizStatusEnd(Integer bizStatusEnd) {
		this.bizStatusEnd = bizStatusEnd;
	}
}
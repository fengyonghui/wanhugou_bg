/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.util.Date;
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
	private Double receiveTotal;	//订单已收货款
	private Double totalExp;		// 订单总费用
	private Double freight;			// 运费
	private Integer invStatus;		// 0 不开发票 1 未开发票 3 已开发票
	private Integer bizStatus;		// 业务状态 0未支付；1首付款支付 2全部支付 3同意发货 4已发货 5客户已收货 6 已完成
	private Integer bizType;		//订单运营类型: 1专营订单 2非专营订单
	private BizPlatformInfo platformInfo;		// 订单来源； biz_platform_info.id
	private BizOrderAddress bizLocation;		// 订单收货地址： common_location.id 在1月22改为 biz_order_address.id

	private CommonLocation location;          //订单交货地址
	private List<BizOrderDetail> orderDetailList;	//查询有多少订单

	private Integer bizStatusStart;
	private Integer bizStatusEnd;
	private Integer consultantId ;    //采购顾问ID，用于查询
	private Date deliveryDate; 		//预计到货日期
	private String oneOrder;		// 首次下单 firstOrder ，非首次下单 endOrder
	private Double DiscountPrice;		//优惠价格页面显示
	private Double tobePaid;		//待支付金额

	private String flag;       //标志位
	private String orderNoEditable;		//页面不可编辑标识符
	private String orderDetails;		//页面不可编辑标识符2
	private String clientModify;		//用于客户专员修改跳转

	private String orderNum2;		//用于删除订单页面传值
    private String localSendIds;
    private Integer orderMark;		//用于订单新增地址返回标记

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

	public BizOrderAddress getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(BizOrderAddress bizLocation) {
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

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Integer getBizType() {
		return bizType;
	}

	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}

	public String getOneOrder() {
		return oneOrder;
	}

	public void setOneOrder(String oneOrder) {
		this.oneOrder = oneOrder;
	}

	public Double getDiscountPrice() {
		return DiscountPrice;
	}

	public void setDiscountPrice(Double discountPrice) {
		DiscountPrice = discountPrice;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Double getTobePaid() {
		return tobePaid;
	}

	public void setTobePaid(Double tobePaid) {
		this.tobePaid = tobePaid;
	}

	public Double getReceiveTotal() {
		return receiveTotal;
	}

	public void setReceiveTotal(Double receiveTotal) {
		this.receiveTotal = receiveTotal;
	}

	public CommonLocation getLocation() {
		return location;
	}

	public void setLocation(CommonLocation location) {
		this.location = location;
	}

	public Integer getConsultantId() {
		return consultantId;
	}

	public void setConsultantId(Integer consultantId) {
		this.consultantId = consultantId;
	}

    public String getOrderNoEditable() {
        return orderNoEditable;
    }

    public void setOrderNoEditable(String orderNoEditable) {
        this.orderNoEditable = orderNoEditable;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

	public String getOrderNum2() {
		return orderNum2;
	}

	public void setOrderNum2(String orderNum2) {
		this.orderNum2 = orderNum2;
	}

    public String getLocalSendIds() {
        return localSendIds;
    }

    public void setLocalSendIds(String localSendIds) {
        this.localSendIds = localSendIds;
    }

	public Integer getOrderMark() {
		return orderMark;
	}

	public void setOrderMark(Integer orderMark) {
		this.orderMark = orderMark;
	}
	
	public String getClientModify() {
		return clientModify;
	}
	
	public void setClientModify(String clientModify) {
		this.clientModify = clientModify;
	}
}
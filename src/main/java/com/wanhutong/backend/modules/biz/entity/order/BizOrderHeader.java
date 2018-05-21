/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.common.supcan.annotation.treelist.cols.SupCol;
import com.wanhutong.backend.common.utils.excel.annotation.ExcelField;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;

import java.util.Date;
import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Entity
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
public class BizOrderHeader extends DataEntity<BizOrderHeader> {

    private static final long serialVersionUID = 1L;
    private String orderNum;        // 订单编号-由系统生成；唯一
    private Integer orderType;        // 1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单
    private Office customer;        // 客户ID sys_office.id &amp;  type=customer
    private Double totalDetail;        // 订单详情总价
    private Double receiveTotal;    //订单已收货款
    private Double totalExp;        // 订单总费用
    private Double freight;            // 运费
    private Integer invStatus;        // 0 不开发票 1 未开发票 3 已开发票
    private Integer bizStatus;        // 业务状态 0未支付；1首付款支付 2全部支付 3同意发货 4已发货 5客户已收货 6 已完成
    private Integer bizType;        //订单运营类型: 1专营订单 2非专营订单
    private BizPlatformInfo platformInfo;        // 订单来源； biz_platform_info.id
    private BizOrderAddress bizLocation;        // 订单收货地址： common_location.id 在1月22改为 biz_order_address.id
    /**
     * 卖方ID
     *  if(order_type == 4)｛
     *     seller_id = sys_office.id
     *  ｝else｛
     *     seller_id = 公司office.id
     *  ｝
     * */
    private Office sellers;


    private CommonLocation location;          //订单交货地址
    private List<BizOrderDetail> orderDetailList;    //查询有多少订单

    private Integer bizStatusStart;
    private Integer bizStatusEnd;
    private Byte bizStatusNot;      //不包含状态
    private Integer consultantId;    //客户专员ID，用于查询
    private Integer centerId;         //采购中心
    private Date deliveryDate;        //预计到货日期
    private String oneOrder;        // 首次下单 firstOrder ，非首次下单 endOrder
    private Double DiscountPrice;        //优惠价格页面显示
    private Double tobePaid;        //待支付金额
    private Double totalBuyPrice;        //订单总出厂价
    private String flag;       //标志位
    private String orderNoEditable;        //页面不可编辑标识符
    private String orderDetails;        //页面不可编辑标识符2
    private String clientModify;        //用于客户专员修改跳转
    private Boolean ownGenPoOrder;
    private Integer onlyVendor; //该订单的唯一供应商
    private String itemNo;      //根据sku货号搜索
    private String partNo;      //根据sku编号搜索
    private Integer orderCount; //find List中订单总条数据
    private String name;  //根据供应商名搜索

    private String orderNum2;        //用于删除订单页面传值
    private String localSendIds;
    private Integer orderMark;        //用于订单新增地址返回标记
    private Integer supplyId;       //用于查询本地发货的订单
    private String centersName;    //用于订单列表查询采购中心

    private User con;        //订单所属客户专员
    private Date ordrHeaderStartTime;    //订单创建开始时间
    private Date orderHeaderEedTime;    //订单创建结束时间
    /**
     * 订单更新开始时间
     * */
    private Date orderUpdaStartTime;
    /**
     * 订单更新结束时间
     * */
    private Date orderUpdaEndTime;

    private List<BizPayRecord> BizPayRecordList;
    private String locationAddress;

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    private boolean includeTestData;

    public boolean isIncludeTestData() {
        return includeTestData;
    }

    public void setIncludeTestData(boolean includeTestData) {
        this.includeTestData = includeTestData;
    }

    private BizOrderHeaderUnline orderHeaderUnline;     //线下支付订单独有内容
    private String statu;       //线下支付订单表示

    public List<BizPayRecord> getBizPayRecordList() {
        return BizPayRecordList;
    }

    public void setBizPayRecordList(List<BizPayRecord> bizPayRecordList) {
        BizPayRecordList = bizPayRecordList;
    }

    public List<BizOrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<BizOrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public BizOrderHeader() {
        super();
    }

    public BizOrderHeader(Integer id) {
        super(id);
    }

    public Integer getOrderType() {
        return orderType;
    }

    public String getCentersName() {
        return centersName;
    }

    public Double getTotalDetail() {
        return totalDetail;
    }

    public Double getReceiveTotal() {
        return receiveTotal;
    }

    public Double getTotalExp() {
        return totalExp;
    }

    public Double getFreight() {
        return freight;
    }

    public Integer getInvStatus() {
        return invStatus;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setCustomer(Office customer) {
        this.customer = customer;
    }

    public void setTotalDetail(Double totalDetail) {
        this.totalDetail = totalDetail;
    }

    public void setTotalExp(Double totalExp) {
        this.totalExp = totalExp;
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

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
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

    public Boolean getOwnGenPoOrder() {
        return ownGenPoOrder;
    }

    public void setOwnGenPoOrder(Boolean ownGenPoOrder) {
        this.ownGenPoOrder = ownGenPoOrder;
    }

    public Integer getOnlyVendor() {
        return onlyVendor;
    }

    public void setOnlyVendor(Integer onlyVendor) {
        this.onlyVendor = onlyVendor;
    }

    public Integer getCenterId() {
        return centerId;
    }

    public void setCenterId(Integer centerId) {
        this.centerId = centerId;
    }

    public Double getTotalBuyPrice() {
        return totalBuyPrice;
    }

    public void setTotalBuyPrice(Double totalBuyPrice) {
        this.totalBuyPrice = totalBuyPrice;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public Integer getSupplyId() {
        return supplyId;
    }

    public void setSupplyId(Integer supplyId) {
        this.supplyId = supplyId;
    }

    public void setCentersName(String centersName) {
        this.centersName = centersName;
    }

    public User getCon() {
        return con;
    }

    public void setCon(User con) {
        this.con = con;
    }

    public Date getOrdrHeaderStartTime() {
        return ordrHeaderStartTime;
    }

    public void setOrdrHeaderStartTime(Date ordrHeaderStartTime) {
        this.ordrHeaderStartTime = ordrHeaderStartTime;
    }

    public Date getOrderHeaderEedTime() {
        return orderHeaderEedTime;
    }

    public void setOrderHeaderEedTime(Date orderHeaderEedTime) {
        this.orderHeaderEedTime = orderHeaderEedTime;
    }

    public Office getCustomer() {
        return customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getOrderUpdaStartTime() {
        return orderUpdaStartTime;
    }

    public void setOrderUpdaStartTime(Date orderUpdaStartTime) {
        this.orderUpdaStartTime = orderUpdaStartTime;
    }

    public Date getOrderUpdaEndTime() {
        return orderUpdaEndTime;
    }

    public void setOrderUpdaEndTime(Date orderUpdaEndTime) {
        this.orderUpdaEndTime = orderUpdaEndTime;
    }

    public Byte getBizStatusNot() {
        return bizStatusNot;
    }

    public void setBizStatusNot(Byte bizStatusNot) {
        this.bizStatusNot = bizStatusNot;
    }

    public BizOrderHeaderUnline getOrderHeaderUnline() {
        return orderHeaderUnline;
    }

    public void setOrderHeaderUnline(BizOrderHeaderUnline orderHeaderUnline) {
        this.orderHeaderUnline = orderHeaderUnline;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public Office getSellers() {
        return sellers;
    }

    public void setSellers(Office sellers) {
        this.sellers = sellers;
    }
}
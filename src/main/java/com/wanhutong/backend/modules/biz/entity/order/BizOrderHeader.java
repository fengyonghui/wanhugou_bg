/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.logistic.BizOrderLogistics;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;

import java.math.BigDecimal;
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
    private Double serviceFee;        // 增值服务费
    private Double receiveTotal;    //订单已收货款
    private Double totalExp;        // 订单总费用
    private Double freight;            // 运费
    private Integer invStatus;        // 0 不开发票 1 未开发票 3 已开发票
    private Integer bizStatus;        // 业务状态 0未支付；1首付款支付 2全部支付 3同意发货 4已发货 5客户已收货 6 已完成
    private Integer bizType;        //订单运营类型: 1专营订单 2非专营订单
    private BizPlatformInfo platformInfo;        // 订单来源； biz_platform_info.id
    private BizOrderAddress bizLocation;        // 订单收货地址： common_location.id 在1月22改为 biz_order_address.id
    private Integer sendGoodsStatus;     //区分非拍照下单发货
    private List<BizInvoice> bizInvoiceList;
    /**
     * 卖方ID
     *  if(order_type == 4)｛
     *     seller_id = sys_office.id
     *  ｝else｛
     *     seller_id = 公司office.id
     *  ｝
     * */
    private Office sellers;
    private Integer sellersId;
    /**
     * 订单备注
     * */
    private BizOrderComment orderComment;


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
    private Integer examine;        //线下支付订单审核状态

    private String orderNum2;        //用于删除订单页面传值
    private String localSendIds;
    private Integer orderMark;        //用于订单新增地址返回标记
    private Integer supplyId;       //用于查询本地发货的订单
    private String centersName;    //用于订单列表查询采购中心

    private User con;        //订单所属客户专员
    private String ordrHeaderStartTime;    //订单创建开始时间
    private String orderHeaderEedTime;    //订单创建结束时间
    /**
     * 订单更新开始时间
     * */
    private Date orderUpdaStartTime;
    private Date orderCreatStartTime;
    /**
     * 订单更新结束时间
     * */
    private Date orderUpdaEndTime;
    private Date orderCreatEndTime;

    /**
     * 查看已经生成的采购单 标识符
     * */
    private String poSource;

    private List<BizPayRecord> BizPayRecordList;
    private String locationAddress;

    /**
     * 客户专员、会员搜索 查看订单开始下单时间与该采购商订单总金额，品类主管 管理 的商品统计
     * */
    private Double userOfficeReceiveTotal;
    private Date userOfficeDeta;
    private Integer officeChatRecord;//沟通次数
    private BizChatRecord bizChatRecord;
    private BizSkuInfo skuInfoId;//商品ID
    private String skuChickCount;//下单量标识
    private Integer opShelfCount;//新品发布量
    private Integer skuCount;//商品销售量
    private Integer officeCount;//新增经销店数

    private String photos; //产品图片
    private String imgPhotosSorts;		//主图顺序

    private Integer vendorId; //根据供应商Id搜索
    private String vendorName; //根据供应商名称
    /**
     * 发货单Id
     * */
    private String sendId;
    /**
     * 发货单号
     * */
    private String sendNum;

    /**
     * 退款状态- 0-申请 1审核通过 2驳回 3撤销申请退款
     */
    private BizDrawBack drawBack;


    /**
     * 供应商查看订单 source = vendor
     */
    private String source;
    /**
     * 固定商品的详情总价
     */
    private String detailPrice;
//    /**
//     * 首付金额
//     */
//    private Integer payProportion;
    /**
     * 审核流程
     */
    private CommonProcessEntity commonProcess;

    /**
     * 一单到底对应的采购单
     */
    private BizPoHeader bizPoHeader;

    /**
     * 审核路径参数记录
     */
    private String str;
    /**
     * 审核状态过滤
     */
    private String selectAuditStatus;


    /**
     * 申请支付金额
     */
    private BigDecimal planPay;

    /**
     * 与供应商结算的金额
     */
    private BigDecimal balanceTotal;

    /**
     * 付款时间
     */
    private Date payDeadline;

    /**
     * 已审批流程
     */
    private List<CommonProcessEntity> commonProcessList;
    /**
     * 支付单
     */
    private BizPoPaymentOrder bizPoPaymentOrder;

    /**
     * 是否是产地直发的订单，0:是 721:是  大于0且不等于721：不是
     */
    private Integer suplys;

    /**
     * 流程查询
     */
    private List<String> originCode;
    private List<String> localCode;
    private List<String> doAllCode;
    private List<String> doFifthCode;
    private List<String> poAuditCode;

    private Integer waitPay;

    private Integer retainage;

    /**
     *  采购单当前审核流程名字
     */
    private String poProcessName;

    /**
     * 手机端客户专员订单检索
     * 0：待审核，1：审核失败，2：其他
     */
    private Integer mobileAuditStatus;

    /**
     * 积分抵扣
     */
    private BigDecimal scoreMoney;

    private List<BizOrderLogistics> bizOrderLogisticsList;

    /**
     * 待发货
     */
    private Integer waitShipments;

    /**
     * 待出库
     * @return
     */
    private Integer waitOutput;

    /**
     * 需要出库筛选条件
     */
    private Integer needOut;

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
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

    public String getPoSource() {
        return poSource;
    }

    public void setPoSource(String poSource) {
        this.poSource = poSource;
    }

    public Double getUserOfficeReceiveTotal() {
        return userOfficeReceiveTotal;
    }

    public void setUserOfficeReceiveTotal(Double userOfficeReceiveTotal) {
        this.userOfficeReceiveTotal = userOfficeReceiveTotal;
    }

    public Date getUserOfficeDeta() {
        return userOfficeDeta;
    }

    public void setUserOfficeDeta(Date userOfficeDeta) {
        this.userOfficeDeta = userOfficeDeta;
    }

    public Integer getOfficeChatRecord() {
        return officeChatRecord;
    }

    public void setOfficeChatRecord(Integer officeChatRecord) {
        this.officeChatRecord = officeChatRecord;
    }

    public BizChatRecord getBizChatRecord() {
        return bizChatRecord;
    }

    public void setBizChatRecord(BizChatRecord bizChatRecord) {
        this.bizChatRecord = bizChatRecord;
    }

    public BizSkuInfo getSkuInfoId() {
        return skuInfoId;
    }

    public void setSkuInfoId(BizSkuInfo skuInfoId) {
        this.skuInfoId = skuInfoId;
    }

    public String getSkuChickCount() {
        return skuChickCount;
    }

    public void setSkuChickCount(String skuChickCount) {
        this.skuChickCount = skuChickCount;
    }

    public Integer getOpShelfCount() {
        return opShelfCount;
    }

    public void setOpShelfCount(Integer opShelfCount) {
        this.opShelfCount = opShelfCount;
    }

    public Integer getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(Integer skuCount) {
        this.skuCount = skuCount;
    }

    public Integer getOfficeCount() {
        return officeCount;
    }

    public void setOfficeCount(Integer officeCount) {
        this.officeCount = officeCount;
    }

    public BizOrderComment getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(BizOrderComment orderComment) {
        this.orderComment = orderComment;
    }

    public String getOrdrHeaderStartTime() {
        return ordrHeaderStartTime;
    }

    public void setOrdrHeaderStartTime(String ordrHeaderStartTime) {
        this.ordrHeaderStartTime = ordrHeaderStartTime;
    }

    public String getOrderHeaderEedTime() {
        return orderHeaderEedTime;
    }

    public void setOrderHeaderEedTime(String orderHeaderEedTime) {
        this.orderHeaderEedTime = orderHeaderEedTime;
    }

    public Date getOrderCreatStartTime() {
        return orderCreatStartTime;
    }

    public void setOrderCreatStartTime(Date orderCreatStartTime) {
        this.orderCreatStartTime = orderCreatStartTime;
    }

    public Date getOrderCreatEndTime() {
        return orderCreatEndTime;
    }

    public void setOrderCreatEndTime(Date orderCreatEndTime) {
        this.orderCreatEndTime = orderCreatEndTime;
    }

    public Integer getExamine() {
        return examine;
    }

    public Integer getSellersId() {
        return sellersId;
    }

    public void setSellersId(Integer sellersId) {
        this.sellersId = sellersId;
    }

    public void setExamine(Integer examine) {
        this.examine = examine;
    }

    public Integer getSendGoodsStatus() {
        return sendGoodsStatus;
    }

    public void setSendGoodsStatus(Integer sendGoodsStatus) {
        this.sendGoodsStatus = sendGoodsStatus;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getImgPhotosSorts() {
        return imgPhotosSorts;
    }

    public void setImgPhotosSorts(String imgPhotosSorts) {
        this.imgPhotosSorts = imgPhotosSorts;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getSendNum() {
        return sendNum;
    }

    public void setSendNum(String sendNum) {
        this.sendNum = sendNum;
    }

    public BizDrawBack getDrawBack() {
        return drawBack;
    }

    public void setDrawBack(BizDrawBack drawBack) {
        this.drawBack = drawBack;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDetailPrice() {
        return detailPrice;
    }

    public void setDetailPrice(String detailPrice) {
        this.detailPrice = detailPrice;
    }

//    public Integer getPayProportion() {
//        return payProportion;
//    }
//
//    public void setPayProportion(Integer payProportion) {
//        this.payProportion = payProportion;
//    }

    public CommonProcessEntity getCommonProcess() {
        return commonProcess;
    }

    public void setCommonProcess(CommonProcessEntity commonProcess) {
        this.commonProcess = commonProcess;
    }

    public BizPoHeader getBizPoHeader() {
        return bizPoHeader;
    }

    public void setBizPoHeader(BizPoHeader bizPoHeader) {
        this.bizPoHeader = bizPoHeader;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public List<CommonProcessEntity> getCommonProcessList() {
        return commonProcessList;
    }

    public void setCommonProcessList(List<CommonProcessEntity> commonProcessList) {
        this.commonProcessList = commonProcessList;
    }

    public BizPoPaymentOrder getBizPoPaymentOrder() {
        return bizPoPaymentOrder;
    }

    public void setBizPoPaymentOrder(BizPoPaymentOrder bizPoPaymentOrder) {
        this.bizPoPaymentOrder = bizPoPaymentOrder;
    }

    public Integer getSuplys() {
        return suplys;
    }

    public void setSuplys(Integer suplys) {
        this.suplys = suplys;
    }

    public List<BizInvoice> getBizInvoiceList() {
        return bizInvoiceList;
    }

    public void setBizInvoiceList(List<BizInvoice> bizInvoiceList) {
        this.bizInvoiceList = bizInvoiceList;
    }

    public BigDecimal getPlanPay() {
        return planPay;
    }

    public void setPlanPay(BigDecimal planPay) {
        this.planPay = planPay;
    }

    public Date getPayDeadline() {
        return payDeadline;
    }

    public void setPayDeadline(Date payDeadline) {
        this.payDeadline = payDeadline;
    }

    public String getSelectAuditStatus() {
        return selectAuditStatus;
    }

    public void setSelectAuditStatus(String selectAuditStatus) {
        this.selectAuditStatus = selectAuditStatus;
    }

    public List<String> getOriginCode() {
        return originCode;
    }

    public void setOriginCode(List<String> originCode) {
        this.originCode = originCode;
    }

    public List<String> getLocalCode() {
        return localCode;
    }

    public void setLocalCode(List<String> localCode) {
        this.localCode = localCode;
    }

    public List<String> getDoAllCode() {
        return doAllCode;
    }

    public void setDoAllCode(List<String> doAllCode) {
        this.doAllCode = doAllCode;
    }

    public List<String> getDoFifthCode() {
        return doFifthCode;
    }

    public void setDoFifthCode(List<String> doFifthCode) {
        this.doFifthCode = doFifthCode;
    }

    public List<String> getPoAuditCode() {
        return poAuditCode;
    }

    public void setPoAuditCode(List<String> poAuditCode) {
        this.poAuditCode = poAuditCode;
    }

    public Integer getWaitPay() {
        return waitPay;
    }

    public void setWaitPay(Integer waitPay) {
        this.waitPay = waitPay;
    }

    public String getPoProcessName() {
        return poProcessName;
    }

    public void setPoProcessName(String poProcessName) {
        this.poProcessName = poProcessName;
    }

    public Integer getRetainage() {
        return retainage;
    }

    public void setRetainage(Integer retainage) {
        this.retainage = retainage;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Integer getMobileAuditStatus() {
        return mobileAuditStatus;
    }

    public void setMobileAuditStatus(Integer mobileAuditStatus) {
        this.mobileAuditStatus = mobileAuditStatus;
    }

    public BigDecimal getScoreMoney() {
        return scoreMoney;
    }

    public void setScoreMoney(BigDecimal scoreMoney) {
        this.scoreMoney = scoreMoney;
    }

    public List<BizOrderLogistics> getBizOrderLogisticsList() {
        return bizOrderLogisticsList;
    }

    public void setBizOrderLogisticsList(List<BizOrderLogistics> bizOrderLogisticsList) {
        this.bizOrderLogisticsList = bizOrderLogisticsList;
    }

    public Integer getWaitShipments() {
        return waitShipments;
    }

    public void setWaitShipments(Integer waitShipments) {
        this.waitShipments = waitShipments;
    }

    public Integer getWaitOutput() {
        return waitOutput;
    }

    public void setWaitOutput(Integer waitOutput) {
        this.waitOutput = waitOutput;
    }

    public Integer getNeedOut() {
        return needOut;
    }

    public void setNeedOut(Integer needOut) {
        this.needOut = needOut;
    }
}


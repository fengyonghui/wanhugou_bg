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
    private Integer orderType;        // 1: 普通订单 ; 2:帐期采购 3:配资采购
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

    private CommonLocation location;          //订单交货地址
    private List<BizOrderDetail> orderDetailList;    //查询有多少订单

    private Integer bizStatusStart;
    private Integer bizStatusEnd;
    private Integer consultantId;    //采购顾问ID，用于查询
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

    private String orderNum2;        //用于删除订单页面传值
    private String localSendIds;
    private Integer orderMark;        //用于订单新增地址返回标记
    private String suplyIds;       //用于查询本地发货的订单
    private String centersName;    //用于订单列表查询采购中心

    private User con;        //订单所属客户专员


    private Date ordrHeaderStartTime;    //订单创建开始时间
    private Date orderHeaderEedTime;    //订单创建结束时间
    /**
     * 以下属性用于订单导出的 列表标题
     */
    private String customePhone;  //采购商名称/电话
    private Double payableTotail; //应付金额
    private String orderDeilList;   //商品名称
    private Integer orderDeilleng;   //商品数量
    private Double totailProfit;   //利润
    private Integer bizBusiness;   //业务状态
    private String bizStae;     //尾款信息
    private String platformName;   //订单来源
    private String addreReceiver;        // 收货人姓名
    private String addrePhone;        // 收货人联系电话
    private String oderAddress;     //订单收货地址
    private String createName;     //创建人
    private String updateName;     //更新人
    private Integer RecodList;    //交易记录
    private String recodLoop;   //记录凭证

    /*
     * 用于订单导出查询平台交易记录
     * */
    private List<BizPayRecord> BizPayRecordList;

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

    @SupCol(isUnique = "true", isHide = "true")
    @ExcelField(title = "ID", type = 1, align = 2, sort = 1)
    public Integer getId() {
        return id;
    }

    @ExcelField(title = "订单编号", align = 2, sort = 10)
    public String getOrderNum() {
        return orderNum;
    }

    @ExcelField(title = "订单类型", align = 2, sort = 20, dictType = "biz_order_type")
    public Integer getOrderType() {
        return orderType;
    }

    @ExcelField(title = "采购商名称/电话", align = 2, sort = 30)
    public String getCustomePhone() {
        return customePhone = customer.getName() + "(" + customer.getPhone() + ")";
    }

    @ExcelField(title = "所属采购中心", align = 2, sort = 40)
    public String getCentersName() {
        return centersName;
    }

    @ExcelField(title = "所购商品名称（编号）", align = 2, sort = 50)
    public String getOrderDeilList() {
        String q = "";
        if (orderDetailList.size() != 0) {
            for (int i = 0; i < orderDetailList.size(); i++) {
                q += orderDetailList.get(i).getSkuName() + "(" + orderDetailList.get(i).getPartNo() + ")，";
            }
        }
        return orderDeilList = q;
    }

    @ExcelField(title = "商品数量", align = 2, sort = 53)
    public Integer getOrderDeilleng() {
        return orderDeilleng = orderDetailList.size();
    }

    @ExcelField(title = "商品总价", align = 2, sort = 60)
    public Double getTotalDetail() {
        return totalDetail;
    }

    @ExcelField(title = "已收货款", align = 2, sort = 70)
    public Double getReceiveTotal() {
        return receiveTotal;
    }

    @ExcelField(title = "交易金额", align = 2, sort = 80)
    public Double getTotalExp() {
        return totalExp;
    }

    @ExcelField(title = "运费", align = 2, sort = 90)
    public Double getFreight() {
        return freight;
    }

    @ExcelField(title = "应付金额", align = 2, sort = 100)
    public Double getPayableTotail() {
        return payableTotail = totalDetail + totalExp + freight;
    }

    @ExcelField(title = "利润", align = 2, sort = 110)
    public Double getTotailProfit() {
        return totailProfit = totalDetail + totalExp + freight - totalBuyPrice;
    }

    @ExcelField(title = "发票状态", align = 2, sort = 120, dictType = "biz_order_invStatus")
    public Integer getInvStatus() {
        return invStatus;
    }

    @ExcelField(title = "业务状态", align = 2, sort = 130, dictType = "biz_order_status")
    public Integer getBizBusiness() {
        return bizBusiness = bizStatus;
    }

    @ExcelField(title = "尾款信息", align = 2, sort = 135)
    public String getBizStae() {
        String aa = "";
        if (bizStatus != 10 && bizStatus != 40 && totalDetail + totalExp + freight != receiveTotal) {
            bizStae = aa + "有尾款";
        }
        return bizStae;
    }

    @ExcelField(title = "订单来源", align = 2, sort = 140)
    public String getPlatformName() {
        return platformName = platformInfo.getName();
    }

    @ExcelField(title = "支付类型", align = 2, sort = 142, dictType = "payType")
    public Integer getRecodList() {
        if (BizPayRecordList.size() != 0) {
            for (int j = 0; j < BizPayRecordList.size(); j++) {
                RecodList = BizPayRecordList.get(j).getPayType();
            }
        }
        return RecodList;
    }

    @ExcelField(title = "业务流水号", align = 2, sort = 143)
    public String getRecodLoop() {
        String a = "";
        if (BizPayRecordList.size() != 0) {
            for (int j = 0; j < BizPayRecordList.size(); j++) {
                a += BizPayRecordList.get(j).getOutTradeNo()+" ";
            }
        }
        return recodLoop = a;
    }

    @ExcelField(title = "收货人", align = 2, sort = 144)
    public String getAddreReceiver() {
        return addreReceiver = bizLocation.getReceiver();
    }

    @ExcelField(title = "联系电话", align = 2, sort = 146)
    public String getAddrePhone() {
        return addrePhone = bizLocation.getPhone();
    }

    @ExcelField(title = "订单收货地址", align = 2, sort = 150)
    public String getOderAddress() {
        return oderAddress = bizLocation.getProvince().getName() + "" + bizLocation.getCity().getName() + "" +
                bizLocation.getRegion().getName() + "" + bizLocation.getAddress() + "";
    }

    @ExcelField(title = "创建人", align = 2, sort = 160)
    public String getCreateName() {
        return createName = createBy.getName();
    }

    @ExcelField(title = "创建时间", type = 0, align = 2, sort = 170)
    public Date getCreateDate() {
        return createDate;
    }

    @ExcelField(title = "更新人", align = 2, sort = 180)
    public String getUpdateName() {
        return updateName = updateBy.getName();
    }

    @ExcelField(title = "更新时间", type = 0, align = 2, sort = 190)
    public Date getUpdateDate() {
        return updateDate;
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

    public String getSuplyIds() {
        return suplyIds;
    }

    public void setSuplyIds(String suplyIds) {
        this.suplyIds = suplyIds;
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
}
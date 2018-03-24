/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.common.supcan.annotation.treelist.cols.SupCol;
import com.wanhutong.backend.common.utils.excel.annotation.ExcelField;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

/**
 * 订单详情(销售订单)Entity
 *
 * @author OuyangXiutian
 * @version 2017-12-22
 */
public class BizOrderDetail extends DataEntity<BizOrderDetail> {

    private static final long serialVersionUID = 1L;
    private BizOrderHeader orderHeader;        // biz_order_header.id
    private Integer lineNo;        // 订单详情行号
    private Integer pLineNo;        // bom产品 kit
    private BizOpShelfSku shelfInfo;  //货架ID biz_op_shelf_info.id
    private BizSkuInfo skuInfo;        // 产品biz_sku_info.id
    private String partNo;        // 商品编号
    private String skuName;        // 商品名称
    private Double unitPrice;        // 商品单价
    private Double buyPrice;        //出厂价
    private Integer sentQty;        //发货数量
    private Integer ordQty;        // 采购数量
    private String quality;        //材质
    private String color;        //颜色
    private String standard;    //规格
    private Office suplyis;     //供货中心ID，sys_office.id; 默认本地备货对应采购中心id；其他货架对应供货中心ID；此值由采购专员同意发货前确定
    private Integer sendNum;        //存储页面传入的供货数量
    private Integer ordQtyUpda;        //用于存储修改时的采购数量
    private List<BizOpShelfSku> shelfList;    //用于计算有多少货架
    private String orderDetaIds;        //用于多选商品时传递数据
    private String saleQtys;          //用于采购数量时传递数量
    private String shelfSkus;          //用于多选货架是传递数量
    private List<BizOrderDetail> orderHeaderList;   //用于查询该订单下有多少商品
    private BizInventoryInfo inventoryInfo;     //仓库
    private List<BizOrderSkuPropValue> orderSkuValueList;   //sku属性值

    private String detailIds;
    private Integer totalReqQty;
    private Integer totalSendQty;
    private Integer vendorId;
    private String vendorName;
    private String suplyIds;
    private Office vendor;      //供应商
    private User primary;       //供应商主联系人


    /**
     * 以下属性用于订单导出的 列表标题
     */
    private Integer headeId; //订单id
    private String orderHNum;   //订单编号
    private Integer headerType;//订单类型
    private String customePhone;  //采购商名称/电话
    private String centersName;//所属采购中心
    private Double payableTotail; //应付金额
    private String orderDeilList;   //商品名称
    private Double skuPrice;    //商品单价
    private Integer orderDeilleng;   //采购数量
    private Double skuTotail;//商品总价
    private Double ceiveTotal;//已收货款
    private Double totalExp;//交易金额
    private Double freight;//运费
    private Double totailProfit;   //利润
    private Integer invStatus;//发票状态
    private Integer bizBusiness;   //业务状态
    private String bizStae;     //尾款信息
    private String platformName;   //订单来源
    private Integer RecodList;    //交易记录
    private String addreReceiver;        // 收货人姓名
    private String addrePhone;        // 收货人联系电话
    private String recodLoop;   //记录凭证
    private String oderAddress;     //订单收货地址
    private String createName;     //创建人
    private Date crDate;    //创建时间
    private String updateName;     //更新人
    private Date upDate; //更新时间
    private BizOrderDetail orderDaillist;//商品
    public BizOrderDetail getOrderDaillist() {
        return orderDaillist;
    }

    public void setOrderDaillist(BizOrderDetail orderDaillist) {
        this.orderDaillist = orderDaillist;
    }

    public List<BizOrderDetail> getOrderHeaderList() {
        return orderHeaderList;
    }

    public void setOrderHeaderList(List<BizOrderDetail> orderHeaderList) {
        this.orderHeaderList = orderHeaderList;
    }

    @SupCol(isUnique = "true", isHide = "true")
    @ExcelField(title = "ID", type = 1, align = 2, sort = 1)
    public Integer getHeadeId() {
        return headeId = orderHeader.getId();

    }

    @ExcelField(title = "订单编号", align = 2, sort = 10)
    public String getOrderHNum() {
        return orderHNum = orderHeader.getOrderNum();
    }

    @ExcelField(title = "订单类型", align = 2, sort = 20, dictType = "biz_order_type")
    public Integer getHeaderType() {
        return headerType = orderHeader.getOrderType();
    }

    @ExcelField(title = "采购商名称（电话）", align = 2, sort = 30)
    public String getCustomePhone() {
        return customePhone = orderHeader.getCustomer().getName() + "(" + orderHeader.getCustomer().getPhone() + ")";
    }

    @ExcelField(title = "所属采购中心", align = 2, sort = 40)
    public String getCentersName() {
        return centersName = orderHeader.getCentersName();
    }

    @ExcelField(title = "商品名称（编号）", align = 2, sort = 50)
    public String getOrderDeilList() {
        if (orderDaillist != null) {
            orderDeilList = orderDaillist.getSkuName() + "(" + orderDaillist.getPartNo() + ")";
        }
        return orderDeilList;
    }

    @ExcelField(title = "商品单价", align = 2, sort = 60)
    public Double getSkuPrice() {
        if (orderDaillist != null) {
            skuPrice = orderDaillist.getUnitPrice();
        }
        return skuPrice;
    }

    @ExcelField(title = "采购数量", align = 2, sort = 70)
    public Integer getOrderDeilleng() {
        if (orderDaillist != null) {
            orderDeilleng = orderDaillist.getOrdQty();
        }
        return orderDeilleng;
    }

    @ExcelField(title = "订单_商品总价", align = 2, sort = 80)
    public Double getSkuTotail() {
        return skuTotail = orderHeader.getTotalDetail();
    }

    @ExcelField(title = "订单_已收货款", align = 2, sort = 90)
    public Double getCeiveTotal() {
        return ceiveTotal = orderHeader.getReceiveTotal();
    }

    @ExcelField(title = "订单_交易金额", align = 2, sort = 100)
    public Double getTotalExp() {
        return totalExp = orderHeader.getTotalExp();
    }

    @ExcelField(title = "运费", align = 2, sort = 110)
    public Double getFreight() {
        return freight = orderHeader.getFreight();
    }

    @ExcelField(title = "应付金额", align = 2, sort = 120)
    public Double getPayableTotail() {
        return payableTotail = orderHeader.getTotalDetail() + orderHeader.getTotalExp() + orderHeader.getFreight();
    }

    @ExcelField(title = "利润", align = 2, sort = 130)
    public Double getTotailProfit() {
        return totailProfit = orderHeader.getTotalDetail() + orderHeader.getTotalExp() + orderHeader.getFreight() - orderHeader.getTotalBuyPrice();
    }

    @ExcelField(title = "发票状态", align = 2, sort = 130, dictType = "biz_order_invStatus")
    public Integer getInvStatus() {
        return invStatus = orderHeader.getInvStatus();
    }

    @ExcelField(title = "业务状态", align = 2, sort = 140, dictType = "biz_order_status")
    public Integer getBizBusiness() {
        return bizBusiness = orderHeader.getBizStatus();
    }

    @ExcelField(title = "尾款信息", align = 2, sort = 150)
    public String getBizStae() {
        String aa = "";
        if (orderHeader.getBizStatus() != 10 && orderHeader.getBizStatus() != 40 &&
                orderHeader.getTotalDetail() + orderHeader.getTotalExp() + orderHeader.getFreight() != orderHeader.getReceiveTotal()) {
            bizStae = aa + "有尾款";
        }
        return bizStae;
    }

    @ExcelField(title = "订单来源", align = 2, sort = 160)
    public String getPlatformName() {
        return platformName = orderHeader.getPlatformInfo().getName();
    }

    @ExcelField(title = "支付类型", align = 2, sort = 170, dictType = "payType")
    public Integer getRecodList() {
        if (orderHeader.getBizPayRecordList().size() != 0) {
            for (int j = 0; j < orderHeader.getBizPayRecordList().size(); j++) {
                RecodList = orderHeader.getBizPayRecordList().get(j).getPayType();
            }
        }
        return RecodList;
    }

    @ExcelField(title = "业务流水号", align = 2, sort = 180)
    public String getRecodLoop() {
        String a = "";
        if (orderHeader.getBizPayRecordList().size() != 0) {
            for (int j = 0; j < orderHeader.getBizPayRecordList().size(); j++) {
                a += orderHeader.getBizPayRecordList().get(j).getOutTradeNo() + "，";
            }
        }
        return recodLoop = a;
    }

    @ExcelField(title = "收货人", align = 2, sort = 190)
    public String getAddreReceiver() {
        return addreReceiver = orderHeader.getBizLocation().getReceiver();
    }

    @ExcelField(title = "联系电话", align = 2, sort = 200)
    public String getAddrePhone() {
        return addrePhone = orderHeader.getBizLocation().getPhone();
    }

    @ExcelField(title = "订单收货地址", align = 2, sort = 210)
    public String getOderAddress() {
        return oderAddress = orderHeader.getBizLocation().getProvince().getName() + "" + orderHeader.getBizLocation().getCity().getName() + "" +
                orderHeader.getBizLocation().getRegion().getName() + "" + orderHeader.getBizLocation().getAddress() + "";
    }

    @ExcelField(title = "创建人", align = 2, sort = 220)
    public String getCreateName() {
        return createName = orderHeader.getCreateBy().getName();
    }

    @ExcelField(title = "创建时间", type = 0, align = 2, sort = 230)
    public Date getCrDate() {
        return crDate = orderHeader.getCreateDate();
    }

    @ExcelField(title = "更新人", align = 2, sort = 240)
    public String getUpdateName() {
        return updateName = orderHeader.getUpdateBy().getName();
    }

    @ExcelField(title = "更新时间", type = 0, align = 2, sort = 250)
    public Date getUpDate() {
        return upDate = orderHeader.getUpdateDate();
    }

    public BizOpShelfSku getShelfInfo() {
        return shelfInfo;
    }

    public void setShelfInfo(BizOpShelfSku shelfInfo) {
        this.shelfInfo = shelfInfo;
    }

    private Integer orderId;    //用于修改跳转地址保存修改信息后跳转

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BizOrderDetail() {
        super();
    }

    public BizOrderDetail(Integer id) {
        super(id);
    }

    //	@id长度必须介于 1 和 11 之间")
    public BizOrderHeader getOrderHeader() {
        return orderHeader;
    }

    public void setOrderHeader(BizOrderHeader orderHeader) {
        this.orderHeader = orderHeader;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getpLineNo() {
        return pLineNo;
    }

    public void setpLineNo(Integer pLineNo) {
        this.pLineNo = pLineNo;
    }

    @Length(min = 0, max = 30, message = "商品编号长度必须介于 0 和 30 之间")
    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    @Length(min = 1, max = 30, message = "商品名称长度必须介于 1 和 30 之间")
    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }


    public BizSkuInfo getSkuInfo() {
        return skuInfo;
    }

    public void setSkuInfo(BizSkuInfo skuInfo) {
        this.skuInfo = skuInfo;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getOrdQty() {
        return ordQty;
    }

    public void setOrdQty(Integer ordQty) {
        this.ordQty = ordQty;
    }

    public Integer getSentQty() {
        return sentQty;
    }

    public void setSentQty(Integer sentQty) {
        this.sentQty = sentQty;
    }

    public List<BizOpShelfSku> getShelfList() {
        return shelfList;
    }

    public void setShelfList(List<BizOpShelfSku> shelfList) {
        this.shelfList = shelfList;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public Integer getOrdQtyUpda() {
        return ordQtyUpda;
    }

    public void setOrdQtyUpda(Integer ordQtyUpda) {
        this.ordQtyUpda = ordQtyUpda;
    }

    public String getOrderDetaIds() {
        return orderDetaIds;
    }

    public void setOrderDetaIds(String orderDetaIds) {
        this.orderDetaIds = orderDetaIds;
    }

    public String getSaleQtys() {
        return saleQtys;
    }

    public void setSaleQtys(String saleQtys) {
        this.saleQtys = saleQtys;
    }

    public String getShelfSkus() {
        return shelfSkus;
    }

    public void setShelfSkus(String shelfSkus) {
        this.shelfSkus = shelfSkus;
    }

    public List<BizOrderSkuPropValue> getOrderSkuValueList() {
        return orderSkuValueList;
    }

    public void setOrderSkuValueList(List<BizOrderSkuPropValue> orderSkuValueList) {
        this.orderSkuValueList = orderSkuValueList;
    }

    public String getDetailIds() {
        return detailIds;
    }

    public void setDetailIds(String detailIds) {
        this.detailIds = detailIds;
    }

    public Integer getTotalReqQty() {
        return totalReqQty;
    }

    public void setTotalReqQty(Integer totalReqQty) {
        this.totalReqQty = totalReqQty;
    }

    public Integer getTotalSendQty() {
        return totalSendQty;
    }

    public void setTotalSendQty(Integer totalSendQty) {
        this.totalSendQty = totalSendQty;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Office getSuplyis() {
        return suplyis;
    }

    public void setSuplyis(Office suplyis) {
        this.suplyis = suplyis;
    }

    public String getSuplyIds() {
        return suplyIds;
    }

    public void setSuplyIds(String suplyIds) {
        this.suplyIds = suplyIds;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Office getVendor() {
        return vendor;
    }

    public void setVendor(Office vendor) {
        this.vendor = vendor;
    }

    public User getPrimary() {
        return primary;
    }

    public void setPrimary(User primary) {
        this.primary = primary;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public BizInventoryInfo getInventoryInfo() {
        return inventoryInfo;
    }

    public void setInventoryInfo(BizInventoryInfo inventoryInfo) {
        this.inventoryInfo = inventoryInfo;
    }
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.common.supcan.annotation.treelist.cols.SupCol;
import com.wanhutong.backend.common.utils.excel.annotation.ExcelField;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
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
    private List<AttributeValueV2> attributeValueV2List;    //属性值

    private Byte orderType;     //订单类型

    private String nowPrices;   //采购商修改的价格
    private String detailIds;
    private Integer totalReqQty;
    private Integer totalSendQty;
    private Integer vendorId;
    private String vendorName;
    private String suplyIds;
    private Office vendor;      //供应商
    private User primary;       //供应商主联系人

    private Office cust;    //采购商
    /**
     * C端订单详情保存
     * */
    private String detailFlag;

    private BizOrderDetail orderDaillist;//商品

    /**
     * 订单发货查看 已经生成的采购单
     * */
    private BizPoHeader poHeader;

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

    public String getDetailFlag() {
        return detailFlag;
    }

    public void setDetailFlag(String detailFlag) {
        this.detailFlag = detailFlag;
    }

    public Byte getOrderType() {
        return orderType;
    }

    public void setOrderType(Byte orderType) {
        this.orderType = orderType;
    }

    public String getNowPrices() {
        return nowPrices;
    }

    public void setNowPrices(String nowPrices) {
        this.nowPrices = nowPrices;
    }

    public List<AttributeValueV2> getAttributeValueV2List() {
        return attributeValueV2List;
    }

    public void setAttributeValueV2List(List<AttributeValueV2> attributeValueV2List) {
        this.attributeValueV2List = attributeValueV2List;
    }

    public BizPoHeader getPoHeader() {
        return poHeader;
    }

    public void setPoHeader(BizPoHeader poHeader) {
        this.poHeader = poHeader;
    }

    public Office getCust() {
        return cust;
    }

    public void setCust(Office cust) {
        this.cust = cust;
    }
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;

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
    private BizOpShelfInfo shelfInfo;  //货架ID biz_op_shelf_info.id
    private BizSkuInfo skuInfo;        // 产品biz_sku_info.id
    private String partNo;        // 商品编号
    private String skuName;        // 商品名称
    private Double unitPrice;        // 商品单价
    private Integer sentQty;        //发货数量
    private Integer ordQty;        // 采购数量
    private String quality;        //材质
    private String color;        //颜色
    private String standard;    //规格

    private Integer ordQtyUpda;        //用于存储修改时的采购数量
    private Integer maxLineNo;        //最大的行号
    private List<BizOpShelfSku> shelfList;    //用于计算有多少货架
    private String orderDetaIds;        //用于多选商品时传递数据

    public Integer getMaxLineNo() {
        return maxLineNo;
    }

    public void setMaxLineNo(Integer maxLineNo) {
        this.maxLineNo = maxLineNo;
    }

    public BizOpShelfInfo getShelfInfo() {
        return shelfInfo;
    }

    public void setShelfInfo(BizOpShelfInfo shelfInfo) {
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
}
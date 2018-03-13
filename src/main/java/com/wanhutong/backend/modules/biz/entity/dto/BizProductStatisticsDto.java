package com.wanhutong.backend.modules.biz.entity.dto;

import java.math.BigDecimal;

/**
 * 产品统计数据Dto
 * @author Ma.Qiang
 */
public class BizProductStatisticsDto {

    /**
     * skuId
     */
    private Integer skuId;
    /**
     *商品新增数量
     */
    private Integer upSkuCount;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 货号
     */
    private String itemNo;
    /**
     * 供应商名称
     */
    private String vendorName;
    /**
     * 单价
     */
    private BigDecimal salePrice;
    /**
     * 销量
     */
    private Integer count;
    /**
     * 点击量
     */
    private Integer clickCount;

    /**
     * 销售额
     */
    private BigDecimal totalMoney;


    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getClickCount() {
        return clickCount;
    }

    public void setClickCount(Integer clickCount) {
        this.clickCount = clickCount;
    }

    public Integer getUpSkuCount() {
        return upSkuCount;
    }

    public void setUpSkuCount(Integer upSkuCount) {
        this.upSkuCount = upSkuCount;
    }
}

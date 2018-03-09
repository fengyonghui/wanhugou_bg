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
}

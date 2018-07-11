package com.wanhutong.backend.modules.biz.entity.dto;

import java.math.BigDecimal;

/**
 * 订单统计数据Dto
 * @author Ma.Qiang
 */
public class BizOrderStatisticsDto {

    /**
     * 总金额
     */
    private BigDecimal totalMoney = new BigDecimal(0);

    /**
     * 回款额
     */
    private BigDecimal receiveTotal = new BigDecimal(0);
    //上月总金额
    private BigDecimal upTotalMoney = new BigDecimal(0);
    /**
     * 总数量
     */
    private int orderCount;
    //上月总数量
    private int upOrderCount;
    /**
     * 机构id
     */
    private int officeId;
    /**
     * 机构名称
     */
    private String officeName;

    /**
     * 成本
     */
    private BigDecimal buyPrice = new BigDecimal(0);

    /**
     * 利润
     */
    private BigDecimal profitPrice;
    //上月利润
    private BigDecimal upProfitPrice = new BigDecimal(0);

    /**
     * 日期
     */
    private String createDate;

    /**
     * 平均单价
     */
    private BigDecimal univalence;


    /**
     * SKU名称
     */
    private String skuName;
    /**
     * SKU ID
     */
    private Integer skuId;


    public BigDecimal getUnivalence() {
        if (orderCount <= 0) {
            return BigDecimal.ZERO;
        }
        return getTotalMoney().divide(BigDecimal.valueOf(getOrderCount()), BigDecimal.ROUND_DOWN);
    }

    public void setUnivalence(BigDecimal univalence) {
        this.univalence = univalence;
    }

    public BigDecimal getReceiveTotal() {
        return receiveTotal;
    }

    public void setReceiveTotal(BigDecimal receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public BigDecimal getProfitPrice() {
        return totalMoney.subtract(buyPrice);
    }

    public void setProfitPrice(BigDecimal profitPrice) {
        this.profitPrice = profitPrice;
    }

    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public BigDecimal getUpTotalMoney() {
        return upTotalMoney;
    }

    public void setUpTotalMoney(BigDecimal upTotalMoney) {
        this.upTotalMoney = upTotalMoney;
    }

    public int getUpOrderCount() {
        return upOrderCount;
    }

    public void setUpOrderCount(int upOrderCount) {
        this.upOrderCount = upOrderCount;
    }

    public BigDecimal getUpProfitPrice() {
        return upProfitPrice;
    }

    public void setUpProfitPrice(BigDecimal upProfitPrice) {
        this.upProfitPrice = upProfitPrice;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }
}

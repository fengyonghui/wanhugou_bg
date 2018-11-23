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

    /**
     * SKU ID
     */
    private String skuItemNo;

    /**
     * 年月时间 例201801
     */
    private String ym;

    /**
     * 订单数
     */
    private Integer orderHeaderCount;

    /**
     * 月销售额
     */
    private BigDecimal accumulatedSalesMonth;

    /**
     * 联营销售额
     */
    private BigDecimal joinSaleAmount;

    /**
     * 联营回款额
     */
    private BigDecimal joinRemitAmount;

    /**
     * 代采销售额
     */
    private BigDecimal purchaseSaleAmount;

    /**
     * 代采回款额
     */
    private BigDecimal purchaseRemitAmount;

    /**
     * 有效开单用户数
     */
    private BigDecimal validCustomerNum = BigDecimal.ZERO;


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
        //马强的计算方法
        //return totalMoney.subtract(buyPrice);
        return profitPrice;
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

    public String getSkuItemNo() {
        return skuItemNo;
    }

    public void setSkuItemNo(String skuItemNo) {
        this.skuItemNo = skuItemNo;
    }

    public String getYm() {
        return ym;
    }

    public void setYm(String ym) {
        this.ym = ym;
    }

    public Integer getOrderHeaderCount() {
        return orderHeaderCount;
    }

    public void setOrderHeaderCount(Integer orderHeaderCount) {
        this.orderHeaderCount = orderHeaderCount;
    }

    public BigDecimal getAccumulatedSalesMonth() {
        return accumulatedSalesMonth;
    }

    public void setAccumulatedSalesMonth(BigDecimal accumulatedSalesMonth) {
        this.accumulatedSalesMonth = accumulatedSalesMonth;
    }

    public BigDecimal getJoinSaleAmount() {
        return joinSaleAmount;
    }

    public void setJoinSaleAmount(BigDecimal joinSaleAmount) {
        this.joinSaleAmount = joinSaleAmount;
    }

    public BigDecimal getJoinRemitAmount() {
        return joinRemitAmount;
    }

    public void setJoinRemitAmount(BigDecimal joinRemitAmount) {
        this.joinRemitAmount = joinRemitAmount;
    }

    public BigDecimal getPurchaseSaleAmount() {
        return purchaseSaleAmount;
    }

    public void setPurchaseSaleAmount(BigDecimal purchaseSaleAmount) {
        this.purchaseSaleAmount = purchaseSaleAmount;
    }

    public BigDecimal getPurchaseRemitAmount() {
        return purchaseRemitAmount;
    }

    public void setPurchaseRemitAmount(BigDecimal purchaseRemitAmount) {
        this.purchaseRemitAmount = purchaseRemitAmount;
    }

    public BigDecimal getValidCustomerNum() {
        return validCustomerNum;
    }

    public void setValidCustomerNum(BigDecimal validCustomerNum) {
        this.validCustomerNum = validCustomerNum;
    }
}

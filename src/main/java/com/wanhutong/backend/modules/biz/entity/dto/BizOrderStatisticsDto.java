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

    @Override
    public String toString() {
        return "BizOrderStatisticsDto{" +
                "totalMoney=" + totalMoney +
                ", orderCount=" + orderCount +
                ", officeId=" + officeId +
                ", officeName=" + officeName +
                '}';
    }
}

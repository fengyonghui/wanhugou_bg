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
     * 总数量
     */
    private int orderCount;
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

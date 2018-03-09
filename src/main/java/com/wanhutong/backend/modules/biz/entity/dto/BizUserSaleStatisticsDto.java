package com.wanhutong.backend.modules.biz.entity.dto;


import java.math.BigDecimal;

/**
 * 用户销售业绩相关
 */
public class BizUserSaleStatisticsDto {

    /**
     * 总销售额
     */
    private BigDecimal totalMoney;
    /**
     * 名称
     */
    private String name;
    /**
     * 数量
     */
    private Integer orderCount;

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
}

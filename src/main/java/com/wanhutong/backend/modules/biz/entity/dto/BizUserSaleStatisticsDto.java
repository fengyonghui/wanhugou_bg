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
     * 采购中心名
     */
    private String centName;
    /**
     * 数量
     */
    private Integer orderCount;

    /**
     * 销售员ID
     */
    private Integer usId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 会员数量
     */
    private Integer custCount;

    /**
     * 会员新增量
     */
    private Integer addCustCount;

    /**
     * 成本
     */
    private BigDecimal buyPrice = new BigDecimal(0);

    /**
     * 利润
     */
    private BigDecimal profitPrice;

    /**
     * 已收货款
     */
    private BigDecimal receiveTotal;

    public BigDecimal getReceiveTotal() {
        return receiveTotal;
    }

    public void setReceiveTotal(BigDecimal receiveTotal) {
        this.receiveTotal = receiveTotal;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getUsId() {
        return usId;
    }

    public void setUsId(Integer usId) {
        this.usId = usId;
    }


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

    public String getCentName() {
        return centName;
    }

    public void setCentName(String centName) {
        this.centName = centName;
    }

    public Integer getCustCount() {
        return custCount;
    }

    public void setCustCount(Integer custCount) {
        this.custCount = custCount;
    }

    public Integer getAddCustCount() {
        return addCustCount;
    }

    public void setAddCustCount(Integer addCustCount) {
        this.addCustCount = addCustCount;
    }
}

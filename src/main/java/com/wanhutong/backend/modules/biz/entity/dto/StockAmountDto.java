package com.wanhutong.backend.modules.biz.entity.dto;

/**
 * 库存 信息
 */
public class StockAmountDto {

    /**
     * 名称
     */
    private String name;

    /**
     * 库存量
     */
    private String stockQty;

    /**
     * 库存额
     */
    private String stockAmount;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public String getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(String stockAmount) {
        this.stockAmount = stockAmount;
    }
}

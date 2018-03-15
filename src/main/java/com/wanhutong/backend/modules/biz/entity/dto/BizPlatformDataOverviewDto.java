package com.wanhutong.backend.modules.biz.entity.dto;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 平台数据概览DTO
 *
 * @author Ma.Qiang
 */
public class BizPlatformDataOverviewDto {

    private static final BigDecimal PERCENTAGE = new BigDecimal(100);


    /**
     * 省份
     */
    private String province;

    /**
     * 名称
     */
    private String name;

    /**
     * 采购额
     */
    private BigDecimal procurement;

    /**
     * 月累计销量
     */
    private BigDecimal accumulatedSalesMonth;

    /**
     * 日采购额
     */
    private BigDecimal procurementDay;

    /**
     * 达成率
     */
    private String yieldRate;

    /**
     * 月累计差异
     */
    private BigDecimal differenceTotalMonth;

    /**
     * 剩余天数
     */
    private Integer remainingDays;

    /**
     * 每日最低回款额
     */
    private BigDecimal dayMinReturned;

    /**
     * 库存金额
     */
    private BigDecimal stockAmount;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getProcurement() {
        return procurement;
    }

    public void setProcurement(BigDecimal procurement) {
        this.procurement = procurement;
    }

    public BigDecimal getAccumulatedSalesMonth() {
        return accumulatedSalesMonth;
    }

    public void setAccumulatedSalesMonth(BigDecimal accumulatedSalesMonth) {
        this.accumulatedSalesMonth = accumulatedSalesMonth;
    }

    public BigDecimal getProcurementDay() {
        return procurementDay;
    }

    public void setProcurementDay(BigDecimal procurementDay) {
        this.procurementDay = procurementDay;
    }

    public String getYieldRate() {
        return accumulatedSalesMonth.divide(procurement, 2,BigDecimal.ROUND_HALF_UP).multiply(PERCENTAGE).toString().concat("%");
    }


    public BigDecimal getDifferenceTotalMonth() {
        return accumulatedSalesMonth.subtract(procurement);
    }


    public Integer getRemainingDays() {
        Calendar c = Calendar.getInstance();
        //获取当前天数
        int day = c.get(Calendar.DAY_OF_MONTH);
        //获取本月最大天数
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return max - day;
    }

    public BigDecimal getDayMinReturned() {
        if (getDifferenceTotalMonth().compareTo(BigDecimal.ZERO) < 0) {
            return getDifferenceTotalMonth().divide(BigDecimal.valueOf(getRemainingDays()), 2,BigDecimal.ROUND_HALF_UP).abs();
        }
        return BigDecimal.ZERO;
    }


    public BigDecimal getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(BigDecimal stockAmount) {
        this.stockAmount = stockAmount;
    }
}

package com.wanhutong.backend.modules.biz.entity.dto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 平台数据概览DTO
 *
 * @author Ma.Qiang
 */
public class BizPlatformDataOverviewDto {

    private static final BigDecimal PERCENTAGE = new BigDecimal(100);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

    /**
     * 当前日期
     */
    private String currentDate;

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

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
        if (getProcurement().compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return getAccumulatedSalesMonth().divide(getProcurement(), 2,BigDecimal.ROUND_HALF_UP).multiply(PERCENTAGE).toString().concat("%");
    }


    public BigDecimal getDifferenceTotalMonth() {
        return getAccumulatedSalesMonth().subtract(getProcurement());
    }

    public Integer getRemainingDays() {
        try {
            Date parse = SIMPLE_DATE_FORMAT.parse(currentDate);
            Calendar c = Calendar.getInstance();
            c.setTime(parse);
            //获取当前天数
            int day = c.get(Calendar.DAY_OF_MONTH);
            //获取本月最大天数
            int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            return max - day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public BigDecimal getDayMinReturned() {
        if (getRemainingDays() == 0) {
            return getDifferenceTotalMonth();
        }
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

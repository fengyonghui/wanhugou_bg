package com.wanhutong.backend.modules.biz.entity.dto;

import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import org.apache.commons.lang3.StringUtils;

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
     * 计划采购额
     */
    private BigDecimal procurement = BigDecimal.ZERO;

    /**
     * 月累计销量
     */
    private BigDecimal accumulatedSalesMonth = BigDecimal.ZERO;

    /**
     * 月回款额
     */
    private BigDecimal receiveTotal = BigDecimal.ZERO;

    /**
     * 新增用户计划,计划开单会员量
     */
    private BigDecimal newUserPlan = BigDecimal.ZERO;

    /**
     * 新增用户，开单会员量
     */
    private BigDecimal newUser = BigDecimal.ZERO;

    /**
     * 新增用户达成率
     */
    private String newUserRate;

    /**
     * 服务费计划
     */
    private BigDecimal serviceChargePlan = BigDecimal.ZERO;

    /**
     * 服务费
     */
    private BigDecimal serviceCharge = BigDecimal.ZERO;

    /**
     * 服务费达成率
     */
    private String serviceChargeRate;

    /**
     * 日采购额
     */
    private BigDecimal procurementDay = BigDecimal.ZERO;

    /**
     * 达成率
     */
    private String yieldRate;

    /**
     * 月累计差异
     */
    private BigDecimal differenceTotalMonth = BigDecimal.ZERO;

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
    private BigDecimal stockAmount = BigDecimal.ZERO;

    /**
     * 当前日期
     */
    private String currentDate;

    /**
     * 采购中心ID
     */
    private Integer officeId;

    /**
     * 采购中心NAME
     */
    private String officeName;

    /**
     * 采购中心TYPE
     */
    private Integer officeType;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 月计划联营订单总额
     */
    private BigDecimal jointOrderPlanAmountTotal;

    /**
     * 月联营订单总额
     */
    private BigDecimal jointOrderAmountTotal;

    /**
     * 月计划代采订单总额
     */
    private BigDecimal purchaseOrderPlanAmountTotal;

    /**
     * 月代采订单总额
     */
    private BigDecimal purchaseOrderAmountTotal;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public Integer getOfficeType() {
        return officeType;
    }

    public void setOfficeType(Integer officeType) {
        this.officeType = officeType;
    }

    public String getProvince() {
        if (officeType == null || officeType == 0) {
            return StringUtils.EMPTY;
        }
        switch (OfficeTypeEnum.stateOf(String.valueOf(officeType))) {
            case PURCHASINGCENTER:
                if (StringUtils.isBlank(province)) {
                    return "未知";
                }
                return province;
            case NETWORKSUPPLY:
                return "网供";
            case NETWORK:
                return "网供";
            case WITHCAPITAL:
                return "配资";
            default:
                break;
        }
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
        return getReceiveTotal().divide(getProcurement(), 2, BigDecimal.ROUND_HALF_UP).multiply(PERCENTAGE).toString().concat("%");
    }

    public String getNewUserRate() {
        if (getNewUserPlan().compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return getNewUser().divide(getNewUserPlan(), 2, BigDecimal.ROUND_HALF_UP).multiply(PERCENTAGE).toString().concat("%");
    }

    public String getServiceChargeRate() {
        if (getServiceChargePlan().compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        return getServiceCharge().divide(getServiceChargePlan(), 2, BigDecimal.ROUND_HALF_UP).multiply(PERCENTAGE).toString().concat("%");
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
        if (getDifferenceTotalMonth().compareTo(BigDecimal.ZERO) >= 0) {
            return BigDecimal.ZERO;
        }
        if (getRemainingDays() == 0) {
            return getDifferenceTotalMonth().abs();
        }

        return getDifferenceTotalMonth().divide(BigDecimal.valueOf(getRemainingDays()), 2, BigDecimal.ROUND_HALF_UP).abs();
    }


    public BigDecimal getStockAmount() {
        if (stockAmount == null) {
            return BigDecimal.ZERO;
        }
        return stockAmount;
    }

    public void setStockAmount(BigDecimal stockAmount) {
        this.stockAmount = stockAmount;
    }

    public BigDecimal getReceiveTotal() {
        return receiveTotal;
    }

    public void setReceiveTotal(BigDecimal receiveTotal) {
        this.receiveTotal = receiveTotal;
    }

    public BigDecimal getNewUserPlan() {
        return newUserPlan;
    }

    public void setNewUserPlan(BigDecimal newUserPlan) {
        this.newUserPlan = newUserPlan;
    }

    public BigDecimal getNewUser() {
        return newUser;
    }

    public void setNewUser(BigDecimal newUser) {
        this.newUser = newUser;
    }

    public BigDecimal getServiceChargePlan() {
        return serviceChargePlan;
    }

    public void setServiceChargePlan(BigDecimal serviceChargePlan) {
        this.serviceChargePlan = serviceChargePlan;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public BigDecimal getJointOrderAmountTotal() {
        return jointOrderAmountTotal;
    }

    public void setJointOrderAmountTotal(BigDecimal jointOrderAmountTotal) {
        this.jointOrderAmountTotal = jointOrderAmountTotal;
    }

    public BigDecimal getPurchaseOrderAmountTotal() {
        return purchaseOrderAmountTotal;
    }

    public void setPurchaseOrderAmountTotal(BigDecimal purchaseOrderAmountTotal) {
        this.purchaseOrderAmountTotal = purchaseOrderAmountTotal;
    }

    public BigDecimal getJointOrderPlanAmountTotal() {
        return jointOrderPlanAmountTotal;
    }

    public void setJointOrderPlanAmountTotal(BigDecimal jointOrderPlanAmountTotal) {
        this.jointOrderPlanAmountTotal = jointOrderPlanAmountTotal;
    }

    public BigDecimal getPurchaseOrderPlanAmountTotal() {
        return purchaseOrderPlanAmountTotal;
    }

    public void setPurchaseOrderPlanAmountTotal(BigDecimal purchaseOrderPlanAmountTotal) {
        this.purchaseOrderPlanAmountTotal = purchaseOrderPlanAmountTotal;
    }

}

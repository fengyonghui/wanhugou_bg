package com.wanhutong.backend.modules.biz.entity.dto;

/**
 * 备货单排产Dto
 * @author 王冰洋
 */
public class BizHeaderSchedulingDto {
    /**
     * 表ID
     */
    public Integer objectId;
    /**
     * 单子原始数量
     */
    public Integer originalNum;
    /**
     * 排产数量
     */
    public Integer schedulingNum;
    /**
     * 已完成数量
     */
    public String planDate;
    /**
     * 订单排产类型
     */
    public String schedulingType;


    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getOriginalNum() {
        return originalNum;
    }

    public void setOriginalNum(Integer originalNum) {
        this.originalNum = originalNum;
    }

    public Integer getSchedulingNum() {
        return schedulingNum;
    }

    public void setSchedulingNum(Integer schedulingNum) {
        this.schedulingNum = schedulingNum;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public String getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(String schedulingType) {
        this.schedulingType = schedulingType;
    }
}

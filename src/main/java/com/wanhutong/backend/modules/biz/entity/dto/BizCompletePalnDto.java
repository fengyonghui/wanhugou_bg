package com.wanhutong.backend.modules.biz.entity.dto;

import java.util.Date;

/**
 * 采购单排产Dto
 * @author Ma.Qiang
 */
public class BizCompletePalnDto {
    /**
     * 排产计划Id
     */
    public Integer schedulingId;
    /**
     * 确认时间
     */
    public String planDate;
    /**
     * 确认数量
     */
    public Integer completeNum;

    public Integer getSchedulingId() {
        return schedulingId;
    }

    public void setSchedulingId(Integer schedulingId) {
        this.schedulingId = schedulingId;
    }

    public String getPlanDate() {
        return planDate;
    }

    public void setPlanDate(String planDate) {
        this.planDate = planDate;
    }

    public Integer getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum(Integer completeNum) {
        this.completeNum = completeNum;
    }
}

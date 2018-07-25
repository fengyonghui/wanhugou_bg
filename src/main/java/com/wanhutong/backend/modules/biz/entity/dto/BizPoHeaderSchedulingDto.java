package com.wanhutong.backend.modules.biz.entity.dto;

/**
 * 采购单排产Dto
 * @author Ma.Qiang
 */
public class BizPoHeaderSchedulingDto {
    /**
     * id
     */
    public Integer id;
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
    public Integer completeNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum(Integer completeNum) {
        this.completeNum = completeNum;
    }
}

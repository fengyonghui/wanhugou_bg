package com.wanhutong.backend.modules.biz.entity.dto;

/**
 * 备货单排产Dto
 * @author 王冰洋
 */
public class BizHeaderSchedulingDto {
    /**
     * poHeaderId
     */
    private Integer id;
    /**
     * 表ID
     */
    private Integer objectId;
    /**
     * 单子原始数量
     */
    private Integer originalNum;
    /**
     * 排产数量
     */
    private Integer schedulingNum;
    /**
     * 排产日期
     */
    private String planDate;
    /**
     * 订单排产类型
     */
    private String schedulingType;

    /**
     * 订单排产类型
     */
    private String remark;

    /**
     * 采购单排产状态 0,未排产  1,排产中  2,排产完成
     */
    private String poSchType;


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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPoSchType() {
        return poSchType;
    }

    public void setPoSchType(String poSchType) {
        this.poSchType = poSchType;
    }
}

package com.wanhutong.backend.modules.biz.entity.dto;

import java.util.Date;

/**
 * 出入库DTO
 */
public class BizSkuInputOutputDto {

    /**
     * 仓库名称
     */
    private String invName;

    /**
     * 出入库数量
     */
    private Integer countNumber;

    /**
     * 出入库类型 1入库 0出库
     */
    private Integer dataType;


    /**
     * sku id
     */
    private Integer skuId;

    /**
     * 仓库ID
     */
    private Integer invId;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public Integer getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(Integer countNumber) {
        this.countNumber = countNumber;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Integer getInvId() {
        return invId;
    }

    public void setInvId(Integer invId) {
        this.invId = invId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

package com.wanhutong.backend.modules.enums;

public enum OrderHeaderCommissionStatusEnum {
    NO_COMMISSSION(0, "未结佣"),
    COMMISSION_COMPLETE(1, "已结佣");

    private Integer comStatus;
    private String desc;

    OrderHeaderCommissionStatusEnum(Integer comStatus, String desc) {
        this.comStatus = comStatus;
        this.desc = desc;
    }

    public Integer getComStatus() {
        return comStatus;
    }

    public void setComStatus(Integer comStatus) {
        this.comStatus = comStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

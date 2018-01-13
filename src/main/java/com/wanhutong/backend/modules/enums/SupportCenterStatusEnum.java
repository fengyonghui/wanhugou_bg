package com.wanhutong.backend.modules.enums;

public enum SupportCenterStatusEnum {
    MAKER_CENTER(31);

    private Integer state;

    SupportCenterStatusEnum(Integer st) {
        this.state=st;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

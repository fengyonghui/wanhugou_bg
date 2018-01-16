package com.wanhutong.backend.modules.enums;

public enum InvSkuTypeEnum {
    CONVENTIONAL(1),DAMAGED(2),EXCLUSIVE(3);

    private Integer state;

    InvSkuTypeEnum(Integer st) {
        this.state=st;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

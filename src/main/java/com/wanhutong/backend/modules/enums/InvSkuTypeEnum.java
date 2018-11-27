package com.wanhutong.backend.modules.enums;

public enum InvSkuTypeEnum {
    //1常规；2残损；3专属；4样品
    CONVENTIONAL(1),DAMAGED(2),EXCLUSIVE(3),SAMPLE(4),;

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

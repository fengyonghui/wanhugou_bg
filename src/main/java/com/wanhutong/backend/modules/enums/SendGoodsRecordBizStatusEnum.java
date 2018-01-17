package com.wanhutong.backend.modules.enums;

public enum SendGoodsRecordBizStatusEnum {
    CENTER(0),VENDOR(1);      //0:采购中心   1：供货中心

    private Integer state;

    SendGoodsRecordBizStatusEnum(Integer st) {
        this.state=st;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

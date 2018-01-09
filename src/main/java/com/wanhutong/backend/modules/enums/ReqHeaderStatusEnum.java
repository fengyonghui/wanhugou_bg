package com.wanhutong.backend.modules.enums;

public enum ReqHeaderStatusEnum {
    UNREVIEWED(0),APPROVE(5),PURCHASING(10),ACCOMPLISH_PURCHASE(15), STOCKING(20),STOCK_COMPLETE(25),COMPLETE(30),CLOSE(35);

    private Integer state;
    ReqHeaderStatusEnum(int st) {
        this.state=st;
    }
    public static ReqHeaderStatusEnum stateOf(Integer index) {
        for (ReqHeaderStatusEnum statusEnum : values()) {
            if (statusEnum.state==index) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

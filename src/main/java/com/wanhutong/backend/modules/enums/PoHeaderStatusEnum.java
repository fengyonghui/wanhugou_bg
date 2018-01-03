package com.wanhutong.backend.modules.enums;

public enum PoHeaderStatusEnum {
    UNPAY(0),INITIAL_PAY(1),ALL_PAY(2),SEND(3),RECEIVED(4),COMPLETE(5);

    private Integer code;

    PoHeaderStatusEnum(Integer o) {
        this.code=o;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}

package com.wanhutong.backend.modules.enums;

public enum PoHeaderStatusEnum {
    /**
     *  0未支付；1首付款支付 2全部支付 5 已完成 6审批中 7审批完成 10 已取消
     */
    UNPAY(0),INITIAL_PAY(1),ALL_PAY(2),COMPLETE(5),PROCESS(6),PROCESS_COMPLETE(7),CANCEL(10),;

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

package com.wanhutong.backend.modules.enums;

public enum ReqHeaderStatusEnum {
    //业务状态：0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
    UNREVIEWED(0), INITIAL_PAY(1),ALL_PAY(2),APPROVE(5),PURCHASING(10),ACCOMPLISH_PURCHASE(15), STOCKING(20),STOCK_COMPLETE(25),COMPLETE(30),CLOSE(35);

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

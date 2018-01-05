package com.wanhutong.backend.modules.enums;

public enum OrderHeaderBizStatusEnum {

 //   业务状态 0未支付；5首付款支付 10全部支付 15同意发货(供货中) 16审核通过 17采购中 18采购完成 19 供应商供货  20已发货 25客户已收货 30已完成
    UNPAY(0),INITIAL_PAY(5),ALL_PAY(10), SUPPLYING(15), APPROVE(16),PURCHASING(17),ACCOMPLISH_PURCHASE(18),STOCKING(19),SEND(20),RECEIVED(25),COMPLETE(30);

    private Integer state;
    OrderHeaderBizStatusEnum(int st) {
        this.state=st;
    }
    public static OrderHeaderBizStatusEnum stateOf(Integer index) {
        for (OrderHeaderBizStatusEnum statusEnum : values()) {
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

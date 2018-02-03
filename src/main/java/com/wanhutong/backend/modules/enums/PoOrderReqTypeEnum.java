package com.wanhutong.backend.modules.enums;


public enum PoOrderReqTypeEnum {
    //SO销售订单 RE备货清单
   SO("1"),RE("2");
    private String orderType;

    PoOrderReqTypeEnum(String orderType) {
        this.orderType = orderType;
    }
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    public static PoOrderReqTypeEnum stateOf(String index) {
        for (PoOrderReqTypeEnum state : values()) {
            if (state.getOrderType().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println( PoOrderReqTypeEnum.SO.ordinal() );


    }
}

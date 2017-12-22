package com.wanhutong.backend.modules.enums;


public enum OrderTypeEnum {
    //SO销售订单
    ORDER("0"),SO("1");
    private String orderType;

    OrderTypeEnum(String orderType) {
        this.orderType = orderType;
    }
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    public static OrderTypeEnum stateOf(String index) {
        for (OrderTypeEnum state : values()) {
            if (state.getOrderType().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println( OrderTypeEnum.SO.ordinal() );


    }
}

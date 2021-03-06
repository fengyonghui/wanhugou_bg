package com.wanhutong.backend.modules.enums;


public enum OrderTypeEnum {
    //SO销售订单 PO采购订单 SE发货单 ，CEND微店订单，FO线下支付流水,ODO 出库单, RIO(入库单) TR(调拨单) RES(备货拆分单) REM(备货合并单)
    ORDER("0"),SO("1"),PO("2"),RE("6"),SE("7"),CO("4"),FO("3"),DO("5"),ODO("8"),RIO("9"),TR("10"),RES("11"),REM("12");
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

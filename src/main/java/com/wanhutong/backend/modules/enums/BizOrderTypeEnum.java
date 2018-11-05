package com.wanhutong.backend.modules.enums;


public enum BizOrderTypeEnum {


 //   订单类型  1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5.代采订单 6.拍照下单
    ORDINARY_ORDER(1, "普通订单"),
    ACCOUNT_BOOK_PURCHASE(2, "帐期采购"),
    ALLOCATION_PURCHASE(3, "配资采购"),
    MICRO_BUSINESS_ORDER(4, "微商订单"),
    PURCHASE_ORDER(5, "代采订单"),
    PHOTO_ORDER(6, "拍照下单"),
    COMMISSION_ORDER(8, "零售代销订单");

    private Integer state;
    private String desc;

    BizOrderTypeEnum(int st, String desc) {
        this.state=st;
        this.desc=desc;
    }

    public static BizOrderTypeEnum stateOf(Integer index) {
        for (BizOrderTypeEnum statusEnum : values()) {
            if (statusEnum.state.intValue() == index) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}

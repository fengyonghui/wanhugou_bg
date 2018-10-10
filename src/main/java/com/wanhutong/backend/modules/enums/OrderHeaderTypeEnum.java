package com.wanhutong.backend.modules.enums;

public enum OrderHeaderTypeEnum {

    //1: 普通订单 ; SO
    SO(1, "普通订单"),
    //2:帐期采购 PD（未用）
    PD(2, "帐期采购"),
    //3:配资采购 FF（未用）
    FF(3, "配资采购"),
    //4:微商订单CO
    CO(4, "微商订单"),
    //5.代采订单 DO
    DO(5, "普通订单"),
    //6.拍照下单 DPO
    DPO(6, "普通订单"),

    //100.线下支付流水单  FO
    FO(100, "线下支付流水单"),
    //101.发货单  SE
    SE(101, "发货单"),
    //102.采购单  PO
    PO(102, "采购单"),
    //103 备货单 RE
    RE(103, "备货单"),
    //104入库单 RIO
    RIO(104, "入库单"),
    //105出库单 ODO
    ODO(105, "出库单"),

    ;


    private int type;
    private String desc;

    OrderHeaderTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

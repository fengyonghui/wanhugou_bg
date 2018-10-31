package com.wanhutong.backend.modules.enums;

public enum TradeTypeEnum {


    RECHARGE_TYPE(1,"充值"),
    WITHDRAWALS_TYPE(2, "提现"),
    PAYMENT_TYPE(3,"支付"),
    REFUND_TYPE(4,"退款"),
    RECHARGEING_TYPE(5,"充值中"),
    WITHDRAWALSing_TYPE(6, "提现中"),
    ORDER_PAY_TYPE(7,"订单支付"),
    REQUEST_PAY_TYPE(10,"备货订单支付"),
    REFUND_PAY_TYPE(11,"线下退款"),
    COMMISSION_PAY_TYPE(12,"零售单结佣");

    private Integer code;
    private String tradeNoType;

    TradeTypeEnum(Integer code, String tradeNoType) {
        this.code = code;
        this.tradeNoType = tradeNoType;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getTradeNoType() {
        return tradeNoType;
    }

    public void setTradeNoType(String tradeNoType) {
        this.tradeNoType = tradeNoType;
    }
}

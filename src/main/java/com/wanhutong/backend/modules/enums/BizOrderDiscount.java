package com.wanhutong.backend.modules.enums;

/**
 * 用于销售订单首次下单计算优惠金额
 * Ouyang Xiutian
 * 2018/01/11
 */
public enum BizOrderDiscount {

    //    自营订单(10%)，非自营订单(5%)
    SELF_SUPPORT(0.1), NON_SELF_SUPPORT(0.05),ONE_ORDER(0);

    //计算元素
    private Double calcs;
    //根据 bizStatus 状态为0 进行查询判断
    private Integer oneOr;

    BizOrderDiscount(Double calcs) {
        this.calcs = calcs;
    }
    BizOrderDiscount(Integer oneOr) {
        this.oneOr = oneOr;
    }

    public Double getCalcs() {
        return calcs;
    }

    public void setCalcs(Double calcs) {
        this.calcs = calcs;
    }

    public Integer getOneOr() {
        return oneOr;
    }

    public void setOneOr(Integer oneOr) {
        this.oneOr = oneOr;
    }

    public static BizOrderDiscount stateOf(Double calcs) {
        for (BizOrderDiscount state : values()) {
            if (state.getCalcs() == calcs) {
                return state;
            }
        }
        return null;
    }

    public static BizOrderDiscount stateOf(Integer oneOr) {
        for (BizOrderDiscount state : values()) {
            if (state.getOneOr() == oneOr) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(BizOrderDiscount.NON_SELF_SUPPORT.getCalcs());
        System.out.println(BizOrderDiscount.SELF_SUPPORT.getCalcs());
        System.out.println(BizOrderDiscount.ONE_ORDER.getOneOr());
    }
}

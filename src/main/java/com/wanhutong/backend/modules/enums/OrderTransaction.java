package com.wanhutong.backend.modules.enums;

/**
 * 交易相关
 * Ouyang Xiutian
 * 2018/01/11
 */
public enum OrderTransaction {

    //交易记录表：交易类型/支付类型   失败0 成功1 平台总钱包
    RECHARGE(1,"充值"),WITHDRAWALS(2,"提现"),PAYMENT(3,"支付"),REFUND(4,"退款"), WECHAT(1,"微信"),ALIPAY(2,"支付宝"),
    PLATFORM_PAYMENT(3,"平台付款"),PLATFORM_REFUND(4,"平台退款"),FAIL(0),SUCCESS(1),TOTAL_PURSE(1),
    WHOLE_PAYMENT(10),FIRST_PAYMENT(5)
    ;
    //销售订单表：订单状态  全部支付10  首付款支付5

    private Integer orderId;
    private String orderName;

    OrderTransaction(Integer orderId,String orderName){
        this.orderId=orderId;
        this.orderName=orderName;
    }

    OrderTransaction(Integer orderId){
        this.orderId=orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }


    public static void main(String[] args) {
        System.out.println("--1--"+OrderTransaction.RECHARGE.getOrderId());
        System.out.println("--2--"+OrderTransaction.RECHARGE.getOrderName());
    }

}

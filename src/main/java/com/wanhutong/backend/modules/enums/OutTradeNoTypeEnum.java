package com.wanhutong.backend.modules.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * (C) Copyright 2017-2019
 * All rights reserved.
 * 支付流水号类型
 *
 * @author DreamerCK
 * @date 2018-01-11 17:31
 **/

public enum OutTradeNoTypeEnum {

    //   支付类型：wx(1微信) alipay(2支付宝) 3平台付款 4平台退款，10（平台微信支付），15（平台支付宝支付）
    /**
     * 充值流水号类型
     */
    RECHARGE_NO_TYPE(1, "WHGC", "充值"),
    /**
     * 提现流水号类型
     */
    WITHDRAWALS_NO_TYPE(2, "WHGT", "提现"),
    /**
     * 微店充值流水号类型
     */
    WE_SHOP_NO_TYPE(3, "WWC", "微店支付"),

    /**
     * 线下付款
     */
    OFFLINE_PAY_TYPE(8, "POP", "平台线下付款"),
    /**
     * 平台微信支付流水号类型
     */
    PLATFORM_WX_NO_TYPE (10,"PWP","平台微信支付"),

    /**
     * 平台支付宝支付流水号类型
     */
    PLATFORM_ALIPAY_NO_TYPE (15,"PAP","平台支付宝支付");

    private Integer code;
    private String tradeNoType;
    private String message;


    OutTradeNoTypeEnum(Integer code,String tradeNoType,String message) {
        this.code=code;
        this.tradeNoType=tradeNoType;
        this.message=message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

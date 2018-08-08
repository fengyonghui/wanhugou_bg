package com.wanhutong.backend.common.constant;

/**
 * 微信支付类型
 * @author Ma.Qiang  2017/1/12.
 */
public enum WechatPayTradeType {
//    JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里
//    MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口

    MWEB("MWEB","H5支付"),
    APP("APP","APP支付"),
    MICROPAY("MICROPAY","收银扫码支付"),
    NATIVE("NATIVE","原生扫码支付"),
    JSAPI("JSAPI","公众号支付"),
    ;

    String type;
    String describe;
    WechatPayTradeType(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    public String getType() {
        return type;
    }

    public String getDescribe() {
        return describe;
    }
}

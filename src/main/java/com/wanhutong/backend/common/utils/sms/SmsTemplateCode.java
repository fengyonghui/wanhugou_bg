package com.wanhutong.backend.common.utils.sms;

/**
 * 短信平台 模版id
 *
 * @author Ma.Qiang
 */
public enum SmsTemplateCode {

    /**
     * 服务器异常
     */
    EXCEPTION_WARN("SMS_136389810", "服务器发生异常: 异常类型:${type},影响业务:${service}", "type:%s,service:%s", "阿里云"),
    /**
     * 流程待审批
     */
    PENDING_AUDIT("SMS_136389818", "您有新的${order}待审批,请及时登录系统后台处理", "order:%s", "阿里云"),
    /**
     * 流程待审批2
     */
    PENDING_AUDIT_1("SMS_138068474", "您有新的${order}待审批,请及时登录系统后台处理.单号${orderNum}", "order:%s,orderNum:%s", "阿里云"),
    /**
     * 订单和备货单待发货
     */
    ORDER_DELIVER("SMS_138070187","您有新的${order}待发货,请及时登陆系统后台处理,单号:${number}","order:%s,number:%s","阿里云"),

    /**
     * 订单和备货单待发货
     */
    COMPLETE_SCHEDULING("SMS_138070188","${order}:${reqNo_1}${reqNo_2}已有商品生成完成，请及时与供应商联系","order:%s,reqNo_1:%s,reqNo_2:%s","阿里云"),

    /**
     * 佣金结算异常
     */
    //SETTLEMENT_COMMISSION_EXCEPTION("SMS_138070189", "佣金结算异常: 结算单id:${commId}", "commId:%s", "阿里云"),

    UNKNOWN("0", "未知", "未知", "未知"),;

    /**
     * 短信模版code
     */
    private final String code;
    /**
     * 描述
     */
    private final String desc;
    /**
     * 参数描述
     */
    private final String paramsIntro;
    /**
     * 平台
     */
    private final String platform;

    SmsTemplateCode(final String code, final String desc, final String paramsIntro, final String platform) {
        this.code = code;
        this.desc = desc;
        this.paramsIntro = paramsIntro;
        this.platform = platform;
    }

    public static SmsTemplateCode parse(String code) {
        for (SmsTemplateCode smsTemplateCode : values()) {
            if (code.equals(smsTemplateCode.getCode())) {
                return smsTemplateCode;
            }
        }
        return SmsTemplateCode.UNKNOWN;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getPlatform() {
        return platform;
    }

    public String getParamsIntro() {
        return paramsIntro;
    }

}



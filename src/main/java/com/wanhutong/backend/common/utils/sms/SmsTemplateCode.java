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



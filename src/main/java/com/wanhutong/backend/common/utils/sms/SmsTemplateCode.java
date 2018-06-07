package com.wanhutong.backend.common.utils.sms;

/**
 * 短信平台 模版id
 *
 *
 * @author Ma.Qiang
 *
 */
public enum SmsTemplateCode {

	// 未注册
	SECURITY_CODE("SMS_14750069","获取验证码","code:%s,minute:%s","阿里云"),
	EXCEPTION_WARN("SMS_136384735", "服务器发生异常: 异常类型:${type},影响业务:${service}", "type:%s,service:%s", "阿里云"),

	UNKNOWN("0", "未知", "未知", "未知"),
	;

	private final String code; // 短信模版code
	private final String desc; // 描述
	private final String paramsIntro; // 参数描述
	private final String platform; // 平台

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



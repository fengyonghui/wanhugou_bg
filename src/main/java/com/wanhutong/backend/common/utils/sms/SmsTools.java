package com.wanhutong.backend.common.utils.sms;

import com.google.common.base.Strings;
import com.wanhutong.backend.common.utils.JedisUtils;

/**
 * Created by Ma.Qiang on 2018/6/7
 * 短信工具类
 */
public class SmsTools {
    public static final int EXPIRE = 60 * 60 * 24;//缓存过期时间(24h)


    /**
     * 将发送的短信记录到redis中,在未过期期间不允许重复发送
     *
     * @param key
     * @Param value
     */
    public static void sendSmsRecord(String key, String value) {
        JedisUtils.setObject(key, value, EXPIRE);
    }

    /**
     * 判断该短信是否可以发送,如在缓存中存在,则不允许发送
     * true为可发送,false为不可重复发送
     *
     * @param key
     * @return
     */
    public static boolean isSendSms(String key) {
        String value = JedisUtils.get(key);
        if (Strings.isNullOrEmpty(value)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}

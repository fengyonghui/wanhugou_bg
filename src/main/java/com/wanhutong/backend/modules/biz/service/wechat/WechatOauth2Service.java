package com.wanhutong.backend.modules.biz.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.InterfaceUtil;
import com.wanhutong.backend.common.utils.JedisUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.modules.biz.entity.wechat.AccessToken;
import com.wanhutong.backend.modules.biz.entity.wechat.UserAccessToken;
import com.wanhutong.backend.modules.biz.entity.wechat.WechatUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Ma.Qiang  2018/8/1.
 */
@Service
public class WechatOauth2Service {

    private final static Logger LOGGER = LoggerFactory.getLogger(WechatOauth2Service.class);

    private final static String WECHAT_MP_ACCESS_TOKEN_MEMCACHED_KEY = "WECHAT.MP.ACCESS.TOKEN.REDIS.KEY.";


    private static final String WECHAT_MP_OAUTH2_AUTHORIZE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";

    public String buildOauth2Url(String redirectUrl, String state, Oauth2Scope scope) {

        String url = WECHAT_MP_OAUTH2_AUTHORIZE_URL.replace("APPID", DsConfig.getWechatAppid())
                .replace("REDIRECT_URI", redirectUrl)
                .replace("SCOPE", scope.getScope())
                .replace("STATE", StringUtils.isBlank(state) ? StringUtils.EMPTY : state);
        return url;
    }

    private static final String WECHAT_MP_OAUTH2_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    public UserAccessToken getUserInfoAccessToken(String code) {

        String accessTokenStr = InterfaceUtil.GetRequestForJSON(null, WECHAT_MP_OAUTH2_ACCESS_TOKEN_URL
                .replace("APPID", DsConfig.getWechatAppid())
                .replace("SECRET", DsConfig.getWechatSecret())
                .replace("CODE", code)
        );
        return JSONObject.parseObject(accessTokenStr, UserAccessToken.class);
    }

    private static final String WECHAT_MP_OAUTH2_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    public WechatUserInfo getUserInfoByUserAccessToken(String access_token, String openid, String lang) {
        String url = WECHAT_MP_OAUTH2_USERINFO_URL;
        // 判断access_token 和 openid 是否为空
        if (StringUtils.isBlank(access_token) || StringUtils.isBlank(openid)) {
            LOGGER.info("WechatOauth2ServiceImpl_getUserInfoByUserAccessToken_accessTokeAndOpenid_null");
            return null;
        }
        if (StringUtils.isNotBlank(lang)) {
            url.replace("zh_CN", lang);
        }
        String userInfoStr = InterfaceUtil.GetRequestForJSON(null, url
                .replace("ACCESS_TOKEN", access_token)
                .replace("OPENID", openid)
        );
        LOGGER.info("get user info by userAccessToken");
        return JSONObject.parseObject(userInfoStr, WechatUserInfo.class);
    }

    private static final String WECHAT_MP_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    public AccessToken getAccessToken() {
        try {

            boolean available = true; // access token 是否可用
            AccessToken accessToken = null;
            String accessTokenStr = JedisUtils.get(WECHAT_MP_ACCESS_TOKEN_MEMCACHED_KEY + DsConfig.getWechatAppid());
            if (StringUtils.isNotBlank(accessTokenStr)) {
                //判断是否过期
                AccessToken oldAccessToken = JSONObject.parseObject(accessTokenStr, AccessToken.class);
                Calendar c = Calendar.getInstance();
                c.setTime(oldAccessToken.getCreate_date());
                c.add(Calendar.SECOND, oldAccessToken.getExpires_in());
                if (c.after(Calendar.getInstance())) {
                    available = false;
                } else {
                    accessToken = oldAccessToken;
                }
            } else {
                available = false;
            }

            if (!available) {
                accessTokenStr = InterfaceUtil.GetRequestForJSON(null, WECHAT_MP_ACCESS_TOKEN_URL
                        .replace("APPID", DsConfig.getWechatAppid())
                        .replace("SECRET", DsConfig.getWechatSecret())
                );
                accessToken = JSONObject.parseObject(accessTokenStr, AccessToken.class);
                accessToken.setCreate_date(new Date());
                JedisUtils.set(WECHAT_MP_ACCESS_TOKEN_MEMCACHED_KEY + DsConfig.getWechatAppid(), JsonUtil.getJson(accessToken), 0);
            }
            return accessToken;
        } catch (Exception e) {
            LOGGER.error("get wechat mp access token error", e);
        }
        return null;

    }

    /**
     * 微信获取用户信息的两种级别
     */
    public enum Oauth2Scope {
        BASE("snsapi_base","基本信息"),
        INFO("snsapi_userinfo","详细信息");
        String scope;
        String describe;

        public String getScope() {
            return scope;
        }

        public String getDescribe() {
            return describe;
        }

        Oauth2Scope(String scope, String describe) {
            this.scope = scope;
            this.describe = describe;
        }
    }

}

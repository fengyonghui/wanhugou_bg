package com.wanhutong.backend.modules.biz.web.wechat;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.wechat.UserAccessToken;
import com.wanhutong.backend.modules.biz.service.wechat.WechatOauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 微信oauth2用户授权Controller
 *
 * @author Ma.Qiang  2018/8/1
 */

@Controller
@RequestMapping(value = "/wechat/Oauth2")
public class WechatOauth2Controller extends BaseController {


    @Autowired
    private WechatOauth2Service wechatOauth2Service;

    @RequestMapping(value = "/buildOauth2Base")
    public String buildOauth2PageBase(String redirectUrl, String state) {
        String url = wechatOauth2Service.buildOauth2Url(redirectUrl ,state, WechatOauth2Service.Oauth2Scope.BASE);
        return "redirect:" + url;
    }

    @RequestMapping(value = "/buildOauth2Info")
    public String buildOauth2PageInfo(String redirectUrl, String state) {
        String url = wechatOauth2Service.buildOauth2Url(redirectUrl, state, WechatOauth2Service.Oauth2Scope.INFO);
        return "redirect:" + url;
    }

    /**
     * 去往注册领取页面
     *
     * @return
     */
    @RequestMapping(value = "/go2StaticPage_{path}", produces = "application/json; charset=utf-8")
    public String registerPage(@PathVariable("path") String path,
                               @RequestParam(value = "code", required = false) String code,
                               @RequestParam(value = "state", required = false) String state

    ) {
        String openId = null;
        if (StringUtils.isNotBlank(code)) {
            UserAccessToken accessToken = wechatOauth2Service.getUserInfoAccessToken(code);
            openId = accessToken.getOpenid();
        }
        path = path.replaceAll("-", "/");
        return "redirect:/static/mobile/html/" + path + ".html?state=" + state + "&openId=" + openId;
    }


}

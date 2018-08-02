package com.wanhutong.backend.modules.biz.entity.wechat;

import java.util.Date;

/**
 * 公众号ACCESS_TOKEN
 * @author Ma.Qiang  2017/1/17.
 */
public class AccessToken {

//    {"access_token":"ACCESS_TOKEN","expires_in":7200}

    private String access_token;
    private int expires_in;
    private Date create_date;


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }
}

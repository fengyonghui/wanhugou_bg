package com.wanhutong.backend.modules.biz.entity.pay.dto;

import org.omg.PortableInterceptor.INACTIVE;

public class RequestQRCodePayDto {

    private String out_trade_no;

    private Integer amount;

    private String notifyUrl;

    private String body;

    private String appid="whtFWQ0NXyXHdnKmIS";

    private String ciphertext="MIIBVgIBADANBgkqhkiG9w0BAQEFAASCAUAwggE8AgEAAkEAxa4Myt3XjZkjlpEMnX2cy/qMq4QLpQ6e6/XRNwb7Pmc1bNaeM7mtl/6Y9eZh24RQOE8VArhKWzmghLB+HIuWFQIDAQABAkB5JaWpXjOQD94DlHlKu4SwbahwJMiOK1ux+EBznM+0+a4HomW9gaNpkzH7O+bLLUJ0Bt6D0VCBgMapncWwRa2xAiEA+60zEUFakEoVnbdAfMwR19vZ5v9gRIYhNbuuylvGdlMCIQDJE2OFzwNmwwUHfhgghYq0uepqzXkWna+AkY0dpe1k9wIhAItQxvIIUU+KEwkpBaOPe1kYNDjwqKOF9CBAcnNF9khNAiEAgBNpgQGrGPl/apZWp+BnyVXiisT2LAGkmDAdwpS98WECIQD4aJctBaLyMgd22YNaZYx2y8cTmpacG/4BuGI3GOG/EA==";


    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}

package com.wanhutong.backend.common.utils;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class DsConfig {
    private static Configuration config = null;
    private static Configuration getConfig() {
        try {
                if (config == null) {

                        config = new PropertiesConfiguration("wanhugou.properties");
                }
        } catch (ConfigurationException e) {
                    e.printStackTrace();
            }
        return config;
    }
    public static String getImgServer(){return getConfig().getString("imgServer");}

    public static String getOldImgServer(){return getConfig().getString("oldImgServer");}

    public static String getWechatAppid(){return getConfig().getString("WECHAT_APPID");}

    public static String getWechatSecret(){return getConfig().getString("WECHAT_SECRET");}

    public static String getWechatMchSecret(){return getConfig().getString("WECHAT_MCH_SECRET");}

    public static String getAppid(){return getConfig().getString("appId");}

    public static String getNotifyUrl(){return getConfig().getString("notifyUrl");}

    public static String getSsecureKey(){return getConfig().getString("secureKey");}

    public static String getWxPostUrl(){return getConfig().getString("wxPostUrl");}

    public static String getAlipayPostUrl(){return getConfig().getString("alipayPostUrl");}

    public static String getAlipay4H5PostUrl(){return getConfig().getString("alipay4h5PostUrl");}

    public static String getUserGenCode(){
        return getConfig().getString("userGenCode");
    }

    public static String getLogisticUrl() {
        return getConfig().getString("logisticUrl");
    }

    public static String getActivityMsgUrl() {
        return getConfig().getString("activityMsgUrl");
    }

    public static void main(String[] args) {
        System.out.println(getImgServer());
    }

    public static String getCreateLogisticUrl() {
        return getConfig().getString("createLogisticUrl");
    }

    public static String getStartAndStopPointCodeWht() {
        return getConfig().getString("GET_START_AND_STOP_POINT_CODE_WHT");
    }

    public static String getAddOrderWht() {
        return getConfig().getString("ADD_ORDER_WHT");
    }
}

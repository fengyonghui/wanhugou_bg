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

    public static String getAppid(){return getConfig().getString("appId");}
    public static String getNotifyUrl(){return getConfig().getString("notifyUrl");}
    public static String getSsecureKey(){return getConfig().getString("secureKey");}

    public static String getWxPostUrl(){return getConfig().getString("wxPostUrl");}

    public static String getAlipayPostUrl(){return getConfig().getString("alipayPostUrl");}





    public static void main(String[] args) {
        System.out.println(getImgServer());
    }
}

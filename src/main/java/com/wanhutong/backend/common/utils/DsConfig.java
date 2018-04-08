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
    public static void main(String[] args) {
        System.out.println(getImgServer());
    }
}

package com.wanhutong.backend.modules.config.parse;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Ma.Qiang on 18/6/7.
 * 手机号配置文件
 */
@XStreamAlias("PhoneConfig")
public class PhoneConfig extends ConfigGeneral {

    @XStreamImplicit(itemFieldName = "phone")
    private List<Phone> phoneList;

    private static Map<String, Phone> emailMap;

    public static Phone getPhone(String receiver) {
        return emailMap.get(receiver);
    }

    @Override
    public PhoneConfig parse(String content) throws Exception {
        PhoneConfig config = XmlUtils.fromXml(content);
        Map<String, Phone> tempMap = Maps.newHashMap();
        for (Phone phone : config.phoneList) {
            tempMap.put(phone.getName(), phone);
        }
        emailMap = tempMap;
        return config;
    }


    public class Phone {
        /**
         * 接收方
         */
        private String name;
        /**
         * 收件人
         */
        private String number;


        public String getName() {
            return name;
        }

        public String getNumber() {
            return number;
        }
    }

    public enum PhoneType {
        /**
         * 线下支付记录异常
         */
        OFFLINE_PAY_RECORD_EXCEPTION,
        /**
         * 生成供货记录异常
         */
        SEND_GOODS_RECORD_EXCEPTION,
        /**
         * 发货提醒异常
         */
        DELIVER_EXCEPTION,
        /**
         * 佣金结算异常
         */
        SETTLEMENT_COMMISSION_EXCEPTION
    }
}

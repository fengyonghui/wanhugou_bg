package com.wanhutong.backend.modules.config.parse;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Ma.Qiang on 18/6/6.
 * 邮件配置文件
 */
@XStreamAlias("EmailConfig")
public class EmailConfig extends ConfigGeneral {

    @XStreamImplicit(itemFieldName = "email")
    private List<Email> emailList;

    private static Map<String, Email> emailMap;

    public static Email getEmail(String receiver) {
        return emailMap.get(receiver);
    }

    @Override
    public EmailConfig parse(String content) throws Exception {
        EmailConfig config = XmlUtils.fromXml(content);
        Map<String, Email> tempMap = Maps.newHashMap();
        for (Email email : config.emailList) {
            tempMap.put(email.getName(), email);
        }
        emailMap = tempMap;
        return config;
    }


    public class Email {
        /**
         * 接收方
         */
        private String name;
        /**
         * 收件人
         */
        private String receiveAddress;
        /**
         * 发件人
         */
        private String sendAddress;
        /**
         * 主题
         */
        private String subject;
        /**
         * 邮件内容
         */
        private String body;

        public String getSubject() {
            return subject;
        }

        public String getName() {
            return name;
        }

		public String getSendAddress() {
			return sendAddress;
		}

        public String getReceiveAddress() {
            return receiveAddress;
		}

		public String getBody() {
            return body;
        }
    }

    public enum EmailType {
        /**
         * 线下支付记录异常
         */
        OFFLINE_PAY_RECORD_EXCEPTION,
        /**
         * 通用异常
         */
        COMMON_EXCEPTION,
        /**
         * 生成供货记录异常
         */
        SEND_GOODS_RECORD_EXCEPTION,
        /**
         * 佣金结算异常
         */
        SETTLEMENT_COMMISSION_EXCEPTION
    }
}

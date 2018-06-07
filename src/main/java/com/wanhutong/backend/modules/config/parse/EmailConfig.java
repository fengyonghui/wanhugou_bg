package com.wanhutong.backend.modules.config.parse;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Ma.Qiang on 16/8/12.
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
        private String name; //接收方
        private String receiveAddress;//收件人
        private String sendAddress;//发件人
        private String subject; //主题
        private String body; //邮件内容

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
    }
}

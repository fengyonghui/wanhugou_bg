package com.wanhutong.backend.modules.config.parse;

/**
 * @author Ma.Qiang
 * 2018/5/28
 */

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支付审核数据配置
 *
 * @author ma.qiang
 */
@XStreamAlias("PaymentOrderProcessConfig")
public class PaymentOrderProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOrderProcessConfig.class);


    @XStreamAlias("defaultProcessId")
    private int defaultProcessId;

    @XStreamAlias("payProcessId")
    private int payProcessId;

    @XStreamAlias("endProcessId")
    private int endProcessId;

    @XStreamAlias("defaultBaseMoney")
    private BigDecimal defaultBaseMoney;

    @XStreamImplicit(itemFieldName = "process")
    private List<Process> processList;

    /**
     * 数据MAP
     */
    private Map<Integer, Process> processMap;

    @Override
    public PaymentOrderProcessConfig parse(String content) throws Exception {
        PaymentOrderProcessConfig paymentOrderProcessConfig = XmlUtils.fromXml(content);
        paymentOrderProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(paymentOrderProcessConfig.processList)) {
            for (Process e : paymentOrderProcessConfig.processList) {
                paymentOrderProcessConfig.processMap.put(e.getCode(), e);
            }
        }
        return paymentOrderProcessConfig;
    }

    /**
     * 取当前状态通过后的状态
     *
     * @param currentProcess 当前状态
     * @return 通过后的状态
     */
    public Process getPassProcess(BigDecimal money, Process currentProcess) {
        for (MoneyRole moneyRole : currentProcess.getMoneyRole()) {
           if (moneyRole.endMoney.compareTo(money) > 0 && moneyRole.startMoney.compareTo(money) <= 0) {
                return processMap.get(moneyRole.getPassCode());
           }
        }
        return null;
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentProcess 当前状态
     * @return 通过后的状态
     */
    public Process getRejectProcess(BigDecimal money, Process currentProcess) {
        for (MoneyRole moneyRole : currentProcess.getMoneyRole()) {
            if (moneyRole.endMoney.compareTo(money) > 0 && moneyRole.startMoney.compareTo(money) <= 0) {
                return processMap.get(moneyRole.getRejectCode());
            }
        }
        return null;
    }

    public Map<Integer, Process> getProcessMap() {
        return processMap;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public BigDecimal getDefaultBaseMoney() {
        return defaultBaseMoney;
    }

    public int getDefaultProcessId() {
        return defaultProcessId;
    }

    public int getPayProcessId() {
        return payProcessId;
    }

    public int getEndProcessId() {
        return endProcessId;
    }

    @XStreamAlias("process")
    public static class Process {
        /**
         * 状态码
         */
        @XStreamAlias("name")
        private String name;

        /**
         * 状态码
         */
        @XStreamAlias("code")
        private int code;


        @XStreamImplicit(itemFieldName = "moneyRole")
        private List<MoneyRole> moneyRole;



        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }


        public List<MoneyRole> getMoneyRole() {
            return moneyRole;
        }
    }

    @XStreamAlias("moneyRole")
    public static class MoneyRole {
        /**
         * 起始金额
         */
        @XStreamAlias("startMoney")
        private BigDecimal startMoney;
        /**
         * 结束金额
         */
        @XStreamAlias("endMoney")
        private BigDecimal endMoney;
        /**
         * 通过之后的状态
         */
        @XStreamAlias("passCode")
        private int passCode;

        /**
         * 拒绝之后的状态
         */
        @XStreamAlias("rejectCode")
        private int rejectCode;

        /**
         * 处理角色
         */
        @XStreamImplicit(itemFieldName = "roleEnNameEnum")
        private List<String> roleEnNameEnum;

        public int getPassCode() {
            return passCode;
        }

        public int getRejectCode() {
            return rejectCode;
        }

        public List<String> getRoleEnNameEnum() {
            return roleEnNameEnum;
        }

        public BigDecimal getStartMoney() {
            return startMoney;
        }

        public BigDecimal getEndMoney() {
            return endMoney;
        }
    }

}

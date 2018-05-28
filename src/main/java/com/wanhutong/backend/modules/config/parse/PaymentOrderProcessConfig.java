package com.wanhutong.backend.modules.config.parse;

/**
 * @author Ma.Qiang
 * 2017/10/8
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
 * 首页数据配置
 *
 * @author ma.qiang
 */
@XStreamAlias("PurchaseOrderProcessConfig")
public class PaymentOrderProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOrderProcessConfig.class);


    @XStreamAlias("defaultProcessId")
    private int defaultProcessId;

    @XStreamAlias("payProcessId")
    private int payProcessId;

    @XStreamImplicit(itemFieldName = "process")
    private List<PurchaseOrderProcess> processList;

    /**
     * 数据MAP
     */
    private Map<Integer, PurchaseOrderProcess> processMap;

    @Override
    public PaymentOrderProcessConfig parse(String content) throws Exception {
        PaymentOrderProcessConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        purchaseOrderProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(purchaseOrderProcessConfig.processList)) {
            for (PurchaseOrderProcess e : purchaseOrderProcessConfig.processList) {
                purchaseOrderProcessConfig.processMap.put(e.getCode(), e);
            }
        }
        return purchaseOrderProcessConfig;
    }

    /**
     * 取当前状态通过后的状态
     *
     * @param currentProcess 当前状态
     * @return 通过后的状态
     */
    public PurchaseOrderProcess getPassProcess(BigDecimal money, PurchaseOrderProcess currentProcess) {
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
    public PurchaseOrderProcess getRejectProcess(BigDecimal money, PurchaseOrderProcess currentProcess) {
        for (MoneyRole moneyRole : currentProcess.getMoneyRole()) {
            if (moneyRole.endMoney.compareTo(money) > 0 && moneyRole.startMoney.compareTo(money) <= 0) {
                return processMap.get(moneyRole.getRejectCode());
            }
        }
        return null;
    }

    public Map<Integer, PurchaseOrderProcess> getProcessMap() {
        return processMap;
    }

    public List<PurchaseOrderProcess> getProcessList() {
        return processList;
    }



    public int getDefaultProcessId() {
        return defaultProcessId;
    }

    public int getPayProcessId() {
        return payProcessId;
    }

    @XStreamAlias("process")
    public static class PurchaseOrderProcess {
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

package com.wanhutong.backend.modules.config.parse;

import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.XmlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 采购单审核数据配置
 *
 * @author ma.qiang
 */
@XStreamAlias("JointOperationOrderProcessOriginConfig")
public class JointOperationOrderProcessOriginConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(JointOperationOrderProcessOriginConfig.class);


    @XStreamAlias("zeroDefaultProcessId")
    private int zeroDefaultProcessId;
     @XStreamAlias("fifthDefaultProcessId")
    private int fifthDefaultProcessId;
     @XStreamAlias("allDefaultProcessId")
    private int allDefaultProcessId;


    @XStreamAlias("payProcessId")
    private int payProcessId;



    @XStreamImplicit(itemFieldName = "process")
    private List<PurchaseOrderProcess> processList;

    /**
     * 数据MAP
     */
    private Map<Integer, PurchaseOrderProcess> processMap;

    @Override
    public JointOperationOrderProcessOriginConfig parse(String content) throws Exception {
        JointOperationOrderProcessOriginConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
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
    public PurchaseOrderProcess getPassProcess(PurchaseOrderProcess currentProcess) {
        return processMap.get(currentProcess.getPassCode());
    }

    public Map<Integer, PurchaseOrderProcess> getProcessMap() {
        return processMap;
    }

    public List<PurchaseOrderProcess> getProcessList() {
        return processList;
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public PurchaseOrderProcess getRejectProcess(PurchaseOrderProcess currentEnum) {
        return processMap.get(currentEnum.getRejectCode());
    }

    public int getZeroDefaultProcessId() {
        return zeroDefaultProcessId;
    }

    public int getFifthDefaultProcessId() {
        return fifthDefaultProcessId;
    }

    public int getAllDefaultProcessId() {
        return allDefaultProcessId;
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

        /**
         * 处理角色
         */
        @XStreamImplicit(itemFieldName = "roleEnNameEnum")
        private List<String> roleEnNameEnum;

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

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

        public List<String> getRoleEnNameEnum() {
            return roleEnNameEnum;
        }

        public int getPassCode() {
            return passCode;
        }

        public int getRejectCode() {
            return rejectCode;
        }

    }

}

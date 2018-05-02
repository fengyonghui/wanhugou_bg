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
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 首页数据配置
 *
 * @author ma.qiang
 */
@XStreamAlias("PurchaseOrderProcessConfig")
public class PurchaseOrderProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderProcessConfig.class);


    @XStreamImplicit(itemFieldName = "process")
    private List<PurchaseOrderProcess> processList;

    /**
     * 数据MAP
     */
    public Map<Integer, PurchaseOrderProcess> processMap;

    @Override
    public PurchaseOrderProcessConfig parse(String content) throws Exception {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
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

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public PurchaseOrderProcess getRejectProcess(PurchaseOrderProcess currentEnum) {
        return processMap.get(currentEnum.getRejectCode());
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
        @XStreamAlias("roleEnNameEnum")
        private String roleEnNameEnum;

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

        public String getRoleEnNameEnum() {
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

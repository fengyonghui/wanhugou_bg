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

import java.util.List;
import java.util.Map;

/**
 * 供应商备货清单审核配置文件
 *
 * @author ma.qiang
 */
@XStreamAlias("VendorRequestOrderProcessConfig")
public class VendorRequestOrderProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(VendorRequestOrderProcessConfig.class);


    @XStreamAlias("defaultProcessId")
    private Integer defaultProcessId;

    @XStreamAlias("autProcessId")
    private Integer autProcessId;

    @XStreamImplicit(itemFieldName = "process")
    private List<RequestOrderProcess> processList;

    /**
     * 数据MAP
     */
    public Map<Integer, RequestOrderProcess> processMap;

    @Override
    public VendorRequestOrderProcessConfig parse(String content) throws Exception {
        VendorRequestOrderProcessConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        purchaseOrderProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(purchaseOrderProcessConfig.processList)) {
            for (RequestOrderProcess e : purchaseOrderProcessConfig.processList) {
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
    public RequestOrderProcess getPassProcess(RequestOrderProcess currentProcess) {
        return processMap.get(currentProcess.getPassCode());
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 拒绝后的状态
     */
    public RequestOrderProcess getRejectProcess(RequestOrderProcess currentEnum) {
        return processMap.get(currentEnum.getRejectCode());
    }

    public Integer getDefaultProcessId() {
        return defaultProcessId;
    }

    public Integer getAutProcessId() {
        return autProcessId;
    }

    @XStreamAlias("process")
    public static class RequestOrderProcess {
        /**
         * 状态码
         */
        @XStreamAlias("name")
        private String name;

        /**
         * 状态码
         */
        @XStreamAlias("code")
        private Integer code;

        /**
         * 处理角色
         */
        @XStreamImplicit(itemFieldName = "roleEnNameEnum")
        private List<String> roleEnNameEnum;

        /**
         * 通过之后的状态
         */
        @XStreamAlias("passCode")
        private Integer passCode;

        /**
         * 拒绝之后的状态
         */
        @XStreamAlias("rejectCode")
        private Integer rejectCode;

        public String getName() {
            return name;
        }

        public Integer getCode() {
            return code;
        }

        public List<String> getRoleEnNameEnum() {
            return roleEnNameEnum;
        }

        public Integer getPassCode() {
            return passCode;
        }

        public Integer getRejectCode() {
            return rejectCode;
        }


    }


}

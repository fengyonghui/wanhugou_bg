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
 * 备货清单审核配置文件
 *
 * @author Tengfei.Zhang
 */
@XStreamAlias("TransferProcessConfig")
public class TransferProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferProcessConfig.class);


    @XStreamAlias("defaultProcessId")
    private Integer defaultProcessId;

    @XStreamAlias("autProcessId")
    private Integer autProcessId;

    @XStreamImplicit(itemFieldName = "process")
    private List<TransferProcess> processList;

    /**
     * 数据MAP
     */
    public Map<Integer, TransferProcess> processMap;

    @Override
    public TransferProcessConfig parse(String content) throws Exception {
        TransferProcessConfig transferProcessConfig = XmlUtils.fromXml(content);
        transferProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(transferProcessConfig.processList)) {
            for (TransferProcess e : transferProcessConfig.processList) {
                transferProcessConfig.processMap.put(e.getCode(), e);
            }
        }
        return transferProcessConfig;
    }

    /**
     * 取当前状态通过后的状态
     *
     * @param currentProcess 当前状态
     * @return 通过后的状态
     */
    public TransferProcess getPassProcess(TransferProcess currentProcess) {
        return processMap.get(currentProcess.getPassCode());
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public TransferProcess getRejectProcess(TransferProcess currentEnum) {
        return processMap.get(currentEnum.getRejectCode());
    }

    public Integer getDefaultProcessId() {
        return defaultProcessId;
    }

    public Integer getAutProcessId() {
        return autProcessId;
    }

    public Map<Integer, TransferProcess> getProcessMap() {
        return processMap;
    }

    public List<TransferProcess> getProcessList() {
        return processList;
    }

    @XStreamAlias("process")
    public static class TransferProcess {
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

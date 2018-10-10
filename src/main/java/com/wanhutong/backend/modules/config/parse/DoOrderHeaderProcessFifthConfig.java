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
 * 备货清单审核配置文件
 *
 * @author ma.qiang
 */
@XStreamAlias("DoOrderHeaderProcessFifthConfig")
public class DoOrderHeaderProcessFifthConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoOrderHeaderProcessFifthConfig.class);

    @XStreamAlias("defaultProcessId")
    private Integer defaultProcessId;

    @XStreamAlias("fifthDefaultProcessId")
    private int fifthDefaultProcessId;
    @XStreamAlias("allDefaultProcessId")
    private int allDefaultProcessId;

    @XStreamAlias("createPoProcessId")
    private Integer createPoProcessId;

    @XStreamAlias("autProcessId")
    private Integer autProcessId;

    @XStreamImplicit(itemFieldName = "process")
    private List<OrderHeaderProcess> processList;

    /**
     * 数据MAP
     */
    public Map<Integer, OrderHeaderProcess> processMap;

    @Override
    public DoOrderHeaderProcessFifthConfig parse(String content) throws Exception {
        DoOrderHeaderProcessFifthConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        purchaseOrderProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(purchaseOrderProcessConfig.processList)) {
            for (OrderHeaderProcess e : purchaseOrderProcessConfig.processList) {
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
    public OrderHeaderProcess getPassProcess(OrderHeaderProcess currentProcess) {
        return processMap.get(currentProcess.getPassCode());
    }

    public Map<Integer, OrderHeaderProcess> getProcessMap() {
        return processMap;
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public OrderHeaderProcess getRejectProcess(OrderHeaderProcess currentEnum) {
        return processMap.get(currentEnum.getRejectCode());
    }

    public Integer getDefaultProcessId() {
        return defaultProcessId;
    }

    public int getFifthDefaultProcessId() {
        return fifthDefaultProcessId;
    }

    public void setFifthDefaultProcessId(int fifthDefaultProcessId) {
        this.fifthDefaultProcessId = fifthDefaultProcessId;
    }

    public int getAllDefaultProcessId() {
        return allDefaultProcessId;
    }

    public void setAllDefaultProcessId(int allDefaultProcessId) {
        this.allDefaultProcessId = allDefaultProcessId;
    }

    public Integer getCreatePoProcessId() {
        return createPoProcessId;
    }

    public Integer getAutProcessId() {
        return autProcessId;
    }

    public List<OrderHeaderProcess> getProcessList() {
        return processList;
    }

    @XStreamAlias("process")
    public static class OrderHeaderProcess {
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
        @XStreamAlias("roleEnNameEnum")
        private String roleEnNameEnum;

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

        /**
         * 通过之后的状态 全部支付
         */
        @XStreamAlias("allPassCode")
        private int allPassCode;

        /**
         * 通过之后的状态 20%支付
         */
        @XStreamAlias("fifthPassCode")
        private int fifthPassCode;

        /**
         * 支付之后的状态 20%支付
         */
        @XStreamAlias("fifthPayCode")
        private Integer fifthPayCode;

        /**
         * 支付之后的状态 100%支付
         */
        @XStreamAlias("allPayCode")
        private Integer allPayCode;

        public String getName() {
            return name;
        }

        public Integer getCode() {
            return code;
        }

        public String getRoleEnNameEnum() {
            return roleEnNameEnum;
        }

        public Integer getPassCode() {
            return passCode;
        }

        public Integer getRejectCode() {
            return rejectCode;
        }

        public int getAllPassCode() {
            return allPassCode;
        }

        public void setAllPassCode(int allPassCode) {
            this.allPassCode = allPassCode;
        }

        public int getFifthPassCode() {
            return fifthPassCode;
        }

        public void setFifthPassCode(int fifthPassCode) {
            this.fifthPassCode = fifthPassCode;
        }

        public Integer getFifthPayCode() {
            return fifthPayCode;
        }

        public void setFifthPayCode(Integer fifthPayCode) {
            this.fifthPayCode = fifthPayCode;
        }

        public Integer getAllPayCode() {
            return allPayCode;
        }

        public void setAllPayCode(Integer allPayCode) {
            this.allPayCode = allPayCode;
        }
    }


}

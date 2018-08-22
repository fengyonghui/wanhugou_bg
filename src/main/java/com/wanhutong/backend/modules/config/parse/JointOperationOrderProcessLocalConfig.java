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
@XStreamAlias("JointOperationOrderProcessLocalConfig")
public class JointOperationOrderProcessLocalConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(JointOperationOrderProcessLocalConfig.class);

    public static final String ORDER_TABLE_NAME = "ORDER_HEADER_SO_LOCAL";

    @XStreamAlias("zeroDefaultProcessId")
    private int zeroDefaultProcessId;
     @XStreamAlias("fifthDefaultProcessId")
    private int fifthDefaultProcessId;
     @XStreamAlias("allDefaultProcessId")
    private int allDefaultProcessId;


    @XStreamImplicit(itemFieldName = "payProcessId")
    private List<Integer> payProcessId;



    @XStreamImplicit(itemFieldName = "process")
    private List<Process> processList;

    /**
     * 数据MAP
     */
    private Map<Integer, Process> processMap;

    @Override
    public JointOperationOrderProcessLocalConfig parse(String content) throws Exception {
        JointOperationOrderProcessLocalConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        purchaseOrderProcessConfig.processMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(purchaseOrderProcessConfig.processList)) {
            for (Process e : purchaseOrderProcessConfig.processList) {
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
    public Process getPassProcess(Process currentProcess) {
        return processMap.get(currentProcess.getPassCode());
    }

    public Map<Integer, Process> getProcessMap() {
        return processMap;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public Process getRejectProcess(Process currentEnum) {
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

    public List<Integer> getPayProcessId() {
        return payProcessId;
    }



}

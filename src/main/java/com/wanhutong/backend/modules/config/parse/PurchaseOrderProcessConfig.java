package com.wanhutong.backend.modules.config.parse;

import com.google.common.collect.Lists;
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
@XStreamAlias("PurchaseOrderProcessConfig")
public class PurchaseOrderProcessConfig extends ConfigGeneral {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderProcessConfig.class);


    @XStreamAlias("defaultProcessId")
    private int defaultProcessId;

    @XStreamAlias("defaultNewProcessId")
    private int defaultNewProcessId;

    @XStreamAlias("orderHeaderDefaultProcessId")
    private int orderHeaderDefaultProcessId;

    @XStreamAlias("payProcessId")
    private int payProcessId;

    @XStreamImplicit(itemFieldName = "process")
    private List<Process> processList;

    /**
     * 过滤条件显示
     */
    private List<Process> showFilterProcessList;

    /**
     * 数据MAP
     */
    private Map<Integer, Process> processMap;
    /**
     * 数据MAP
     */
    private Map<String, List<Process>> nameProcessMap;
    /**
     * 过滤条件显示
     */
    private Map<Integer, Process> showFilterProcessMap;

    @Override
    public PurchaseOrderProcessConfig parse(String content) throws Exception {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = XmlUtils.fromXml(content);
        purchaseOrderProcessConfig.processMap = Maps.newHashMap();
        purchaseOrderProcessConfig.showFilterProcessMap = Maps.newHashMap();
        purchaseOrderProcessConfig.nameProcessMap = Maps.newHashMap();
        purchaseOrderProcessConfig.showFilterProcessList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(purchaseOrderProcessConfig.processList)) {
            for (Process e : purchaseOrderProcessConfig.processList) {
                purchaseOrderProcessConfig.processMap.put(e.getCode(), e);
                List<Process> processes = purchaseOrderProcessConfig.nameProcessMap.putIfAbsent(e.getName(), Lists.newArrayList(e));
                if (CollectionUtils.isNotEmpty(processes)) {
                    processes.add(e);
                }
                if (Boolean.valueOf(e.getShowFilter())) {
                    purchaseOrderProcessConfig.showFilterProcessMap.put(e.getCode(), e);
                    purchaseOrderProcessConfig.showFilterProcessList.add(e);
                }
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

    public Map<Integer, Process> getShowFilterProcessMap() {
        return showFilterProcessMap;
    }

    public Map<Integer, Process> getProcessMap() {
        return processMap;
    }

    public List<Process> getProcessList() {
        return processList;
    }

    public List<Process> getShowFilterProcessList() {
        return showFilterProcessList;
    }

    public Map<String, List<Process>> getNameProcessMap() {
        return nameProcessMap;
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

    public int getDefaultProcessId() {
        return defaultProcessId;
    }

    public int getDefaultNewProcessId() {
        return defaultNewProcessId;
    }

    public int getOrderHeaderDefaultProcessId() {
        return orderHeaderDefaultProcessId;
    }

    public int getPayProcessId() {
        return payProcessId;
    }


}

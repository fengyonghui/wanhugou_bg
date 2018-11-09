package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum TransferStatusEnum {
    //业务状态：0: 未审核；10:审核完成; 20:出库中; 30:已出库; 40:入库中；50:已入库 60:取消
    UNREVIEWED(0, "未审核"),
    APPROVE(10, "审核完成"),
    OUTING_WAREHOUSE(20, "出库中"),
    ALREADY_OUT_WAREHOUSE(30, "已出库"),
    INING_WAREHOUSE(40, "入库中"),
    ALREADY_IN_WAREHOUSE(50, "已入库"),
    CLOSE(60, "取消"),
    ;
    private Integer state;
    private String desc;

    private static Map<Integer, TransferStatusEnum> statusMap;
    private static Map<Integer, String> stateDescMap;

    TransferStatusEnum(int st, String desc) {
        this.state=st;
        this.desc=desc;
    }
    public static TransferStatusEnum stateOf(Integer index) {
        for (TransferStatusEnum statusEnum : values()) {
            if (statusEnum.state.intValue() == index) {
                return statusEnum;
            }
        }
        return null;
    }


    public static Map<Integer, String> getStateDescMap() {
        if (stateDescMap == null) {
            Map<Integer, String> mapTemp = Maps.newHashMap();
            for (TransferStatusEnum statusEnum : values()) {
                mapTemp.put(statusEnum.getState(), statusEnum.getDesc());
            }
            stateDescMap = mapTemp;
        }
        return stateDescMap;
    }

    public static Map<Integer, TransferStatusEnum> getStatusMap() {
        if (statusMap == null) {
            Map<Integer, TransferStatusEnum> mapTemp = Maps.newHashMap();
            for (TransferStatusEnum statusEnum : values()) {
                mapTemp.put(statusEnum.getState(), statusEnum);
            }
            statusMap = mapTemp;
        }
        return statusMap;
    }


    public Integer getState() {
        return state;
    }


    public String getDesc() {
        return desc;
    }

    public static TransferStatusEnum getEnum(String desc){
        for (TransferStatusEnum statusEnum : values()) {
            if (desc.equals(statusEnum.getDesc())){
                return statusEnum;
            }
        }
        return null;
    }
}

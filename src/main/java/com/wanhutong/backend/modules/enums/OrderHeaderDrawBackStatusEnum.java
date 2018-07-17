package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public enum OrderHeaderDrawBackStatusEnum {
    /**
     * 订单退款：0:"退款申请"，  1:"退款中", 2:"驳回", 3:"退款完成"
     */
    REFUND(0, "退款申请"),
    REFUNDING(1, "退款中"),
    REFUNDREJECT(2, "驳回"),
    REFUNDED(3, "退款完成");

    private Integer state;
    private String desc;

    private static Map<Integer, OrderHeaderDrawBackStatusEnum> statusMap;

    OrderHeaderDrawBackStatusEnum(int st, String desc) {
        this.state=st;
        this.desc=desc;
    }

    public static Map<Integer, OrderHeaderDrawBackStatusEnum> getStatusMap() {
        if (statusMap == null) {
            Map<Integer, OrderHeaderDrawBackStatusEnum> mapTemp = Maps.newHashMap();
            for (OrderHeaderDrawBackStatusEnum statusEnum : values()) {
                mapTemp.put(statusEnum.getState(), statusEnum);
            }
            statusMap = mapTemp;
        }
        return statusMap;
    }

    public static OrderHeaderDrawBackStatusEnum stateOf(Integer index) {
        for (OrderHeaderDrawBackStatusEnum statusEnum : values()) {
            if (statusEnum.state.intValue() == index) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}

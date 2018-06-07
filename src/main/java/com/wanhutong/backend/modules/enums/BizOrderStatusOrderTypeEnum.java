package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum BizOrderStatusOrderTypeEnum {
    SELLORDER(0, "biz_order_header"),
    REPERTOIRE(1, "biz_request_header"),
    PURCHASEORDER(2, "biz_po_header");

    private Integer state;
    private String desc;

    private static Map<Integer, BizOrderStatusOrderTypeEnum> orderTypeMap;

    BizOrderStatusOrderTypeEnum(Integer state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public static BizOrderStatusOrderTypeEnum stateOf(Integer index) {
        for (BizOrderStatusOrderTypeEnum orderEnum : values()) {
            if (orderEnum.state.intValue() == index) {
                return orderEnum;
            }
        }
        return null;
    }

    public static Map<Integer, BizOrderStatusOrderTypeEnum> getOrderTypeMap() {
        if (orderTypeMap == null) {
            Map<Integer, BizOrderStatusOrderTypeEnum> mapTemp = Maps.newHashMap();
            for (BizOrderStatusOrderTypeEnum orderTpyeEnum : values()) {
                mapTemp.put(orderTpyeEnum.getState(), orderTpyeEnum);
            }
            orderTypeMap = mapTemp;
        }
        return orderTypeMap;
    }

    public Integer getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }
}

package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * biz_order_status记录表中order_type类型
 * <p>
 * state:0 代表订单业务状态状态改变
 * state:1 代表备货单业务状态状态改变
 * state:0 代表采购单业务状态状态改变
 *
 * @author wangby
 */
public enum BizOrderStatusOrderTypeEnum {
    SELLORDER(0, "biz_order_header"),
    REPERTOIRE(1, "biz_request_header"),
    PURCHASEORDER(2, "biz_po_header"),
    SKUTRANSFER(5,"biz_sku_transfer");

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

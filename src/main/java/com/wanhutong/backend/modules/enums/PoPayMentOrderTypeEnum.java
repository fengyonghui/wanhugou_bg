package com.wanhutong.backend.modules.enums;


public enum PoPayMentOrderTypeEnum {

    /**
     * 采购单付款
     */
    PO_TYPE(1),
    /**
     * 订单付款
     */
    ORDER_TYPE(2),
    /**
     * 备货单付款
     */
    REQ_TYPE(3);
    private Integer type;

    PoPayMentOrderTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static PoPayMentOrderTypeEnum typeOf(String index) {
        for (PoPayMentOrderTypeEnum type : values()) {
            if (type.getType().equals(index)) {
                return type;
            }
        }
        return null;
    }
}

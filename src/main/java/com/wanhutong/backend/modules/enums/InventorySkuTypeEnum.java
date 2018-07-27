package com.wanhutong.backend.modules.enums;


public enum InventorySkuTypeEnum {

    /**
     * 采购中心库存商品
     */
    CENTER_TYPE(1),
    /**
     * 供应商库存商品
     */
    VENDOR_TYPE(2);
    private Integer type;

    InventorySkuTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static InventorySkuTypeEnum typeOf(String index) {
        for (InventorySkuTypeEnum type : values()) {
            if (type.getType().equals(index)) {
                return type;
            }
        }
        return null;
    }
}

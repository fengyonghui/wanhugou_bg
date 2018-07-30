package com.wanhutong.backend.modules.enums;


public enum ReqFromTypeEnum {

    /**
     * 采购中心备货
     */
    CENTER_TYPE(1),
    /**
     * 供应商备货
     */
    VENDOR_TYPE(2);
    private Integer type;

    ReqFromTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static ReqFromTypeEnum typeOf(String index) {
        for (ReqFromTypeEnum type : values()) {
            if (type.getType().equals(index)) {
                return type;
            }
        }
        return null;
    }
}

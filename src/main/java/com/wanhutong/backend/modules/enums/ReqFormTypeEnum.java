package com.wanhutong.backend.modules.enums;


public enum ReqFormTypeEnum {

    //采购中心备货
    CENTER_TYPE(1),
    //供应商备货
    VENDOR_TYPE(2);
    private Integer type;

    ReqFormTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static ReqFormTypeEnum typeOf(String index) {
        for (ReqFormTypeEnum type : values()) {
            if (type.getType().equals(index)) {
                return type;
            }
        }
        return null;
    }
}

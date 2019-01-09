package com.wanhutong.backend.modules.enums;

public enum BizOrderLogisticsEnum {
    CUSTOMER_PICK_UP(1, "客户自提"),
    DELIVER_HOME(2, "送货到家"),
    DIRECT_MANUFACTURERS(3, "厂家直发");

    private Integer state;
    private String desc;

    BizOrderLogisticsEnum(Integer state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public static BizOrderLogisticsEnum descOf(String desc) {
        for (BizOrderLogisticsEnum statusEnum : values()) {
            if (statusEnum.desc.equals(desc)) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package com.wanhutong.backend.modules.enums;

public enum BizMessageCompanyTypeEnum {

    CONSUMER(15, "全部零售商"),
    CONSIGNEE(16, "全部代销商"),
    PURCHASERS(6, "全部经销商"),
    SUPPLY_CHAIN(7, "全部供应商"),
    OTHER_TYPE(20, "部分用户");

    private int type;
    private String desc;

    BizMessageCompanyTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

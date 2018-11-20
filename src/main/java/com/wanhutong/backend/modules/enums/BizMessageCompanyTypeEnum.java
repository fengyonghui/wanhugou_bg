package com.wanhutong.backend.modules.enums;

public enum BizMessageCompanyTypeEnum {

    CONSUMER(1, "全部零售商"),
    CONSIGNEE(2, "全部代销商"),
    PURCHASERS(3, "全部经销商"),
    SUPPLY_CHAIN(4, "全部供应商"),
    OTHER_TYPE(15, "部分用户");

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

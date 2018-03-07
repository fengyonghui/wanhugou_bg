package com.wanhutong.backend.modules.enums;

/**
 * 订单统计数据类型枚举
 *
 * @author Ma.Qiang
 */
public enum  OrderStatisticsDataTypeEnum {
    /**
     * 销售增长率
     */
    SALEROOM(1,"销售额"),
    SALES_GROWTH_RATE(2,"销售增长率"),
    ;
    private int code;
    private String desc;

    OrderStatisticsDataTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatisticsDataTypeEnum parse(int i) {
        for (OrderStatisticsDataTypeEnum type : values()) {
            if (type.getCode() == i) {
                return type;
            }
        }
        return SALEROOM;
    }
}

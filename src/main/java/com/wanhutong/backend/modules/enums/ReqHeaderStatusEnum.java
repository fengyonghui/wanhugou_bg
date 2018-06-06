package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Maps;

import java.util.Map;

public enum ReqHeaderStatusEnum {
    //业务状态：0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
    UNREVIEWED(0, "未审核"),
    INITIAL_PAY(1, "首付款支付"),
    ALL_PAY(2, "全部支付"),
    APPROVE(5, "审核通过"),
    PURCHASING(10, "采购中"),
    ACCOMPLISH_PURCHASE(15, "采购完成"),
    STOCKING(20, "备货中"),
    STOCK_COMPLETE(25, "供货完成"),
    COMPLETE(30, "收货完成"),
    CLOSE(35, "关闭");

    private Integer state;
    private String desc;

    private static Map<Integer, ReqHeaderStatusEnum> statusMap;

    ReqHeaderStatusEnum(int st, String desc) {
        this.state=st;
        this.desc=desc;
    }
    public static ReqHeaderStatusEnum stateOf(Integer index) {
        for (ReqHeaderStatusEnum statusEnum : values()) {
            if (statusEnum.state.intValue() == index) {
                return statusEnum;
            }
        }
        return null;
    }


    public static Map<Integer, ReqHeaderStatusEnum> getStatusMap() {
        if (statusMap == null) {
            Map<Integer, ReqHeaderStatusEnum> mapTemp = Maps.newHashMap();
            for (ReqHeaderStatusEnum statusEnum : values()) {
                mapTemp.put(statusEnum.getState(), statusEnum);
            }
            statusMap = mapTemp;
        }
        return statusMap;
    }


    public Integer getState() {
        return state;
    }


    public String getDesc() {
        return desc;
    }
}

package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Maps;
import org.restlet.resource.Get;

import java.util.Map;

public enum ReqHeaderStatusEnum {
    //业务状态：0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
    UNREVIEWED(0, "未审核"),
    INITIAL_PAY(1, "首付款支付"),
    ALL_PAY(2, "全部支付"),
    IN_REVIEW(4,"审核中"),
    APPROVE(5, "采销部审核通过"),
    PURCHASING(10, "采购中"),
    ACCOMPLISH_PURCHASE(15, "待供货"),
    STOCKING(20, "供货中"),
    STOCK_COMPLETE(25, "供货完成"),
    COMPLETEING(27, "收货中"),
    COMPLETE(30, "收货完成"),
    VEND_INITIAL_PAY(35,"部分结算"),
    VEND_ALL_PAY(37,"结算完成"),
    CLOSE(40, "关闭"),

    //通用流程Entity对应业务状态
    PURCHASE(40, "待采购中心经理审批"),
    CHANNEL(45, "待渠道部经理审批"),
    REJECT(50, "驳回");


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

    public static ReqHeaderStatusEnum getEnum(String desc){
        for (ReqHeaderStatusEnum statusEnum : values()) {
            if (desc.equals(statusEnum.getDesc())){
                return statusEnum;
            }
        }
        return null;
    }
}

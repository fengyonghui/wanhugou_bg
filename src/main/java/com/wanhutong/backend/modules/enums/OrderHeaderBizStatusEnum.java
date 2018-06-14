package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public enum OrderHeaderBizStatusEnum {


 //   地址管理里的默认地址类型  业务状态 0未支付； 5首付款支付  10全部支付 15同意发货(供货中) 16采购中心供货 17采购中 18采购完成 19 供应中心供货 20已发货 25客户已收货 30已完成35已取消40已删除45审核失败
    ORDER_DEFAULTSTATUS(1, "新建订单"),
    UNPAY(0, "未支付"),
    INITIAL_PAY(5, "首付款支付"),
    ALL_PAY(10, "全部支付"),
    SUPPLYING(15, "同意发货(供货中)"),
    APPROVE(16, "采购中心供货"),
    PURCHASING(17, "采购中"),
    ACCOMPLISH_PURCHASE(18, "采购完成"),
    STOCKING(19,"供应中心供货"),
    SEND(20,"已发货"),
    RECEIVED(25,"客户已收货"),
    COMPLETE(30,"已完成"),
    CANCLE(35,"已取消"),
    DELETE(40,"已删除"),
    UNAPPROVE(45, "审核失败"),

    REFUND(50, "退款申请"),
    REFUNDING(55, "退款中"),
    REFUNDED(60, "退款完成");

    private Integer state;
    private String desc;

    private static Map<Integer, OrderHeaderBizStatusEnum> statusMap;

    OrderHeaderBizStatusEnum(int st, String desc) {
        this.state=st;
        this.desc=desc;
    }

    public static Map<Integer, OrderHeaderBizStatusEnum> getStatusMap() {
        if (statusMap == null) {
            Map<Integer, OrderHeaderBizStatusEnum> mapTemp = Maps.newHashMap();
            for (OrderHeaderBizStatusEnum statusEnum : values()) {
                mapTemp.put(statusEnum.getState(), statusEnum);
            }
            statusMap = mapTemp;
        }
        return statusMap;
    }

    public static OrderHeaderBizStatusEnum stateOf(Integer index) {
        for (OrderHeaderBizStatusEnum statusEnum : values()) {
            if (statusEnum.state.intValue() == index) {
                return statusEnum;
            }
        }
        return null;
    }

    public Integer getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 无效订单状态集合
     * DELETE 状态情况特殊,在订单已支付的情况下也是有效订单
     */
    public static final List<OrderHeaderBizStatusEnum> INVALID_STATUS = Lists.newArrayList(
            UNAPPROVE,CANCLE,UNPAY,DELETE
    );

    /**
     * 订单导出集合
     *  尾款信息 状态判断是否有尾款
     * */
    public static final List<OrderHeaderBizStatusEnum> EXPORT_TAIL = Lists.newArrayList(
            ALL_PAY, CANCLE, DELETE, UNAPPROVE
    );

}

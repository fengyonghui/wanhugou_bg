package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Lists;

import java.util.List;

public enum OrderHeaderBizStatusEnum {


 //   地址管理里的默认地址类型  业务状态 0未支付；5首付款支付 10全部支付 15同意发货(供货中) 17采购中 18采购完成 19 供应中心供货 20已发货 25客户已收货 30已完成35已取消40已删除45审核失败
    ORDER_DEFAULTSTATUS(1),UNPAY(0),INITIAL_PAY(5),ALL_PAY(10),SUPPLYING(15),APPROVE(16),PURCHASING(17),ACCOMPLISH_PURCHASE(18),
    STOCKING(19),SEND(20),RECEIVED(25),COMPLETE(30),CANCLE(35),DELETE(40),UNAPPROVE(45);

    private Integer state;
    OrderHeaderBizStatusEnum(int st) {
        this.state=st;
    }
    public static OrderHeaderBizStatusEnum stateOf(Integer index) {
        for (OrderHeaderBizStatusEnum statusEnum : values()) {
            if (statusEnum.state==index) {
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

    /**
     * 有效订单状态集合
     * DELETE 状态情况特殊,在订单已支付的情况下也是有效订单
     */
    public static final List<OrderHeaderBizStatusEnum> VALID_STATUS = Lists.newArrayList(
            SUPPLYING,
            PURCHASING,
            ACCOMPLISH_PURCHASE,
            STOCKING,
            SEND,
            RECEIVED,
            COMPLETE
    );
}

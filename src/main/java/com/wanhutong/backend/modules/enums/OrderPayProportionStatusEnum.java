package com.wanhutong.backend.modules.enums;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;

import java.math.BigDecimal;

/**
 * 支付比例
 *
 * @author Ma.Qiang
 */
public enum OrderPayProportionStatusEnum {


    ZERO(0, 0, 19, "0%首付款"),
    FIFTH(1, 20, 99, "20%首付款"),
    ALL(2, 100, 10000, "全部支付"),
    UNKNOWN(-1, -1, -1, "未知"),;

    private Integer state;
    private Integer minProportion;
    private Integer maxProportion;
    private String desc;

    OrderPayProportionStatusEnum(int st, int minProportion, int maxProportion, String desc) {
        this.state = st;
        this.minProportion = minProportion;
        this.maxProportion = maxProportion;
        this.desc = desc;
    }


    /**
     * 根据支付金额和应支付金额确认支付比例
     *
     * @param orderTotal 除数 应付金额
     * @param payTotal 被除数 支付金额
     * @return
     */
    public static OrderPayProportionStatusEnum parse(BigDecimal orderTotal, BigDecimal payTotal) {
        BigDecimal divide = payTotal.divide(orderTotal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));

        for (OrderPayProportionStatusEnum statusEnum : values()) {
            if (statusEnum.minProportion <= divide.intValue() && statusEnum.maxProportion >= divide.intValue()) {
                return statusEnum;
            }
        }
        return UNKNOWN;
    }

    /**
     * 根据支付金额和应支付金额确认支付比例
     *
     * @param orderTotal 除数 应付金额
     * @param payTotal 被除数 支付金额
     * @return
     */
    public static OrderPayProportionStatusEnum parse(Double orderTotal, Double payTotal) {
        if (orderTotal <= 0) {
            return ALL;
        }

        return OrderPayProportionStatusEnum.parse(BigDecimal.valueOf(orderTotal), BigDecimal.valueOf(payTotal));
    }

    /**
     * 根据支付金额和应支付金额确认支付比例
     *
     * @return
     */
    public static OrderPayProportionStatusEnum parse(BizOrderHeader b) {
        return OrderPayProportionStatusEnum.parse(b.getTotalDetail() + b.getFreight() + b.getTotalExp() + b.getServiceFee(), b.getReceiveTotal() + (b.getScoreMoney() == null ? 0 : b.getScoreMoney().doubleValue()));
    }

    public static OrderPayProportionStatusEnum parse(Integer index) {
        for (OrderPayProportionStatusEnum statusEnum : values()) {
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

    public Integer getMinProportion() {
        return minProportion;
    }

    public Integer getMaxProportion() {
        return maxProportion;
    }
}

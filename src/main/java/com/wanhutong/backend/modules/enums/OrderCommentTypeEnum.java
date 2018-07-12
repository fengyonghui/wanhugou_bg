package com.wanhutong.backend.modules.enums;

import com.wanhutong.backend.common.utils.StringUtils;

public enum OrderCommentTypeEnum {
    /**
     * 采购商备注
     */
    CUSTCOMMENT(1),
    /**
     * 采购专员备注
     */
    BUYERCOMMENT(2),
    /**
     * 其余人员备注
     */
    OHTERCOMMENT(3);

    private Integer type;

    OrderCommentTypeEnum(Integer type) {
        this.type=type;
    }

    public Integer getType() {
        return type;
    }

    public static OrderCommentTypeEnum parse (Integer type) {
        for (OrderCommentTypeEnum enNameEnum : values()) {
            if (enNameEnum.type.intValue() == type) {
                return enNameEnum;
            }
        }
        return null;
    }
}

package com.wanhutong.backend.modules.enums;

import org.apache.commons.lang3.StringUtils;

public enum RoleEnNameEnum {
    /**
     * 采购中心经理
     */
    P_CENTER_MANAGER("p_center_manager", "采购中心经理"),
    /**
     * 采购专员
     */
    BUYER("buyer", "采购专员"),
    /**
     * 运营总监
     */
    OP_DIRECTOR("op_director", "运营总监"),
    /**
     * 总经理
     */
    GENERAL_MANAGER("General manager", "总经理"),
    /**
     * 财务
     */
    FINANCE("finance", "财务"),
    /**
     * 出纳
     */
    TELLER("teller", "出纳"),
    /**
     * 备货专员
     */
    STOCKREADYCOMMISSIONER("stock_ready_commissioner", "备货专员"),
    /**
     * 采销经理
     */
    MARKETINGMANAGER("marketing_manager", "采销经理"),
    /**
     * 仓储专员
     */
    WAREHOUSESPECIALIST("warehouse_specialist", "仓储专员"),
    /**
     * 选品专员
     */
    SELECTIONOFSPECIALIST("selection_of_specialist", "选品专员"),
    /**
     * 供货中心经理
     */
    PROVIDER_MANAGER("provider_manager", "供货中心经理"),
    /**
     * 供应商
     */
    SUPPLY_CHAIN("supply-chain", "供应商"),

    CHANNEL_MANAGER("channel_manager","渠道经理"),

    /**
     * 系统管理员
     */
    DEPT("dept", "系统管理员");





    private String state;
    private String desc;

    RoleEnNameEnum(String state, String desc) {
        this.state=state;
        this.desc=desc;
    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public static RoleEnNameEnum parse (String state) {
        if (StringUtils.isBlank(state)) {
            return null;
        }
        for (RoleEnNameEnum enNameEnum : values()) {
            if (enNameEnum.getState().equals(state)) {
                return enNameEnum;
            }
        }
        return null;
    }
}

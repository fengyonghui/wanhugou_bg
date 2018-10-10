package com.wanhutong.backend.modules.enums;

import org.apache.commons.lang3.StringUtils;

public enum RoleEnNameEnum {
    /**
     * 采购中心经理
     */
    P_CENTER_MANAGER("p_center_manager", "采购中心经理"),
    /**
     * 客户专员
     */
    BUYER("buyer", "客户专员"),
    /**
     * 运营总监
     */
    OP_DIRECTOR("op_director", "运营总监"),
    /**
     * 总经理
     */
    GENERAL_MANAGER("General manager", "总经理"),

    /**
     * 财务经理
     */
    FINANCE("finance", "财务经理"),

    /**
     * 财务总经理
     */
    FINANCIAL_GENERAL_MANAGER ("financial_general_manager ", "财务总经理"),

    /**
     * 财务总监
     */
    FINANCE_DIRECTOR("finance_director", "财务总监"),
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
     * 品类主管
     */
    SELECTION_OF_SPECIALIST("selection_of_specialist", "品类主管"),
    /**
     * 供货中心经理
     */
    PROVIDER_MANAGER("provider_manager", "供货中心经理"),
    /**
     * 发货专员
     */
    SHIPPER("shipper", "发货专员"),
    /**
     * 供应商
     */
    SUPPLY_CHAIN("supply-chain", "供应商"),

    /**
     * 渠道经理
     */
    CHANNEL_MANAGER("channel_manager","渠道经理"),

    /**
     * 渠道主管
     */
    CHANNEL_SUPERVISOR("Channel Supervisor","渠道主管"),

    /**
     * 提付款专员
     */
    PAYMENT_SPECIALIST_SUBMIT("payment_specialist_submit","提付款专员"),

    /**
     * 验货员
     */
    INSPECTOR("inspector","验货员"),

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

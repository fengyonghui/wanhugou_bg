package com.wanhutong.backend.modules.enums;

public enum RoleEnNameEnum {
    /**
     * 采购中心经理
     */
    P_CENTER_MANAGER("p_center_manager"),
    /**
     * 采购专员
     */
    BUYER("buyer"),
    /**
     * 运营总监
     */
    OP_DIRECTOR("op_director"),
    /**
     * 总经理
     */
    GENERAL_MANAGER("general_manager"),
    /**
     * 财务
     */
    FINANCE("finance"),
    /**
     * 备货专员
     */
    STOCKREADYCOMMISSIONER("stock_ready_commissioner"),
    /**
     * 采销经理
     */
    MARKETINGMANAGER("marketing_manager"),
    /**
     * 仓储专员
     */
    WAREHOUSESPECIALIST("warehouse_specialist"),
    /**
     * 供货中心经理
     */
    PROVIDER_MANAGER("provider_manager"),
    /**
     * 供应商
     */
    SUPPLY_CHAIN("supply-chain"),
    /**
     * 系统管理员
     */
    DEPT("dept");



    private String state;

    RoleEnNameEnum(String state) {

        this.state=state;
    }

    public static void main(String[] args) {


        System.out.println( P_CENTER_MANAGER.getState() );


    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

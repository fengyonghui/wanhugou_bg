package com.wanhutong.backend.modules.enums;

public enum RoleEnNameEnum {
    P_CENTER_MANAGER("p_center_manager"),
    BUYER("buyer"),
    STOCKREADYCOMMISSIONER("stock_ready_commissioner"),
    MARKETINGMANAGER("marketing_manager"),
    WAREHOUSESPECIALIST("warehouse_specialist"),
    SUPPLY_CHAIN("supply-chain"),
    DEPT("dept");//系统管理员
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

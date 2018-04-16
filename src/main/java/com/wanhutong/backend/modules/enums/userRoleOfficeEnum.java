package com.wanhutong.backend.modules.enums;

/**
 * Created by CK on 2016/7/14.
 */
public enum userRoleOfficeEnum {
    //用户角色，采购专员角色ID，采购中心经理角色ID
    PURCHASE("10"),MANAGER("13");

   private String type;

    userRoleOfficeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static userRoleOfficeEnum stateOf(String index) {
        for (userRoleOfficeEnum state : values()) {
            if (state.getType().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {


        System.out.println( userRoleOfficeEnum.PURCHASE.ordinal() );


    }
}

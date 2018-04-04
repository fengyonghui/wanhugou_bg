package com.wanhutong.backend.modules.enums;

/**
 * 用于存放仓库ID
 * ZhangTengfei
 * 2018/04/02
 * */
public enum BizInventoryEnum {
    /*用于指定一个采购商下面的订单数，根据下面的条件显示*/
    CLOUDCHAMBE(8);

    private Integer statu;

    BizInventoryEnum(Integer statu) {
        this.statu = statu;
    }
    
    
    public Integer getStatu() {
        return statu;
    }
    
    public void setStatu(Integer statu) {
        this.statu = statu;
    }
    
    public static BizInventoryEnum stateOf(Integer index) {
        for (BizInventoryEnum state : values()) {
            if (state.getStatu() == index) {
                return state;
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
    
//        System.out.println( BizInventoryEnum.BIZSTATUS.getStatu());
    }
    
}
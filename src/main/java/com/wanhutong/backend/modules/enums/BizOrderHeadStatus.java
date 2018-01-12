package com.wanhutong.backend.modules.enums;

/**
 * 用于计算采购商下面的订单数
 * Ouyang Xiutian
 * 2017/12/26
 * */
public enum BizOrderHeadStatus {
    /*用于指定一个采购商下面的订单数，根据下面的条件显示*/
    INVSTATUS(1), BIZSTATUS(30);
    
    private Integer statu;
    
    BizOrderHeadStatus(Integer statu) {
        this.statu = statu;
    }
    
    
    public Integer getStatu() {
        return statu;
    }
    
    public void setStatu(Integer statu) {
        this.statu = statu;
    }
    
    public static BizOrderHeadStatus stateOf(Integer index) {
        for (BizOrderHeadStatus state : values()) {
            if (state.getStatu() == index) {
                return state;
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
    
        System.out.println( BizOrderHeadStatus.BIZSTATUS.getStatu());
    }
    
}
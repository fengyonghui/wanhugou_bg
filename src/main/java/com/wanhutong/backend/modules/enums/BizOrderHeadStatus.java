package com.wanhutong.backend.modules.enums;

public enum BizOrderHeadStatus {
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
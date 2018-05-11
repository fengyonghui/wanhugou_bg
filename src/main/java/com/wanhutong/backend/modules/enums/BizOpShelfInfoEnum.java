package com.wanhutong.backend.modules.enums;

/**
 * 用于计算采购商下面的订单数
 * Ouyang Xiutian
 * 2017/12/26
 * */
public enum BizOpShelfInfoEnum {
//    /*用于商品上下架选择货架 显示采购中心添加*/
//    本地备货            货架
    LOCAL_STOCK(3), SPECIAL_OFFER(2),HOME_BANNER(1);

    private Integer local;

    BizOpShelfInfoEnum(Integer local) {
        this.local = local;
    }

    public Integer getLocal() {
        return local;
    }

    public void setLocal(Integer local) {
        this.local = local;
    }

    public static BizOpShelfInfoEnum stateOf(Integer local) {
        for (BizOpShelfInfoEnum state : values()) {
            if (state.getLocal() == local) {
                return state;
            }
        }
        return null;
    }
    
    public static void main(String[] args) {
    
        System.out.println( BizOpShelfInfoEnum.LOCAL_STOCK.getLocal());
    }
    
}
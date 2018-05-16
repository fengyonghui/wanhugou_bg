package com.wanhutong.backend.modules.enums;


public enum ProdTypeEnum {

    //普通产品
    PROD("1"),
    //采购商产品
    CUSTPROD("2");
    private String type;

    ProdTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPropValue(String type) {
        this.type = type;
    }

    public static ProdTypeEnum stateOf(String index) {
        for (ProdTypeEnum state : values()) {
            if (state.getType().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println( ProdTypeEnum.PROD.getType() );
        System.out.println( ProdTypeEnum.CUSTPROD.getType() );


    }
}

package com.wanhutong.backend.modules.enums;

public enum SkuTypeEnum {

    /**专营商品*/
    OWN_PRODUCT((byte)1,"自选商品"),
    MADE_PRODUCT((byte)2,"定制商品"),
    COMMON_PRODUCT((byte)3,"非自选商品");

    private int code;
    private String name;

    SkuTypeEnum(byte code, String name) {
        this.code=code;
        this.name=name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SkuTypeEnum stateOf(Integer index) {
        for (SkuTypeEnum state : values()) {
            if (state.getCode()==index) {
                return state;
            }
        }
        return null;
    }
    public static void main(String[] args) {

        SkuTypeEnum typeEnum=stateOf(1);
        System.out.println( typeEnum.getName() );


    }
}

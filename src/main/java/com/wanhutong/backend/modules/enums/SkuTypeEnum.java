package com.wanhutong.backend.modules.enums;

public enum SkuTypeEnum {

    /**主销品*/
    MAIN_SKU((byte)1,"主销品"),
    HOT_SKU((byte)2,"热销品"),
    END_SKU((byte)3,"尾销品");

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

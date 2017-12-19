package com.wanhutong.backend.modules.enums;

public enum ImgEnum{

    /**用户头像*/
    USER_PHOTO(0,"用户头像"),

    /**首页轮播图*/
    INDEX_BANNER_TYPE(1,"首页轮播图"),

    /**可访问入口类型*/
    OTHER_ACCESS_URL_TYPE(2,"可访问入口类型"),

    /**产品主图*/
    MAIN_PRODUCT(3,"产品主图"),

    /*产品详情图*/
    SUB_PRODUCT(4,"产品详情图");

    private int code;
    private String name;

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

    private ImgEnum(int code, String name){
        this.code = code;
        this.name = name;
        }
}

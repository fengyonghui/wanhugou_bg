package com.wanhutong.backend.modules.enums;

import javax.print.DocFlavor;

public enum ImgEnum{

    /**用户头像*/
    USER_PHOTO((byte)0,"用户头像"),

    /**首页轮播图*/
    INDEX_BANNER_TYPE((byte)1,"首页轮播图"),

    /**可访问入口类型*/
    OTHER_ACCESS_URL_TYPE((byte)2,"可访问入口类型"),
    /** 分类图片*/
    CATEGORY_TYPE((byte)3,"分类图片"),

    /**产品主图*/
    MAIN_PRODUCT_TYPE((byte)4,"产品主图"),

    /*产品详情图*/
    SUB_PRODUCT_TYPE((byte)5,"产品详情图"),
    /** 产品SKU图*/
    SKU_TYPE((byte)6,"产品SKU图");

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

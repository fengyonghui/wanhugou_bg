package com.wanhutong.backend.modules.enums;

import javax.print.DocFlavor;

public enum ImgEnum{

    /**用户头像*/
    USER_PHOTO((byte)0,"用户头像"),

    /**首页轮播图*/
    INDEX_BANNER_TYPE((byte)5,"首页轮播图"),

    /**可访问入口类型*/
    OTHER_ACCESS_URL_TYPE((byte)10,"可访问入口类型"),
    /** 分类图片*/
    CATEGORY_TYPE((byte)15,"分类图片"),
    /**产品列表图*/
    LIST_PRODUCT_TYPE((byte)16,"产品列表图"),
    /**产品主图*/
    MAIN_PRODUCT_TYPE((byte)20,"产品主图"),

    /*产品详情图*/
    SUB_PRODUCT_TYPE((byte)25,"产品详情图"),
    /** 产品SKU图*/
    SKU_TYPE((byte)30,"产品SKU图"),

    /**office图*/
    OFFICE_TYPE((byte)35,"office图"),

    /**物流信息图*/
    LOGISTICS_TYPE((byte)40,"物流信息图");

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

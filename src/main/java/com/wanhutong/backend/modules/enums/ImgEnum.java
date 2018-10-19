package com.wanhutong.backend.modules.enums;


public enum ImgEnum{

    /**用户头像*/
    USER_PHOTO((byte)1,"用户头像", "sys_user"),

    /**首页轮播图*/
    INDEX_BANNER_TYPE((byte)5,"首页轮播图", "biz_product_info"),

    /**可访问入口类型*/
    OTHER_ACCESS_URL_TYPE((byte)10,"可访问入口类型", ""),
    /** 分类图片*/
    CATEGORY_TYPE((byte)15,"分类图片", "biz_category_info"),
    /**产品列表图*/
    LIST_PRODUCT_TYPE((byte)16,"产品列表图", "biz_product_info"),
    /**产品主图*/
    MAIN_PRODUCT_TYPE((byte)20,"产品主图", "biz_product_info"),

    /*产品详情图*/
    SUB_PRODUCT_TYPE((byte)25,"产品详情图", "biz_product_info"),
    /** 产品SKU图*/
    SKU_TYPE((byte)30,"产品SKU图", "biz_sku_info"),

    /**office图*/
    OFFICE_TYPE((byte)35,"office图", "sys_office"),

    /**物流信息图*/
    LOGISTICS_TYPE((byte)40,"物流信息图", "biz_invoice"),

    /**供应商合同图*/
    VEND_COMPACT((byte)50,"供应商合同", "biz_vend_info"),
    /**供应商身份证*/
    VEND_IDENTITY_CARD((byte)51,"供应商身份证", "biz_vend_info"),
    /**线下支付凭证*/
    UNlINE_PAYMENT_VOUCHER((byte)27,"线下支付凭证","biz_order_header_unline"),
    /**线下退款凭证*/
    UNlINE_REFUND_VOUCHER((byte)31,"线下退款凭证","biz_order_header"),

    /**订单备注*/
    ORDER_REMARK((byte)29,"订单备注","biz_order_header"),
    /**
     * 采购单支付凭证
     */
    BIZ_PO_HEADER_PAY_OFFLINE((byte)61,"采购单支付凭证","biz_po_header_pay_offline"),
    /**
     * 零售代销订单佣金支付凭证
     */
    BIZ_COMMISSION_HEADER_PAY_OFFLINE((byte)62,"采购单支付凭证","biz_commission_header_pay_offline"),
    /**
     * 拍照下单的商品信息图
     */
    ORDER_SKU_PHOTO((byte)28,"订单商品信息图","biz_order_header"),

    /**
     * 产品主页视频
     */
    PRODUCT_MIAN_VIDEO((byte) 71, "产品主页视频", "biz_product_info"),
    /**
     * 产品详情视频
     */
    PRODUCT_DETAIL_VIDEO((byte) 72, "产品详情视频", "biz_product_info"),
    /**
     * 73
     */
    VENDOR_VIDEO((byte) 73,"供应商视频","sys_office"),
    ;

    private int code;
    private String name;
    private String tableName;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setName(String name) {
        this.name = name;
    }

    ImgEnum(int code, String name, String tableName){
        this.code = code;
        this.name = name;
        this.tableName = tableName;
    }
}

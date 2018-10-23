package com.wanhutong.backend.modules.enums;

/**
 * Created by CK on 2016/7/14.
 */
public enum OfficeTypeEnum {

    OFFICE("0") ,
    COMPANY("1"),
    DEPARTMENT("2"),
    GROUP("3"),
    OTHER("4"),
    WANHUTONG("5"),
    /**
     * 采购商
     */
    CUSTOMER("6"),
    VENDOR("7"),
    PURCHASINGCENTER("8"),
    SUPPLYCENTER("9"),
    WITHCAPITAL("10"),
    /**
     * 网供
     */
    NETWORKSUPPLY("11"),
    /**
     * 网供
     */
    NETWORK("13"),
    /**
     * 零售商
     */
    SHOPKEEPER("15"),
    /**
     * 代销商
     */
    COMMISSION_MERCHANT("16")

    ;

   private String type;

    OfficeTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static OfficeTypeEnum stateOf(String index) {
        for (OfficeTypeEnum state : values()) {
            if (state.getType().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {


        System.out.println( OfficeTypeEnum.VENDOR.ordinal() );


    }
}

package com.wanhutong.backend.modules.enums;

/**
 * Created by CK on 2016/7/14.
 */
public enum OfficeTypeEnum {

   OFFICE ,COMPANY,DEPARTMENT, GROUP,OTHER,WANHUTONG,VENDOR;

    public static void main(String[] args) {


        System.out.println( OfficeTypeEnum.VENDOR.ordinal() );


    }
}

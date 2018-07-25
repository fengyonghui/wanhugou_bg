package com.wanhutong.backend.modules.enums;

/**
 * 采购单排产状态
 */
public enum BizOrderSchedulingEnum {


    //   1: 当前状态下无需排产 ; 2:未排产 3:排产中 4:排产完成
    UNABLE_SCHEDULING("无需排产"),
    SCHEDULING_NOT("未排产"),
    SCHEDULING_PLAN("排产中"),
    SCHEDULING_DONE("排产完成");

    private String desc;

    BizOrderSchedulingEnum(String desc) {
        this.desc=desc;
    }

    public String getDesc() {
        return desc;
    }
}

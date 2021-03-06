package com.wanhutong.backend.modules.enums;

/**
 * 采购单排产状态
 */
public enum BizOrderSchedulingEnum {


    //   1: 当前状态下无需排产 ; 2:未排产 3:排产中 4:排产完成
    UNABLE_SCHEDULING(-1, "无需排产"),
    SCHEDULING_NOT(0, "未排产"),
    SCHEDULING_PLAN(1, "排产中"),
    SCHEDULING_DONE(2, "排产完成");

    private int type;
    private String desc;

    BizOrderSchedulingEnum(int type, String desc) {
        this.type=type;
        this.desc=desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}

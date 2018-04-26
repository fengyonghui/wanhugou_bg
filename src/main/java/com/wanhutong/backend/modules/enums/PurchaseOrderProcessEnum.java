package com.wanhutong.backend.modules.enums;


import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 采购单审核流程状态
 * @author Ma.Qiang
 */
public enum  PurchaseOrderProcessEnum {

    /**
     * 采购中心负责人审批
     */
    PURCHASE_CENTER (1, RoleEnNameEnum.P_CENTER_MANAGER, 2),
    /**
     * 供货部
     */
    PROVIDER_CENTER (2, RoleEnNameEnum.PROVIDER_MANAGER, 3),
    /**
     * 会计
     */
    FINANCE (3, RoleEnNameEnum.FINANCE, 4),
    /**
     * 运营总监
     */
    OPERATION (4, RoleEnNameEnum.OP_DIRECTOR, 5),
    /**
     * 总经理
     */
    GENERAL_MANAGER (5, RoleEnNameEnum.GENERAL_MANAGER, 6),
    /**
     * 财会待付款
     */
    PAYMENT (6, RoleEnNameEnum.FINANCE, 7),
    /**
       * 支付完成
       */
    FINISH (7, null, 0),
    ;

    /**
     * 数据MAP
     */
    public static final Map<Integer, PurchaseOrderProcessEnum> PROCESS_ENUM_MAP = getEnumMap();


    /**
     * 状态码
     */
    private int code;

    /**
     * 处理角色
     */
    private RoleEnNameEnum roleEnNameEnum;


    /**
     * 待处理状态
     */
    private int nextCode;


    PurchaseOrderProcessEnum(int code, RoleEnNameEnum roleEnNameEnum, int nextCode) {
        this.code = code;
        this.roleEnNameEnum = roleEnNameEnum;
        this.nextCode = nextCode;
    }

    public int getCode() {
        return code;
    }

    public RoleEnNameEnum getRoleEnNameEnum() {
        return roleEnNameEnum;
    }

    public int getNextCode() {
        return nextCode;
    }


    private static Map<Integer, PurchaseOrderProcessEnum> getEnumMap() {
        Map<Integer, PurchaseOrderProcessEnum> result = Maps.newHashMap();
        for (PurchaseOrderProcessEnum e : values()) {
            result.putIfAbsent(e.getCode(), e);
        }
        return result;
    }

    public static Map<Integer, PurchaseOrderProcessEnum> getProcessEnumMap() {
        return PROCESS_ENUM_MAP;
    }
}


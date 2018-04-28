package com.wanhutong.backend.modules.enums;


import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 采购单审核流程状态
 *
 * @author Ma.Qiang
 */
public enum PurchaseOrderProcessEnum {

    /**
     * 采购中心负责人审批
     */
    PURCHASE_CENTER(1, RoleEnNameEnum.P_CENTER_MANAGER, 2, -1),
    /**
     * 供货部
     */
    PROVIDER_CENTER(2, RoleEnNameEnum.PROVIDER_MANAGER, 3, -1),
    /**
     * 会计
     */
    FINANCE(3, RoleEnNameEnum.FINANCE, 4, -1),
    /**
     * 运营总监
     */
    OPERATION(4, RoleEnNameEnum.OP_DIRECTOR, 6, -1),
    /**
     * 总经理
     */
    GENERAL_MANAGER(5, RoleEnNameEnum.GENERAL_MANAGER, 6, -1),
    /**
     * 财会待付款
     */
    PAYMENT(6, RoleEnNameEnum.FINANCE, 7, -1),
    /**
     * 支付完成
     */
    FINISH(7, null, 0, 0),

    /**
     * 终止
     */
    TERMINATION(-1, null, 0, 0),;

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
     * 通过之后的状态
     */
    private int passCode;

    /**
     * 拒绝之后的状态
     */
    private int rejectCode;


    PurchaseOrderProcessEnum(int code, RoleEnNameEnum roleEnNameEnum, int passCode, int rejectCode) {
        this.code = code;
        this.roleEnNameEnum = roleEnNameEnum;
        this.passCode = passCode;
        this.rejectCode = rejectCode;
    }

    public int getCode() {
        return code;
    }

    public RoleEnNameEnum getRoleEnNameEnum() {
        return roleEnNameEnum;
    }

    public int getPassCode() {
        return passCode;
    }

    public int getRejectCode() {
        return rejectCode;
    }

    /**
     * 取封装好的数据MAP
     *
     * @return 封装好的数据MAP
     */
    private static Map<Integer, PurchaseOrderProcessEnum> getEnumMap() {
        Map<Integer, PurchaseOrderProcessEnum> result = Maps.newHashMap();
        for (PurchaseOrderProcessEnum e : values()) {
            result.putIfAbsent(e.getCode(), e);
        }
        return result;
    }

    /**
     * 取当前状态通过后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public static PurchaseOrderProcessEnum getPassEnum(PurchaseOrderProcessEnum currentEnum) {
        return PROCESS_ENUM_MAP.get(currentEnum.getPassCode());
    }

    /**
     * 取当前状态拒绝后的状态
     *
     * @param currentEnum 当前状态
     * @return 通过后的状态
     */
    public static PurchaseOrderProcessEnum getRejectEnum(PurchaseOrderProcessEnum currentEnum) {
        return PROCESS_ENUM_MAP.get(currentEnum.getRejectCode());
    }
}


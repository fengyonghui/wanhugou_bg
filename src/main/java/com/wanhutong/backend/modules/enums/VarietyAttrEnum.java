package com.wanhutong.backend.modules.enums;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public enum VarietyAttrEnum {


    MATERIAL(1, "材质"),
    SIZE(2, "尺寸"),
    COLOR(3, "颜色"),
    BRAND(7, "品牌");

    private Integer id;
    private String name;

    VarietyAttrEnum(int id, String name) {
        this.id=id;
        this.name=name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static final List<Integer> NOT_VARIETY_ATTR= Lists.newArrayList(
            MATERIAL.getId(),SIZE.getId(),COLOR.getId(),BRAND.getId()
    );

}

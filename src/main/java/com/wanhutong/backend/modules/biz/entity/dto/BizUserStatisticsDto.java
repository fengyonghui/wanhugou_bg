package com.wanhutong.backend.modules.biz.entity.dto;


/**
 * 用户统计数据Dto
 * @author Ma.Qiang
 */
public class BizUserStatisticsDto {


    /**
     * 名称
     */
    private String name;
    /**
     * 数量
     */
    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

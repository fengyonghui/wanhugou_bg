package com.wanhutong.backend.modules.biz.entity.dto;


/**
 * EchartsYAxisDto 图表Y轴数据
 *
 * @author Ma.Qiang
 */
public class EchartsYAxisDto {


//    type: 'value',
//    scale: true,
//    name: '销量',
//    max: 150,
//    min: 0,



    private String type;
    private boolean scale = true;
    private String name;
    private Integer max;
    private Integer min;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isScale() {
        return scale;
    }

    public void setScale(boolean scale) {
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }
}

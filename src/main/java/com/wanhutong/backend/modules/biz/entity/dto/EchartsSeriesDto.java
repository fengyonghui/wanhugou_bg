package com.wanhutong.backend.modules.biz.entity.dto;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * EchartsSeriesDto 图表数据
 *
 * @author Ma.Qiang
 */
public class EchartsSeriesDto {

    /**
     * 默认的图表类型
     */
    private static final SeriesTypeEnum DEFAULT_TYPE = SeriesTypeEnum.BAR;

    /**
     * 使用的纵坐标角标
     */
    private Integer  yAxisIndex = 0;

    /**
     * 显示名称
     */
    private String name;

    /**
     * 图表类型
     */
    private String type = DEFAULT_TYPE.getCode();

    /**
     * 元素颜色, 可使用色号或英文
     */
    private String color;

    /**
     * 柱图最大宽度
     */
    private Integer barMaxWidth = 30;

    /**
     * 数据集合
     */
    private List<Object> data = Lists.newArrayList();

    private ItemStyle itemStyle;

    public Integer getBarMaxWidth() {
        return barMaxWidth;
    }

    public void setBarMaxWidth(Integer barMaxWidth) {
        this.barMaxWidth = barMaxWidth;
    }

    public Integer getyAxisIndex() {
        return yAxisIndex;
    }

    public void setyAxisIndex(Integer yAxisIndex) {
        this.yAxisIndex = yAxisIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public ItemStyle getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(ItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    /**
     * 图表类型枚举
     */
    public enum  SeriesTypeEnum{
        BAR("bar","柱状图"),
        LINE("line","折线图"),
        ;

        SeriesTypeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public static class ItemStyle {
        private Normal normal;

        public Normal getNormal() {
            return normal;
        }

        public void setNormal(Normal normal) {
            this.normal = normal;
        }
    }

    public static class Normal {
        private Label label;

        public Label getLabel() {
            return label;
        }

        public void setLabel(Label label) {
            this.label = label;
        }
    }

    public static class Label {
        /**
         * 是否展示
         */
        private boolean show = Boolean.FALSE;
        private String position = "top";

        private String textStyle;


        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getTextStyle() {
            return textStyle;
        }

        public void setTextStyle(String textStyle) {
            this.textStyle = textStyle;
        }

        public boolean isShow() {
            return show;
        }

        public void setShow(boolean show) {
            this.show = show;
        }
    }
}

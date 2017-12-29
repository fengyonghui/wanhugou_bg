package com.wanhutong.backend.modules.enums;


public enum DefaultPropEnum {
    //brand 品牌属性
    PROPBRAND("42");
    private String propValue;

    DefaultPropEnum(String propValue) {
        this.propValue = propValue;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public static DefaultPropEnum stateOf(String index) {
        for (DefaultPropEnum state : values()) {
            if (state.getPropValue().equals(index)) {
                return state;
            }
        }
        return null;
    }

    public static void main(String[] args) {

        System.out.println( DefaultPropEnum.PROPBRAND.getPropValue() );


    }
}

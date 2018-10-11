package com.wanhutong.backend.modules.biz.entity.logistic;

import java.util.List;

/**
 * 采购中心region信息Entity
 * @author wangby
 * @version 2018-09-26
 */
public class AddressVoEntity {

    private Integer id;
    private String receiver;
    private String phone;
    private String address;
    private String defaultStatus;
    private String regionCode;
    private String provCode;
    private String cityCode;

    private List<VarietyInfoIdSParams> VarietyInfoIdSParams;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDefaultStatus() {
        return defaultStatus;
    }

    public void setDefaultStatus(String defaultStatus) {
        this.defaultStatus = defaultStatus;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public List<com.wanhutong.backend.modules.biz.entity.logistic.VarietyInfoIdSParams> getVarietyInfoIdSParams() {
        return VarietyInfoIdSParams;
    }

    public void setVarietyInfoIdSParams(List<com.wanhutong.backend.modules.biz.entity.logistic.VarietyInfoIdSParams> varietyInfoIdSParams) {
        VarietyInfoIdSParams = varietyInfoIdSParams;
    }
}

package com.wanhutong.backend.modules.biz.entity.dto;


import java.util.Date;

/**
 * 客户专员关联经销店Dto
 *
 * @author wangby
 */
public class BizCustomCenterConsultantDto {

    private Integer customsId;
    private String customsName;
    private String customsPrimaryPersonName;
    private String centersName;
    private Integer consultantsId;
    private String consultantsName;
    private String consultantsMobile;
    private Date userOfficeDeta;
    private Double userOfficeReceiveTotal;
    private String provinceName;
    private String cityName;
    private String regionName;
    private String address;
    private Integer orderCount;



    public Integer getCustomsId() {
        return customsId;
    }

    public void setCustomsId(Integer customsId) {
        this.customsId = customsId;
    }

    public String getCentersName() {
        return centersName;
    }

    public void setCentersName(String centersName) {
        this.centersName = centersName;
    }

    public String getConsultantsName() {
        return consultantsName;
    }

    public void setConsultantsName(String consultantsName) {
        this.consultantsName = consultantsName;
    }

    public String getConsultantsMobile() {
        return consultantsMobile;
    }

    public void setConsultantsMobile(String consultantsMobile) {
        this.consultantsMobile = consultantsMobile;
    }

    public String getCustomsName() {
        return customsName;
    }

    public void setCustomsName(String customsName) {
        this.customsName = customsName;
    }

    public String getCustomsPrimaryPersonName() {
        return customsPrimaryPersonName;
    }

    public void setCustomsPrimaryPersonName(String customsPrimaryPersonName) {
        this.customsPrimaryPersonName = customsPrimaryPersonName;
    }

    public Double getUserOfficeReceiveTotal() {
        return userOfficeReceiveTotal;
    }

    public void setUserOfficeReceiveTotal(Double userOfficeReceiveTotal) {
        this.userOfficeReceiveTotal = userOfficeReceiveTotal;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public Date getUserOfficeDeta() {
        return userOfficeDeta;
    }

    public void setUserOfficeDeta(Date userOfficeDeta) {
        this.userOfficeDeta = userOfficeDeta;
    }

    public Integer getConsultantsId() {
        return consultantsId;
    }

    public void setConsultantsId(Integer consultantsId) {
        this.consultantsId = consultantsId;
    }
}

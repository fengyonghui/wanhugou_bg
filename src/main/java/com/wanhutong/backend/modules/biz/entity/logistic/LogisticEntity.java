package com.wanhutong.backend.modules.biz.entity.logistic;

import java.math.BigDecimal;
import java.util.Date;

public class LogisticEntity {

    /**
     * 返回状态
     */
    private boolean rlt;
    /**
     * 信息
     */
    private String message;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回数据
     */
    private Data data;

    static class Data {

        /**
         *物流状态
         */
        private Integer logisticStatus;
        /**
         * 快递公司编码
         */
        private String shipperCode;
        /**
         *物流费用
         */
        private BigDecimal logisticFee;
        /**
         *收件人
         */
        private LogisticUser receiver;
        /**
         * 发件人
         */
        private LogisticUser sender;
        /**
         * 物流信息
         */
        private LogisticInfo logisticInfo;
        /**
         * 备注
         */
        private String remark;

        Data() {
            super();
        }

        public Integer getLogisticStatus() {
            return logisticStatus;
        }

        public void setLogisticStatus(Integer logisticStatus) {
            this.logisticStatus = logisticStatus;
        }

        public String getShipperCode() {
            return shipperCode;
        }

        public void setShipperCode(String shipperCode) {
            this.shipperCode = shipperCode;
        }

        public BigDecimal getLogisticFee() {
            return logisticFee;
        }

        public void setLogisticFee(BigDecimal logisticFee) {
            this.logisticFee = logisticFee;
        }

        public LogisticUser getReceiver() {
            return receiver;
        }

        public void setReceiver(LogisticUser receiver) {
            this.receiver = receiver;
        }

        public LogisticUser getSender() {
            return sender;
        }

        public void setSender(LogisticUser sender) {
            this.sender = sender;
        }

        public LogisticInfo getLogisticInfo() {
            return logisticInfo;
        }

        public void setLogisticInfo(LogisticInfo logisticInfo) {
            this.logisticInfo = logisticInfo;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    static class LogisticUser {
        /**
         * 姓名
         */
        private String name;
        /**
         * 电话
         */
        private String tel;
        /**
         * 省
         */
        private String provinceName;
        /**
         * 市
         */
        private String cityName;
        /**
         * 区
         */
        private String expAreaName;
        /**
         * 详细地址
         */
        private String address;

        LogisticUser() {
            super();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
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

        public String getExpAreaName() {
            return expAreaName;
        }

        public void setExpAreaName(String expAreaName) {
            this.expAreaName = expAreaName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    static class LogisticInfo {
        /**
         * 物流时间
         */
        private Date logisticTime;
        /**
         * 物流商信息
         */
        private LogisticDetail logisticDetail;

        LogisticInfo() {
            super();
        }

        public Date getLogisticTime() {
            return logisticTime;
        }

        public void setLogisticTime(Date logisticTime) {
            this.logisticTime = logisticTime;
        }

        public LogisticDetail getLogisticDetail() {
            return logisticDetail;
        }

        public void setLogisticDetail(LogisticDetail logisticDetail) {
            this.logisticDetail = logisticDetail;
        }
    }

    static class LogisticDetail {
        /**
         *物流联系人姓名
         */
        private String logisticName;
        /**
         * 物流名称
         */
        private String logisticCompanyName;
        /**
         * 联系人电话
         */
        private String mobile;

        LogisticDetail() {
            super();
        }

        public String getLogisticName() {
            return logisticName;
        }

        public void setLogisticName(String logisticName) {
            this.logisticName = logisticName;
        }

        public String getLogisticCompanyName() {
            return logisticCompanyName;
        }

        public void setLogisticCompanyName(String logisticCompanyName) {
            this.logisticCompanyName = logisticCompanyName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
    }

    public boolean getRlt() {
        return rlt;
    }

    public void setRlt(boolean rlt) {
        this.rlt = rlt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    LogisticEntity() {
        super();
    }
}





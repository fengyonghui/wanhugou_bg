package com.wanhutong.backend.modules.biz.entity.dto;


public class OfficeLevelApplyDto {


    /**
     * officeId
     */
    private Integer officeId;
    /**
     * 申请类型 经销商:6 代销商:16
     */
    private Integer applyLevel;

    /**
     * 收款人姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCardNumber;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 开户银行
     */

    private String depositBank;

    /**
     * 签名
     */
    private String sign;


    public Integer getApplyLevel() {
        return applyLevel;
    }

    public void setApplyLevel(Integer applyLevel) {
        this.applyLevel = applyLevel;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getDepositBank() {
        return depositBank;
    }

    public void setDepositBank(String depositBank) {
        this.depositBank = depositBank;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    // 封装待签名数据
    public String buildData() {
        return "officeId=" + officeId +
                "applyLevel=" + applyLevel +
                "realName=" + realName +
                "idCardNumber=" + idCardNumber +
                "bankCardNumber=" + bankCardNumber +
                "depositBank=" + depositBank;
    }
}

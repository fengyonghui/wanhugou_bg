package com.wanhutong.backend.modules.biz.entity.custom;


import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 机构信息entityEntity
 * @author wangby
 * @version 2018-10-22
 */
public class BizCustomerInfo extends DataEntity<BizCustomerInfo> {

    private static final long serialVersionUID = 100001000010000L;
    private Integer officeId;		// 机构id
    private String bankName;		// 开户行
    private String cardNumber;		// 银行卡号
    private String payee;		// 收款人
    private String remark;		// 备注
    private String idCardNumber;		// 身份证号

    public BizCustomerInfo() {
        super();
    }

    public BizCustomerInfo(Integer id){
        super(id);
    }

    public Integer getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Integer officeId) {
        this.officeId = officeId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
}
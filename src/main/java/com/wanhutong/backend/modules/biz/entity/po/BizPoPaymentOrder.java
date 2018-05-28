/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购付款单Entity
 *
 * @author Ma.Qiang
 * @version 2018-05-04
 */
public class BizPoPaymentOrder extends DataEntity<BizPoPaymentOrder> {

    private static final long serialVersionUID = 1111111111111L;
    private Integer poHeaderId;        // 采购单ID
    private BigDecimal total;        // 申请金额
    private BigDecimal payTotal;        // 付款金额
    private Integer bizStatus;        // 当前状态
    private String img;        // 图片

    private Date deadline;

    private Integer processId;

    private CommonProcessEntity commonProcess;


    public BizPoPaymentOrder() {
        super();
    }

    public BizPoPaymentOrder(Integer id) {
        super(id);
    }

    @Length(min = 1, max = 11, message = "采购单ID长度必须介于 1 和 11 之间")
    public Integer getPoHeaderId() {
        return poHeaderId;
    }

    public void setPoHeaderId(Integer poHeaderId) {
        this.poHeaderId = poHeaderId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getBizStatus() {
        return bizStatus;
    }

    public void setBizStatus(Integer bizStatus) {
        this.bizStatus = bizStatus;
    }

    public BigDecimal getPayTotal() {
        return payTotal;
    }

    public void setPayTotal(BigDecimal payTotal) {
        this.payTotal = payTotal;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public CommonProcessEntity getCommonProcess() {
        return commonProcess;
    }

    public void setCommonProcess(CommonProcessEntity commonProcess) {
        this.commonProcess = commonProcess;
    }

    public enum BizStatus {
        NO_PAY(0, "未支付"),
        ALL_PAY(1, "已支付"),
        ;
        private int status;
        private String desc;

        BizStatus(int status, String desc) {
            this.status = status;
            this.desc = desc;
        }

        public int getStatus() {
            return status;
        }

        public String getDesc() {
            return desc;
        }
    }
}
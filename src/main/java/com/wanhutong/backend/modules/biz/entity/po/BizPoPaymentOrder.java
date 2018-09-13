/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;

import com.wanhutong.backend.common.persistence.DataEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    /**
     * 支付单类型
     */
    private Integer orderType;

    private List<CommonImg> imgList;

    private Date deadline;

    private Integer processId;

    private CommonProcessEntity commonProcess;

    private Date payTime;

    /**
     * 备注
     */
    private String remark;


    public BizPoPaymentOrder() {
        super();
    }

    public BizPoPaymentOrder(Integer id) {
        super(id);
    }

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

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public List<CommonImg> getImgList() {
        return imgList;
    }

    public void setImgList(List<CommonImg> imgList) {
        this.imgList = imgList;
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

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
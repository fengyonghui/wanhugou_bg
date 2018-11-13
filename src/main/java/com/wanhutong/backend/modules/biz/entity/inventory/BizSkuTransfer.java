/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import java.util.Date;
import java.util.List;

/**
 * 库存调拨Entity
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
public class BizSkuTransfer extends DataEntity<BizSkuTransfer> {
	
	private static final long serialVersionUID = 1L;

	public static final String DATABASE_TABLE_NAME = "biz_sku_transfer";

	/**
	 * 调拨单号
	 */
	private String transferNo;

	/**
	 * biz_inventor_info.id 原仓库id
	 */
	private BizInventoryInfo fromInv;

	/**
	 * biz_inventor_info.id 目标仓库id
	 */
	private BizInventoryInfo toInv;

	/**
	 * sys_user.id 申请人id
	 */
	private User applyer;

	/**
	 * 0: 未审核；10:审核完成; 20:出库中; 30:已出库; 40:入库中；50:已入库 60:取消
	 */
	private Byte bizStatus;

	/**
	 * 期望收货时间
	 */
	private Date recvEta;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 区分出库和入库（source == 'out' or source == 'in'）
	 */
	private String source;

	/**
	 * 页面传值，商品ID
	 */
	private String skuIds;

	/**
	 * 页面传值，调拨数量
	 */
	private String transferNums;

	/**
	 * 区分详情("detail"),修改(""),审核("audit")
	 */
	private String str;

    /**
     * 审核
     */
	private CommonProcessEntity commonProcess;

    /**
     * 审核流程
     */
	private List<CommonProcessEntity> commonProcessList;

	public BizSkuTransfer() {
		super();
	}

	public BizSkuTransfer(Integer id){
		super(id);
	}

	public String getTransferNo() {
		return transferNo;
	}

	public void setTransferNo(String transferNo) {
		this.transferNo = transferNo;
	}

	public BizInventoryInfo getFromInv() {
		return fromInv;
	}

	public void setFromInv(BizInventoryInfo fromInv) {
		this.fromInv = fromInv;
	}

	public BizInventoryInfo getToInv() {
		return toInv;
	}

	public void setToInv(BizInventoryInfo toInv) {
		this.toInv = toInv;
	}

	public User getApplyer() {
		return applyer;
	}

	public void setApplyer(User applyer) {
		this.applyer = applyer;
	}
	
	public Byte getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Byte bizStatus) {
		this.bizStatus = bizStatus;
	}
	
	public Date getRecvEta() {
		return recvEta;
	}

	public void setRecvEta(Date recvEta) {
		this.recvEta = recvEta;
	}
	
	@Length(min=0, max=200, message="备注长度必须介于 0 和 200 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSkuIds() {
		return skuIds;
	}

	public void setSkuIds(String skuIds) {
		this.skuIds = skuIds;
	}

	public String getTransferNums() {
		return transferNums;
	}

	public void setTransferNums(String transferNums) {
		this.transferNums = transferNums;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

    public CommonProcessEntity getCommonProcess() {
        return commonProcess;
    }

    public void setCommonProcess(CommonProcessEntity commonProcess) {
        this.commonProcess = commonProcess;
    }

    public List<CommonProcessEntity> getCommonProcessList() {
        return commonProcessList;
    }

    public void setCommonProcessList(List<CommonProcessEntity> commonProcessList) {
        this.commonProcessList = commonProcessList;
    }
}
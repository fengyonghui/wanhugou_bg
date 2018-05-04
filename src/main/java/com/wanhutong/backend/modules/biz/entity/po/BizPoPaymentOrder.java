/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购付款单Entity
 * @author Ma.Qiang
 * @version 2018-05-04
 */
public class BizPoPaymentOrder extends DataEntity<BizPoPaymentOrder> {
	
	private static final long serialVersionUID = 1L;
	private String poHeaderId;		// 采购单ID
	private String total;		// 付款金额
	private String processId;		// 当前审核状态ID

	private CommonProcessEntity commonProcess;

	public BizPoPaymentOrder() {
		super();
	}

	public BizPoPaymentOrder(Integer id){
		super(id);
	}

	@Length(min=1, max=11, message="采购单ID长度必须介于 1 和 11 之间")
	public String getPoHeaderId() {
		return poHeaderId;
	}

	public void setPoHeaderId(String poHeaderId) {
		this.poHeaderId = poHeaderId;
	}
	
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	@Length(min=1, max=11, message="当前审核状态ID长度必须介于 1 和 11 之间")
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public CommonProcessEntity getCommonProcess() {
		return commonProcess;
	}

	public void setCommonProcess(CommonProcessEntity commonProcess) {
		this.commonProcess = commonProcess;
	}
}
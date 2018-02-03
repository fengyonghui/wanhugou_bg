/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import org.hibernate.validator.constraints.Length;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 销售采购备货中间表Entity
 * @author 张腾飞
 * @version 2018-01-09
 */
public class BizPoOrderReq extends DataEntity<BizPoOrderReq> {

	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// 销售订单
	private BizRequestHeader requestHeader;		// 备货单
	private BizPoHeader poHeader;		// 采购单
	private Integer poLineNo;
	private Integer soLineNo;
	private Integer soQty;
	private Byte soType;

	private Integer soId;

	
	public BizPoOrderReq() {
		super();
	}

	public BizPoOrderReq(Integer id){
		super(id);
	}

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}

	public BizPoHeader getPoHeader() {
		return poHeader;
	}

	public void setPoHeader(BizPoHeader poHeader) {
		this.poHeader = poHeader;
	}

	public Byte getSoType() {
		return soType;
	}

	public void setSoType(Byte soType) {
		this.soType = soType;
	}

	public Integer getPoLineNo() {
		return poLineNo;
	}

	public void setPoLineNo(Integer poLineNo) {
		this.poLineNo = poLineNo;
	}

	public Integer getSoLineNo() {
		return soLineNo;
	}

	public void setSoLineNo(Integer soLineNo) {
		this.soLineNo = soLineNo;
	}

	public Integer getSoQty() {
		return soQty;
	}

	public void setSoQty(Integer soQty) {
		this.soQty = soQty;
	}

	public Integer getSoId() {
		return soId;
	}

	public void setSoId(Integer soId) {
		this.soId = soId;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;
import org.omg.PortableInterceptor.INACTIVE;

/**
 * 备货清单详细信息Entity
 * @author liuying
 * @version 2017-12-23
 */
public class BizRequestDetail extends DataEntity<BizRequestDetail> {
	
	private static final long serialVersionUID = 1L;
	private BizRequestHeader requestHeader;		// biz_request_header.id
	private String lineNo;		// 行号
	private BizSkuInfo skuInfo;		// biz_sku_info.id
	private Integer reqQty;		// 请求数量
	private Integer recvQty;		// 收货数量
	private String remark;		// 备注

	
	public BizRequestDetail() {
		super();
	}

	public BizRequestDetail(Integer id){
		super(id);
	}


	
	@Length(min=1, max=11, message="行号长度必须介于 1 和 11 之间")
	public String getLineNo() {
		return lineNo;
	}

	public void setLineNo(String lineNo) {
		this.lineNo = lineNo;
	}

	
	@Length(min=0, max=200, message="备注长度必须介于 0 和 200 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BizRequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(BizRequestHeader requestHeader) {
		this.requestHeader = requestHeader;
	}

	public BizSkuInfo getSkuInfo() {
		return skuInfo;
	}

	public void setSkuInfo(BizSkuInfo skuInfo) {
		this.skuInfo = skuInfo;
	}

	public Integer getReqQty() {
		return reqQty;
	}

	public void setReqQty(Integer reqQty) {
		this.reqQty = reqQty;
	}

	public Integer getRecvQty() {
		return recvQty;
	}

	public void setRecvQty(Integer recvQty) {
		this.recvQty = recvQty;
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
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
	private Integer lineNo;		// 行号
	private BizSkuInfo skuInfo;		// biz_sku_info.id
	private Double unitPrice;      //单价
	private Integer reqQty;		// 请求数量
	private Integer recvQty;		// 收货数量
	private Integer sendQty;		//已发货数量
	private String remark;		// 备注

	private String  reqDetailIds;
	private Integer totalReqQty;
	private Integer totalSendQty;
	private Integer totalRecvQty;
	private Integer vendorId;
	private String vendorName;
	private Integer sendNum;		//记录页面传的供货数量

	/**
	 * 备货清单查看 已生成的采购单
	 * */
	private BizPoDetail poDetail;


	public BizRequestDetail() {
		super();
	}

	public BizRequestDetail(Integer id){
		super(id);
	}


	public Integer getLineNo() {
		return lineNo;
	}

	public void setLineNo(Integer lineNo) {
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

	public Integer getSendQty() {
		return sendQty;
	}

	public void setSendQty(Integer sendQty) {
		this.sendQty = sendQty;
	}

	public Integer getRecvQty() {
		return recvQty;
	}

	public void setRecvQty(Integer recvQty) {
		this.recvQty = recvQty;
	}

	public String getReqDetailIds() {
		return reqDetailIds;
	}

	public void setReqDetailIds(String reqDetailIds) {
		this.reqDetailIds = reqDetailIds;
	}

	public Integer getTotalReqQty() {
		return totalReqQty;
	}

	public void setTotalReqQty(Integer totalReqQty) {
		this.totalReqQty = totalReqQty;
	}

	public Integer getTotalSendQty() {
		return totalSendQty;
	}

	public void setTotalSendQty(Integer totalSendQty) {
		this.totalSendQty = totalSendQty;
	}

	public Integer getTotalRecvQty() {
		return totalRecvQty;
	}

	public void setTotalRecvQty(Integer totalRecvQty) {
		this.totalRecvQty = totalRecvQty;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BizPoDetail getPoDetail() {
		return poDetail;
	}

	public void setPoDetail(BizPoDetail poDetail) {
		this.poDetail = poDetail;
	}
}
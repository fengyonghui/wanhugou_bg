/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 备货清单Entity
 * @author liuying
 * @version 2017-12-23
 */
public class BizRequestHeader extends DataEntity<BizRequestHeader> {
	
	private static final long serialVersionUID = 1L;
	private String reqNo;		// 需求单号-备货单号
	private Byte reqType;		// 1备消品备货 2非备消品备货-来自订单
	private Office fromOffice;		// 需求单位：当前用户所在的采购中心名称
	private Office toOffice;		// 接受单位：备货中心-目前只有一个
	private Date recvEta;		// 期望收货时间
	private String remark;		// 备注
	private Byte bizStatus;		// 业务状态：0未审核 1审核通过 2备货中 3 完成 4关闭
	private String skuIds;
	private List<BizSkuInfo> skuInfoList = Lists.newArrayList();
	private BizProductInfo productInfo;

	private List<BizRequestDetail> requestDetailList;

	private String startDate;

	private String endDate;
	
	public BizRequestHeader() {
		super();
	}

	public BizRequestHeader(Integer id){
		super(id);
	}

	@Length(min=1, max=20, message="需求单号-备货单号长度必须介于 1 和 20 之间")
	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="期望收货时间不能为空")
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

	public Byte getReqType() {
		return reqType;
	}

	public void setReqType(Byte reqType) {
		this.reqType = reqType;
	}

	public Office getFromOffice() {
		return fromOffice;
	}

	public void setFromOffice(Office fromOffice) {
		this.fromOffice = fromOffice;
	}

	public Office getToOffice() {
		return toOffice;
	}

	public void setToOffice(Office toOffice) {
		this.toOffice = toOffice;
	}

	public Byte getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Byte bizStatus) {
		this.bizStatus = bizStatus;
	}

	public String getSkuIds() {
		skuIds= StringUtils.join(getSkuIdList(), ",");
		return skuIds;
	}
	public List<Integer> getSkuIdList() {
		List<Integer> skuIdList = Lists.newArrayList();
		for (BizSkuInfo skuInfo : skuInfoList) {
			skuIdList.add(skuInfo.getId());
		}
		return skuIdList;
	}

	public List<BizRequestDetail> getRequestDetailList() {
		return requestDetailList;
	}

	public void setRequestDetailList(List<BizRequestDetail> requestDetailList) {
		this.requestDetailList = requestDetailList;
	}

	public void setSkuIds(String skuIds) {
		skuInfoList = Lists.newArrayList();
		if (skuIds != null){
			String[] ids = StringUtils.split(skuIds, ",");
			setSkuIdList(Lists.newArrayList(ids));
		}
		this.skuIds = skuIds;
	}
	public void setSkuIdList(List<String> skuIdList) {
		skuInfoList = Lists.newArrayList();
		for (String skuId : skuIdList) {
			BizSkuInfo skuInfo = new BizSkuInfo();
			skuInfo.setId(Integer.valueOf(skuId));
			skuInfoList.add(skuInfo);
		}
	}

	public BizProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(BizProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
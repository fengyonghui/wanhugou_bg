/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
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
	private Integer bizStatus;		// 业务状态：0未审核 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
	private Double totalDetail;
	private Double recvTotal;


	private String skuIds;
	private String reqDetailIds;
	private String skuInfoIds;
	private String reqQtys;
	private String lineNos;
	private List<BizSkuInfo> skuInfoList = Lists.newArrayList();
	private BizProductInfo productInfo;
	private Boolean ownGenPoOrder; //自己生成采购单
	private Integer onlyVendor;

	private String itemNo;      //根据sku货号搜索
	private String partNo;      //根据sku编号搜索
	private String name;		//根據供應商姓名搜索
	private Byte bizStatusStart;
	private Byte bizStatusEnd; //用于查询业务状态的区间

	private Byte bizStatusNot;		//不包含的状态
	/**
	 * 列表序号
	 * */
	private Integer numberRownum;
	/**
	 * 驳回原因,审核通过标识
	 * */
	private String remarkReject;

	private List<BizRequestDetail> requestDetailList;

	private List<BizPoDetail> poDetailList;

	private Double totalMoney;

	private String vendOfficeIds;

	private String startDate;

	private String endDate;

	private String str;

	private String recvQtys;

	private CommonProcessEntity commonProcess;

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

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
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

	public List<BizPoDetail> getPoDetailList() {
		return poDetailList;
	}

	public void setPoDetailList(List<BizPoDetail> poDetailList) {
		this.poDetailList = poDetailList;
	}

	public String getVendOfficeIds() {
		return vendOfficeIds;
	}

	public void setVendOfficeIds(String vendOfficeIds) {
		this.vendOfficeIds = vendOfficeIds;
	}

	public Byte getBizStatusStart() {
		return bizStatusStart;
	}

	public void setBizStatusStart(Byte bizStatusStart) {
		this.bizStatusStart = bizStatusStart;
	}

	public Byte getBizStatusEnd() {
		return bizStatusEnd;
	}

	public void setBizStatusEnd(Byte bizStatusEnd) {
		this.bizStatusEnd = bizStatusEnd;
	}

	public String getReqQtys() {
		return reqQtys;
	}

	public void setReqQtys(String reqQtys) {
		this.reqQtys = reqQtys;
	}

	public String getSkuInfoIds() {
		return skuInfoIds;
	}

	public void setSkuInfoIds(String skuInfoIds) {
		this.skuInfoIds = skuInfoIds;
	}

	public String getReqDetailIds() {
		return reqDetailIds;
	}

	public void setReqDetailIds(String reqDetailIds) {
		this.reqDetailIds = reqDetailIds;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String getRecvQtys() {
		return recvQtys;
	}

	public void setRecvQtys(String recvQtys) {
		this.recvQtys = recvQtys;
	}

	public String getLineNos() {
		return lineNos;
	}

	public void setLineNos(String lineNos) {
		this.lineNos = lineNos;
	}

	public Boolean getOwnGenPoOrder() {
		return ownGenPoOrder;
	}

	public void setOwnGenPoOrder(Boolean ownGenPoOrder) {
		this.ownGenPoOrder = ownGenPoOrder;
	}

	public Integer getOnlyVendor() {
		return onlyVendor;
	}

	public void setOnlyVendor(Integer onlyVendor) {
		this.onlyVendor = onlyVendor;
	}

	public String getItemNo() {
		return itemNo;
	}

	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
	}

	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}

	public Integer getNumberRownum() {
		return numberRownum;
	}

	public void setNumberRownum(Integer numberRownum) {
		this.numberRownum = numberRownum;
	}

	public Byte getBizStatusNot() {
		return bizStatusNot;
	}

	public void setBizStatusNot(Byte bizStatusNot) {
		this.bizStatusNot = bizStatusNot;
	}

	public String getRemarkReject() {
		return remarkReject;
	}

	public void setRemarkReject(String remarkReject) {
		this.remarkReject = remarkReject;
	}


	public Double getTotalDetail() {
		return totalDetail;
	}

	public void setTotalDetail(Double totalDetail) {
		this.totalDetail = totalDetail;
	}

	public Double getRecvTotal() {
		return recvTotal;
	}

	public void setRecvTotal(Double recvTotal) {
		this.recvTotal = recvTotal;
	}

	public CommonProcessEntity getCommonProcess() {
		return commonProcess;
	}

	public void setCommonProcess(CommonProcessEntity commonProcess) {
		this.commonProcess = commonProcess;
	}

}
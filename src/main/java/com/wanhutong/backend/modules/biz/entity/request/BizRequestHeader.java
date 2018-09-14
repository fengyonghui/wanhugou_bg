/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

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

	/**
	 * 备货单类型
	 */
	private Byte headerType;

	/**
	 * 备货清单查看 已生成的采购单
	 * */
	private String poSource;
	/**
	 * 备货清单的 备货商品数量
	 * */
	private Integer ordCount;
	/**
	 * 查看已经有的商品总库存数及标识符
	 * */
	private String invenSource;

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

	/**
	 * 品类 id
	 * */
	private BizVarietyInfo varietyInfo;

	/**
	 * 排产类型: 0:按订单排产， 1:按商品排产
	 */
	private Integer schedulingType;

//	/**
//	 * 排产计划
//	 */
//	private List<BizSchedulingPlan> schedulingPlanList;

	private List<BizRequestDetail> requestDetailList;

	private List<BizPoDetail> poDetailList;

	private Double totalMoney;

	private String vendOfficeIds;

	private String startDate;

	private String endDate;

	private String str;

	private String recvQtys;

	private CommonProcessEntity commonProcess;

	private String dataFrom; //数据属于哪个页面

	private Integer vendorId; //根据供应商Id搜索
	private Integer vendorName; //根据供应商Id搜索

	/**
	 * 备货方：1.采购中心备货；2.供应商备货
	 */
	private Integer fromType;
	/**
	 * 供应商拓展信息
	 */
	private BizVendInfo bizVendInfo;
	/**
	 * 申请支付金额
	 */
	private BigDecimal planPay;

	/**
	 * 付款时间
	 */
	private Date payDeadline;
	/**
	 * 当前支付单ID
	 */
	private Integer currentPaymentId;
	/**
	 * 支付单
	 */
	private BizPoPaymentOrder bizPoPaymentOrder;

	/**
	 * 该采购单下所有商品的总采购数量
	 */
	private Integer totalOrdQty;

	/**
	 * 该采购单下按商品排产的总排产量
	 */
	private Integer totalSchedulingDetailNum;

	/**
	 * 该采购单下按订单排产的总排产量
	 */
	private Integer totalSchedulingHeaderNum;
	/**
	 * 该采购单下按商品排产时总的已确认量
	 */
	private Integer totalCompleteScheduHeaderNum;
	/**
	 * 与供应商结算的金额
	 */
	private BigDecimal balanceTotal;

	/**
	 * 已审批流程
	 */
	private List<CommonProcessEntity> commonProcessList;

	/**
	 * 一单到底对应的采购单
	 */
	private BizPoHeader bizPoHeader;

	/**
	 * 用于审核状态查询
	 */
	private String process;

	/**
	 * 备货单审核code
	 */
	private Integer reqCode;

	/**
	 * PO审核code
	 */
	private Integer poCode;

	/**
	 * 审核是用来判断审核的是RE还是PO
	 */
	private String processPo;

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

	public String getPoSource() {
		return poSource;
	}

	public void setPoSource(String poSource) {
		this.poSource = poSource;
	}

	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public BizVarietyInfo getVarietyInfo() {
		return varietyInfo;
	}

	public void setVarietyInfo(BizVarietyInfo varietyInfo) {
		this.varietyInfo = varietyInfo;
	}

	public Integer getOrdCount() {
		return ordCount;
	}

	public void setOrdCount(Integer ordCount) {
		this.ordCount = ordCount;
	}

	public String getInvenSource() {
		return invenSource;
	}

	public void setInvenSource(String invenSource) {
		this.invenSource = invenSource;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public Integer getVendorName() {
		return vendorName;
	}

	public void setVendorName(Integer vendorName) {
		this.vendorName = vendorName;
	}

	public Integer getFromType() {
		return fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	public BizVendInfo getBizVendInfo() {
		return bizVendInfo;
	}

	public void setBizVendInfo(BizVendInfo bizVendInfo) {
		this.bizVendInfo = bizVendInfo;
	}

	public BigDecimal getPlanPay() {
		return planPay;
	}

	public void setPlanPay(BigDecimal planPay) {
		this.planPay = planPay;
	}

	public BizPoPaymentOrder getBizPoPaymentOrder() {
		return bizPoPaymentOrder;
	}

	public void setBizPoPaymentOrder(BizPoPaymentOrder bizPoPaymentOrder) {
		this.bizPoPaymentOrder = bizPoPaymentOrder;
	}

	public Date getPayDeadline() {
		return payDeadline;
	}

	public void setPayDeadline(Date payDeadline) {
		this.payDeadline = payDeadline;
	}

	public Integer getCurrentPaymentId() {
		return currentPaymentId;
	}

	public void setCurrentPaymentId(Integer currentPaymentId) {
		this.currentPaymentId = currentPaymentId;
	}

	public Integer getTotalOrdQty() {
		return totalOrdQty;
	}

	public void setTotalOrdQty(Integer totalOrdQty) {
		this.totalOrdQty = totalOrdQty;
	}

	public BigDecimal getBalanceTotal() {
		return balanceTotal;
	}

	public void setBalanceTotal(BigDecimal balanceTotal) {
		this.balanceTotal = balanceTotal;
	}

	public List<CommonProcessEntity> getCommonProcessList() {
		return commonProcessList;
	}

	public void setCommonProcessList(List<CommonProcessEntity> commonProcessList) {
		this.commonProcessList = commonProcessList;
	}

	public Integer getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(Integer schedulingType) {
		this.schedulingType = schedulingType;
	}

	public Integer getTotalSchedulingDetailNum() {
		return totalSchedulingDetailNum;
	}

	public void setTotalSchedulingDetailNum(Integer totalSchedulingDetailNum) {
		this.totalSchedulingDetailNum = totalSchedulingDetailNum;
	}

	public Integer getTotalSchedulingHeaderNum() {
		return totalSchedulingHeaderNum;
	}

	public void setTotalSchedulingHeaderNum(Integer totalSchedulingHeaderNum) {
		this.totalSchedulingHeaderNum = totalSchedulingHeaderNum;
	}

	public Integer getTotalCompleteScheduHeaderNum() {
		return totalCompleteScheduHeaderNum;
	}

	public void setTotalCompleteScheduHeaderNum(Integer totalCompleteScheduHeaderNum) {
		this.totalCompleteScheduHeaderNum = totalCompleteScheduHeaderNum;
	}

	public BizPoHeader getBizPoHeader() {
		return bizPoHeader;
	}

	public void setBizPoHeader(BizPoHeader bizPoHeader) {
		this.bizPoHeader = bizPoHeader;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public Integer getReqCode() {
		return reqCode;
	}

	public void setReqCode(Integer reqCode) {
		this.reqCode = reqCode;
	}

	public Integer getPoCode() {
		return poCode;
	}

	public void setPoCode(Integer poCode) {
		this.poCode = poCode;
	}

	public String getProcessPo() {
		return processPo;
	}

	public void setProcessPo(String processPo) {
		this.processPo = processPo;
	}

	public Byte getHeaderType() {
		return headerType;
	}

	public void setHeaderType(Byte headerType) {
		this.headerType = headerType;
	}
}
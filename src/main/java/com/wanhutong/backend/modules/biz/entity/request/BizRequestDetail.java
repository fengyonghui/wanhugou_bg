/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.request;

import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.context.annotation.Lazy;

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
	 * 已出库数量
	 */
	private Integer outQty;

	/**
	 * 实际库存
	 */
	private Integer actualQty;
	/**
	 * 库存
	 */
	private BizInventorySku inventorySku;
	/**
	 * 备货清单查看 已生成的采购单
	 * */
	private BizPoHeader bizPoHeader;
	private String invName;//仓库名称
	private Integer skuInvQty;//库存数量
	/**
	 * 查询已生成的采购单
	 * */
	private String poheaderSource;
	/**
	 * 查看商品的总库存数量
	 * */
	private Integer invenSkuOrd;
	/**
	 * 销售数量
	 */
	private Integer sellCount;

    /**
     * 排产计划
     */
    //private List<BizSchedulingPlan> schedulingPlanList;

	/**
	 * 总的已排产量
	 */
	private Integer sumSchedulingNum = 0;

	/**
	 * 订单按商品排产是，总的已确认量
	 */
	private Integer sumCompleteDetailNum;

	/**
	 * 总的已确认数量
	 */
	private Integer sumCompleteNum = 0;

	private BizSchedulingPlan bizSchedulingPlan;

	/**
	 * detail商品对应的各个采购中心的库存，Map<采购中心ID,库存数>
	 */
	private Map<String,Integer> invSkuMap;

	/**
	 * 品类主管
	 */
	private User varietyUser;

	/**
	 * 前一天的出库数量
	 */
	private Integer sumSendNum;

	/**
	 * 出库关系
	 */
	private BizInventoryOrderRequest invOrderReq;

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

	public BizPoHeader getBizPoHeader() {
		return bizPoHeader;
	}

	public void setBizPoHeader(BizPoHeader bizPoHeader) {
		this.bizPoHeader = bizPoHeader;
	}

	public String getInvName() {
		return invName;
	}

	public void setInvName(String invName) {
		this.invName = invName;
	}

	public Integer getSkuInvQty() {
		return skuInvQty;
	}

	public void setSkuInvQty(Integer skuInvQty) {
		this.skuInvQty = skuInvQty;
	}

	public String getPoheaderSource() {
		return poheaderSource;
	}

	public void setPoheaderSource(String poheaderSource) {
		this.poheaderSource = poheaderSource;
	}

	public Integer getInvenSkuOrd() {
		return invenSkuOrd;
	}

	public void setInvenSkuOrd(Integer invenSkuOrd) {
		this.invenSkuOrd = invenSkuOrd;
	}

	public Integer getSellCount() {
		return sellCount;
	}

	public void setSellCount(Integer sellCount) {
		this.sellCount = sellCount;
	}

//    public List<BizSchedulingPlan> getSchedulingPlanList() {
//        return schedulingPlanList;
//    }
//
//    public void setSchedulingPlanList(List<BizSchedulingPlan> schedulingPlanList) {
//        this.schedulingPlanList = schedulingPlanList;
//    }

	public Integer getSumSchedulingNum() {
		return sumSchedulingNum;
	}

	public void setSumSchedulingNum(Integer sumSchedulingNum) {
		this.sumSchedulingNum = sumSchedulingNum;
	}

	public Integer getSumCompleteNum() {
		return sumCompleteNum;
	}

	public void setSumCompleteNum(Integer sumCompleteNum) {
		this.sumCompleteNum = sumCompleteNum;
	}

	public BizSchedulingPlan getBizSchedulingPlan() {
		return bizSchedulingPlan;
	}

	public void setBizSchedulingPlan(BizSchedulingPlan bizSchedulingPlan) {
		this.bizSchedulingPlan = bizSchedulingPlan;
	}

	public Integer getSumCompleteDetailNum() {
		return sumCompleteDetailNum;
	}

	public void setSumCompleteDetailNum(Integer sumCompleteDetailNum) {
		this.sumCompleteDetailNum = sumCompleteDetailNum;
	}

	public Map<String, Integer> getInvSkuMap() {
		return invSkuMap;
	}

	public void setInvSkuMap(Map<String, Integer> invSkuMap) {
		this.invSkuMap = invSkuMap;
	}

	public User getVarietyUser() {
		return varietyUser;
	}

	public void setVarietyUser(User varietyUser) {
		this.varietyUser = varietyUser;
	}

	public Integer getOutQty() {
		return outQty;
	}

	public void setOutQty(Integer outQty) {
		this.outQty = outQty;
	}

	public BizInventorySku getInventorySku() {
		return inventorySku;
	}

	public void setInventorySku(BizInventorySku inventorySku) {
		this.inventorySku = inventorySku;
	}

	public Integer getActualQty() {
		return actualQty;
	}

	public void setActualQty(Integer actualQty) {
		this.actualQty = actualQty;
	}

	public Integer getSumSendNum() {
		return sumSendNum;
	}

	public void setSumSendNum(Integer sumSendNum) {
		this.sumSendNum = sumSendNum;
	}

	public BizInventoryOrderRequest getInvOrderReq() {
		return invOrderReq;
	}

	public void setInvOrderReq(BizInventoryOrderRequest invOrderReq) {
		this.invOrderReq = invOrderReq;
	}
}
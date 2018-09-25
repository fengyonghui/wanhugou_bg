/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.po;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.hibernate.validator.constraints.Length;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 采购订单表Entity
 * @author liuying
 * @version 2017-12-30
 */
public class BizPoHeader extends DataEntity<BizPoHeader> {
	
	private static final long serialVersionUID = 1L;
	private String orderNum;		// 订单编号-由系统生成；唯一
	private Office vendOffice;		// 供应商ID sys_office.id &amp;  type=vend
	private Double totalDetail;		// 订单详情总价
	private Double totalExp;		// 订单总费用
	private Double freight;		// 运费
	private Byte invStatus;		// 0 不开发票 1 未开发票 3 已开发票
	private Byte bizStatus;		// 业务状态 0未支付；1首付款支付 2全部支付3已发货 4已收货 5 已完成
	private Double initialPay; //首付款
	private Date lastPayDate;  //最后结算日期（账期）
	private String remark;     //备注
	private Office deliveryOffice;//交割地点（供应商或采购商）
	private Integer deliveryStatus;
	private String str;//详情标志
	/**
	 *	是否是预览数据 ,1是，2否
	 * */
	private int isPrewUseful;

	private BizPlatformInfo plateformInfo;		// 订单来源； biz_platform_info.id
	private List<BizPoDetail> poDetailList;
	private String orderDetailIds;
	private String skuInfoIds;
	private String reqDetailIds;
	private String unitPrices;
	private String ordQtys;
	private List<BizPoOrderReq>poOrderReqList;

	private Map<Integer,ArrayList<BizPoOrderReq>> orderNumMap;
	private Map<String,Integer> orderSourceMap;

	private int isPrew;

	private BizPoPaymentOrder bizPoPaymentOrder;

	private Integer currentPaymentId;

	private Integer processId;
    private CommonProcessEntity commonProcess;
    private CommonProcessEntity prevCommonProcess;

	private BigDecimal planPay;
	private BigDecimal payTotal;

	private List<CommonProcessEntity> commonProcessList;

	private String num;	//查询的订单号和备货单号

	private Date payDeadline;

	private String type;    //createPo 为生成采购单的状态

	/**
	 * 起始金额
	 */
	private BigDecimal startPrice;
	/**
	 * 结束金额
	 */
	private BigDecimal endPrice;
	/**
	 * 起始付款时间
	 */
	private Date startPayTime;
	/**
	 * 结束付款时间
	 */
	private Date endPayTime;

	/**
	 * 该采购单下所有商品的总采购数量
	 */
	private Integer totalOrdQty;

	/**
	 * 按商品排产的总排产量
	 */
	private Integer totalSchedulingDetailNum;

	/**
	 * 按订单排产的总排产量
	 */
	private Integer totalSchedulingHeaderNum;

	/**
	 * 该采购单下按商品排产时总的已确认量
	 */
	private Integer totalCompleteScheduHeaderNum;

	/**
	 * 排产类型: 0:按订单排产， 1:按商品排产
	 */
	private Integer schedulingType;
	/**
	 * 备货单
	 */
	private BizRequestHeader bizRequestHeader;

	/**
	 * 订单
	 */
	private BizOrderHeader bizOrderHeader;

	/**
	 * 排产
	 */
	private BizSchedulingPlan bizSchedulingPlan;

	/**
	 * 采购单排产状态 0,未排产  1,排产中  2,排产完成
	 */
	private Integer poSchType;

	/**
	 * 用于新版本和旧版本的PO页面查询的过滤时间条件
	 */
	private Date filteringDate;

	/**
	 * type 拼接字符串
	 *
	 * @return
	 */
	private String processTypeStr;

	/**
	 * type 拼接字符串
	 *
	 * @return
	 */
	private List<String> processTypeList;

	/**
	 * 页面来源
	 */
	private String fromPage;

	/**
	 * 待支付
	 * @return
	 */
	private Integer waitPay;

	/**
	 *
	 * @return
	 */
	private Integer applyPayment;

	public Integer getCurrentPaymentId() {
		return currentPaymentId;
	}

	public void setCurrentPaymentId(Integer currentPaymentId) {
		this.currentPaymentId = currentPaymentId;
	}

	public BizPoHeader() {
		super();
	}

	public BizPoHeader(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="订单编号-由系统生成；唯一长度必须介于 1 和 30 之间")
	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public int getIsPrew() {
		return isPrew;
	}

	public void setIsPrew(int isPrew) {
		this.isPrew = isPrew;
	}

	public Office getVendOffice() {
		return vendOffice;
	}

	public void setVendOffice(Office vendOffice) {
		this.vendOffice = vendOffice;
	}

	public Double getTotalDetail() {
		return totalDetail;
	}

	public void setTotalDetail(Double totalDetail) {
		this.totalDetail = totalDetail;
	}

	public Double getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(Double totalExp) {
		this.totalExp = totalExp;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Byte getInvStatus() {
		return invStatus;
	}

	public void setInvStatus(Byte invStatus) {
		this.invStatus = invStatus;
	}

	public Byte getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Byte bizStatus) {
		this.bizStatus = bizStatus;
	}

	public BizPlatformInfo getPlateformInfo() {
		return plateformInfo;
	}

	public void setPlateformInfo(BizPlatformInfo plateformInfo) {
		this.plateformInfo = plateformInfo;
	}

	public List<BizPoDetail> getPoDetailList() {
		return poDetailList;
	}

	public void setPoDetailList(List<BizPoDetail> poDetailList) {
		this.poDetailList = poDetailList;
	}

	public String getOrderDetailIds() {
		return orderDetailIds;
	}

	public void setOrderDetailIds(String orderDetailIds) {
		this.orderDetailIds = orderDetailIds;
	}

	public String getReqDetailIds() {
		return reqDetailIds;
	}

	public void setReqDetailIds(String reqDetailIds) {
		this.reqDetailIds = reqDetailIds;
	}

	public String getUnitPrices() {
		return unitPrices;
	}

	public void setUnitPrices(String unitPrices) {
		this.unitPrices = unitPrices;
	}

	public String getSkuInfoIds() {
		return skuInfoIds;
	}

	public void setSkuInfoIds(String skuInfoIds) {
		this.skuInfoIds = skuInfoIds;
	}

	public String getOrdQtys() {
		return ordQtys;
	}

	public void setOrdQtys(String ordQtys) {
		this.ordQtys = ordQtys;
	}

	public List<BizPoOrderReq> getPoOrderReqList() {
		return poOrderReqList;
	}

	public void setPoOrderReqList(List<BizPoOrderReq> poOrderReqList) {
		this.poOrderReqList = poOrderReqList;
	}

	public Double getInitialPay() {
		return initialPay;
	}

	public void setInitialPay(Double initialPay) {
		this.initialPay = initialPay;
	}

	public Date getLastPayDate() {
		return lastPayDate;
	}

	public void setLastPayDate(Date lastPayDate) {
		this.lastPayDate = lastPayDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Office getDeliveryOffice() {
		return deliveryOffice;
	}

	public void setDeliveryOffice(Office deliveryOffice) {
		this.deliveryOffice = deliveryOffice;
	}

	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(Integer deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public Map<Integer, ArrayList<BizPoOrderReq>> getOrderNumMap() {
		return orderNumMap;
	}

	public void setOrderNumMap(Map<Integer, ArrayList<BizPoOrderReq>> orderNumMap) {
		this.orderNumMap = orderNumMap;
	}

	public Map<String, Integer> getOrderSourceMap() {
		return orderSourceMap;
	}

	public void setOrderSourceMap(Map<String, Integer> orderSourceMap) {
		this.orderSourceMap = orderSourceMap;
	}

	public BizPoPaymentOrder getBizPoPaymentOrder() {
		return bizPoPaymentOrder;
	}

	public void setBizPoPaymentOrder(BizPoPaymentOrder bizPoPaymentOrder) {
		this.bizPoPaymentOrder = bizPoPaymentOrder;
	}

	public BigDecimal getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(BigDecimal payTotal) {
		this.payTotal = payTotal;
	}

    public CommonProcessEntity getCommonProcess() {
        return commonProcess;
    }

    public void setCommonProcess(CommonProcessEntity commonProcess) {
        this.commonProcess = commonProcess;
    }

    public CommonProcessEntity getPrevCommonProcess() {
        return prevCommonProcess;
    }

    public void setPrevCommonProcess(CommonProcessEntity prevCommonProcess) {
        this.prevCommonProcess = prevCommonProcess;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

	public Date getPayDeadline() {
		return payDeadline;
	}

	public void setPayDeadline(Date payDeadline) {
		this.payDeadline = payDeadline;
	}

	public BigDecimal getPlanPay() {
		return planPay;
	}

	public void setPlanPay(BigDecimal planPay) {
		this.planPay = planPay;
	}


    public int getIsPrewUseful() {
        return isPrewUseful;
    }

    public void setIsPrewUseful(int isPrewUseful) {
        this.isPrewUseful = isPrewUseful;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

	public List<CommonProcessEntity> getCommonProcessList() {
		return commonProcessList;
	}

	public void setCommonProcessList(List<CommonProcessEntity> commonProcessList) {
		this.commonProcessList = commonProcessList;
	}

	public BigDecimal getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	public BigDecimal getEndPrice() {
		return endPrice;
	}

	public void setEndPrice(BigDecimal endPrice) {
		this.endPrice = endPrice;
	}

	public enum BizStatus {
		NO_PAY(0, "未支付"),
		DOWN_PAYMENT(1, "首付款支付"),
		ALL_PAY(2, "全部支付"),
		COMPLETE(5, "已完成"),
		PROCESS(6, "审批中"),
		PROCESS_COMPLETE(7, "审批完成"),
		CANCEL(10, "取消"),
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

	public enum SchedulingType {

		ORDER(0),
		SKU(1),
		;
		private Integer type;

		SchedulingType(Integer type){this.type = type;}

		public Integer getType() {
			return type;
		}
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public Date getStartPayTime() {
		return startPayTime;
	}

	public void setStartPayTime(Date startPayTime) {
		this.startPayTime = startPayTime;
	}

	public Date getEndPayTime() {
		return endPayTime;
	}

	public void setEndPayTime(Date endPayTime) {
		this.endPayTime = endPayTime;
	}

	public Integer getTotalOrdQty() {
		return totalOrdQty;
	}

	public void setTotalOrdQty(Integer totalOrdQty) {
		this.totalOrdQty = totalOrdQty;
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

	public BizRequestHeader getBizRequestHeader() {
		return bizRequestHeader;
	}

	public void setBizRequestHeader(BizRequestHeader bizRequestHeader) {
		this.bizRequestHeader = bizRequestHeader;
	}

	public BizSchedulingPlan getBizSchedulingPlan() {
		return bizSchedulingPlan;
	}

	public void setBizSchedulingPlan(BizSchedulingPlan bizSchedulingPlan) {
		this.bizSchedulingPlan = bizSchedulingPlan;
	}

	public BizOrderHeader getBizOrderHeader() {
		return bizOrderHeader;
	}

	public void setBizOrderHeader(BizOrderHeader bizOrderHeader) {
		this.bizOrderHeader = bizOrderHeader;
	}

	public Integer getPoSchType() {
		return poSchType;
	}

	public void setPoSchType(Integer poSchType) {
		this.poSchType = poSchType;
	}

	public Date getFilteringDate() {
		return filteringDate;
	}

	public void setFilteringDate(Date filteringDate) {
		this.filteringDate = filteringDate;
	}

	public String getProcessTypeStr() {
		return processTypeStr;
	}

	public void setProcessTypeStr(String processTypeStr) {
		this.processTypeStr = processTypeStr;
	}

	public List<String> getProcessTypeList() {
		return processTypeList;
	}

	public void setProcessTypeList(List<String> processTypeList) {
		this.processTypeList = processTypeList;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public Integer getWaitPay() {
		return waitPay;
	}

	public void setWaitPay(Integer waitPay) {
		this.waitPay = waitPay;
	}

	public Integer getApplyPayment() {
		return applyPayment;
	}

	public void setApplyPayment(Integer applyPayment) {
		this.applyPayment = applyPayment;
	}
}
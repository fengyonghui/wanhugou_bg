/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.inventory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 发货单Entity
 * @author 张腾飞
 * @version 2018-03-05
 */
public class BizInvoice extends DataEntity<BizInvoice> {
	
	private static final long serialVersionUID = 1L;
	private String sendNumber;		// 发货单号-由系统生成；唯一
	private Office logistics;		// 物流商ID，biz_logistics.id
	private String imgUrl;		// 物流信息图(img_server+img_path)
	private Double valuePrice;		// 货值
	private Double operation;		// 操作费
	private Double freight;		// 运费
	private String carrier;		// 承运人
	private Integer settlementStatus;		// 物流结算方式：1、现结；2、账期;
	private Integer bizStatus;		//发货地：0、采购中心； 1、供货中心；
	private Integer ship;		//发货单来源：0、销售订单；1、备货单
	private Date sendDate;		// 供货时间
	private List<BizOrderHeader> orderHeaderList;
	private List<BizRequestHeader> requestHeaderList;
	private String requestHeaders;		//发货备货单ID
	private String orderHeaders;		//发货订单ID
	private String sendNums;		//供货数
	private String reqDetails;		//发货备货单详情ID
	private String ordDetails;		//发货订单详情ID
	private String trackingNumber;		//物流单号

	private String orderNum; //订单号
	private String reqNo; //备货单号

	/**
	 * 调拨单号
	 */
	private String transferNo;

	/**
	 * 物流单页面标志
	 * */
	private String targetPage;

	/**
	 * 同一物流单号下总运费
	 * */
	private Double logisticsFreight;
	/**
	 * 同一物流单号下总操作费
	 * */
	private Double logisticsOperation;
	/**
	 * 同一物流单号下总货值
	 * */
	private Double logisticsValuePrice;

	/**
	 * 集货地点
	 * */
	private Byte collLocate;

	/**
	 * 验货时间
	 */
	private Date inspectDate;

	/**
	 * 验货员
	 */
	private User inspector;

	/**
	 * 验货备注
	 */
	private String inspectRemark;

    /**
     * 用于页面传参 str = audit (确认发货信息)
     */
	private String str;

    /**
     * 是否已确认发货单
     */
	private Integer isConfirm;

	/**
	 * 用于判断页面新增发货单（source = new）
	 */
	private String source;

	private String creOrdLogistics;


	public BizInvoice() {
		super();
	}

	public BizInvoice(Integer id){
		super(id);
	}

	@Length(min=1, max=30, message="发货单号-由系统生成；唯一长度必须介于 1 和 30 之间")
	public String getSendNumber() {
		return sendNumber;
	}

	public void setSendNumber(String sendNumber) {
		this.sendNumber = sendNumber;
	}
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	@Length(min=1, max=20, message="承运人长度必须介于 1 和 20 之间")
	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="供货时间不能为空")
	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public Office getLogistics() {
		return logistics;
	}

	public void setLogistics(Office logistics) {
		this.logistics = logistics;
	}

	public Double getValuePrice() {
		return valuePrice;
	}

	public void setValuePrice(Double valuePrice) {
		this.valuePrice = valuePrice;
	}

	public Double getOperation() {
		return operation;
	}

	public void setOperation(Double operation) {
		this.operation = operation;
	}

	public Double getFreight() {
		return freight;
	}

	public void setFreight(Double freight) {
		this.freight = freight;
	}

	public Integer getSettlementStatus() {
		return settlementStatus;
	}

	public void setSettlementStatus(Integer settlementStatus) {
		this.settlementStatus = settlementStatus;
	}

	public List<BizOrderHeader> getOrderHeaderList() {
		return orderHeaderList;
	}

	public void setOrderHeaderList(List<BizOrderHeader> orderHeaderList) {
		this.orderHeaderList = orderHeaderList;
	}

	public List<BizRequestHeader> getRequestHeaderList() {
		return requestHeaderList;
	}

	public void setRequestHeaderList(List<BizRequestHeader> requestHeaderList) {
		this.requestHeaderList = requestHeaderList;
	}

	public String getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public String getOrderHeaders() {
		return orderHeaders;
	}

	public void setOrderHeaders(String orderHeaders) {
		this.orderHeaders = orderHeaders;
	}

	public String getSendNums() {
		return sendNums;
	}

	public void setSendNums(String sendNums) {
		this.sendNums = sendNums;
	}

	public String getReqDetails() {
		return reqDetails;
	}

	public void setReqDetails(String reqDetails) {
		this.reqDetails = reqDetails;
	}

	public String getOrdDetails() {
		return ordDetails;
	}

	public void setOrdDetails(String ordDetails) {
		this.ordDetails = ordDetails;
	}

	public Integer getShip() {
		return ship;
	}

	public void setShip(Integer ship) {
		this.ship = ship;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}

	public String getReqNo() {
		return reqNo;
	}

	public void setReqNo(String reqNo) {
		this.reqNo = reqNo;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

	public Double getLogisticsFreight() {
		return logisticsFreight;
	}

	public void setLogisticsFreight(Double logisticsFreight) {
		this.logisticsFreight = logisticsFreight;
	}

	public Double getLogisticsOperation() {
		return logisticsOperation;
	}

	public void setLogisticsOperation(Double logisticsOperation) {
		this.logisticsOperation = logisticsOperation;
	}

	public Double getLogisticsValuePrice() {
		return logisticsValuePrice;
	}

	public void setLogisticsValuePrice(Double logisticsValuePrice) {
		this.logisticsValuePrice = logisticsValuePrice;
	}

	public Date getInspectDate() {
		return inspectDate;
	}

	public void setInspectDate(Date inspectDate) {
		this.inspectDate = inspectDate;
	}

	public Byte getCollLocate() {
		return collLocate;
	}

	public void setCollLocate(Byte collLocate) {
		this.collLocate = collLocate;
	}

	public User getInspector() {
		return inspector;
	}

	public void setInspector(User inspector) {
		this.inspector = inspector;
	}

	public String getInspectRemark() {
		return inspectRemark;
	}

	public void setInspectRemark(String inspectRemark) {
		this.inspectRemark = inspectRemark;
	}

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public enum BizStatus {

        /**
         * 采购中心发货
         */
		CENTER(0),

        /**
         * 供货部发货
         */
        SUPPLY(1),
		;
		private Integer bizStatus;

		BizStatus(Integer bizStatus) {
			this.bizStatus = bizStatus;
		}
		public Integer getBizStatus() {
			return bizStatus;
		}
	}

	public enum Ship {

        /**
         * 订单发货
         */
		SO(0),

        /**
         * 备货单发货
         */
		RE(1),

		/**
		 * 调拨单发货
		 */
		TR(2),
		;
		private Integer ship;

		Ship(Integer ship) {
			this.ship = ship;
		}
		public Integer getShip() {
			return ship;
		}
	}

    public Integer getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(Integer isConfirm) {
        this.isConfirm = isConfirm;
    }

    public enum IsConfirm {

        /**
         * 未确认
         */
        NO(0),
        YES(1),
        ;
        private Integer isConfirm;

        IsConfirm(Integer isConfirm) {
            this.isConfirm = isConfirm;
        }

        public Integer getIsConfirm() {
            return isConfirm;
        }
    }

	public String getCreOrdLogistics() {
		return creOrdLogistics;
	}

	public void setCreOrdLogistics(String creOrdLogistics) {
		this.creOrdLogistics = creOrdLogistics;
	}

	public String getTransferNo() {
		return transferNo;
	}

	public void setTransferNo(String transferNo) {
		this.transferNo = transferNo;
	}
}
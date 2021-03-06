/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.sys.entity.Office;

/**
 * 订单状态修改日志Entity
 * @author Oy
 * @version 2018-05-15
 */
public class BizOrderStatus extends DataEntity<BizOrderStatus> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader orderHeader;		// biz_order_detail
	private Integer bizStatus;		// biz_order_header.biz_status
	private Integer orderType;		//订单类型：0订单，1备货清单

	/**
	 * 显示经销店
	 * */
	private Office office;
	
	public BizOrderStatus() {
		super();
	}

	public BizOrderStatus(Integer id){
		super(id);
	}

	public BizOrderHeader getOrderHeader() {
		return orderHeader;
	}

	public void setOrderHeader(BizOrderHeader orderHeader) {
		this.orderHeader = orderHeader;
	}

	public Integer getBizStatus() {
		return bizStatus;
	}

	public void setBizStatus(Integer bizStatus) {
		this.bizStatus = bizStatus;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}


	public enum OrderType{
		ORDER(0, "订单"),
		REQUEST(1, "备货单"),
		INVENTORY(3,"盘点"),
		;


		private int type;
		private String desc;

		OrderType(int type, String desc) {
			this.type = type;
			this.desc = desc;
		}

		public int getType() {
			return type;
		}

		public String getDesc() {
			return desc;
		}
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
}
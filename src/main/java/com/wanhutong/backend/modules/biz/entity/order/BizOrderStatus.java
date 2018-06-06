/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.order;

import org.hibernate.validator.constraints.Length;
import com.wanhutong.backend.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

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
		REQUEST(1, "采购单"),
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
}
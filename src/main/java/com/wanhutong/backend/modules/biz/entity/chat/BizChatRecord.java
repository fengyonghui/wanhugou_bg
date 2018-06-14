/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.chat;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import javax.validation.constraints.NotNull;
import com.wanhutong.backend.modules.sys.entity.User;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 沟通记录：品类主管或客户专员，机构沟通Entity
 * @author Oy
 * @version 2018-05-22
 */
public class BizChatRecord extends DataEntity<BizChatRecord> {
	
	private static final long serialVersionUID = 1L;
	private Office office;		// 采购商 或 供应商，sys_office
	private User user;		// 品类主管 或 客户专员，sys_user
	private String chatRecord;		// 沟通记录

	/**
	 * 沟通记录 经销店/供应商 标识
	 * */
	private String source;
	private BizOrderHeader order;
	private String ordrHeaderStartTime;//日期查询
	private String orderHeaderEedTime;//日期查询

	public BizChatRecord() {
		super();
	}

	public BizChatRecord(Integer id){
		super(id);
	}

	@NotNull(message="采购商 或 供应商，sys_office不能为空")
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	
	@NotNull(message="品类主管 或 客户专员，sys_user不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=255, message="沟通记录长度必须介于 1 和 255 之间")
	public String getChatRecord() {
		return chatRecord;
	}

	public void setChatRecord(String chatRecord) {
		this.chatRecord = chatRecord;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public BizOrderHeader getOrder() {
		return order;
	}

	public void setOrder(BizOrderHeader order) {
		this.order = order;
	}

	public String getOrdrHeaderStartTime() {
		return ordrHeaderStartTime;
	}

	public void setOrdrHeaderStartTime(String ordrHeaderStartTime) {
		this.ordrHeaderStartTime = ordrHeaderStartTime;
	}

	public String getOrderHeaderEedTime() {
		return orderHeaderEedTime;
	}

	public void setOrderHeaderEedTime(String orderHeaderEedTime) {
		this.orderHeaderEedTime = orderHeaderEedTime;
	}
}
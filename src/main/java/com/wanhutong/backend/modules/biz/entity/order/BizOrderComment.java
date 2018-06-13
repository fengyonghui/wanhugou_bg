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
 * 订单备注表Entity
 * @author oy
 * @version 2018-06-12
 */
public class BizOrderComment extends DataEntity<BizOrderComment> {
	
	private static final long serialVersionUID = 1L;
	private BizOrderHeader order;		// biz_order_header.id
	private String comments;		// 订单备注
	
	public BizOrderComment() {
		super();
	}

	public BizOrderComment(Integer id){
		super(id);
	}
	
	@Length(min=1, max=500, message="订单备注长度必须介于 1 和 500 之间")
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public BizOrderHeader getOrder() {
		return order;
	}

	public void setOrder(BizOrderHeader order) {
		this.order = order;
	}
}
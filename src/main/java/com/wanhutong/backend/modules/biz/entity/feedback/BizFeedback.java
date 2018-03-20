/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.entity.feedback;

import com.wanhutong.backend.common.persistence.DataEntity;

/**
 * 意见反馈Entity
 * @author zx
 * @version 2018-03-06
 */
public class BizFeedback extends DataEntity<BizFeedback> {
	
	private static final long serialVersionUID = 1L;
	private String userFeedback;		// 意见或反馈内容
	
	public BizFeedback() {
		super();
	}

	public BizFeedback(Integer id){
		super(id);
	}


	public String getUserFeedback() {
		return userFeedback;
	}

	public void setUserFeedback(String userFeedback) {
		this.userFeedback = userFeedback;
	}

	
}
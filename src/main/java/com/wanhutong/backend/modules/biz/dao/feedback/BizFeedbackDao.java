/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.feedback;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.feedback.BizFeedback;

/**
 * 意见反馈DAO接口
 * @author zx
 * @version 2018-03-06
 */
@MyBatisDao
public interface BizFeedbackDao extends CrudDao<BizFeedback> {
	
}
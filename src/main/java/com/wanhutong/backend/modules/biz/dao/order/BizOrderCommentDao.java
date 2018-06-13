/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderComment;

/**
 * 订单备注表DAO接口
 * @author oy
 * @version 2018-06-12
 */
@MyBatisDao
public interface BizOrderCommentDao extends CrudDao<BizOrderComment> {
	
}
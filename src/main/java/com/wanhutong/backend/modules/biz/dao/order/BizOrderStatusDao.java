/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;

/**
 * 订单状态修改日志DAO接口
 * @author Oy
 * @version 2018-05-15
 */
@MyBatisDao
public interface BizOrderStatusDao extends CrudDao<BizOrderStatus> {
	
}
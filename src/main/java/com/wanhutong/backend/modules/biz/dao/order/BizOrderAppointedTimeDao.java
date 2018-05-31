/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAppointedTime;

/**
 * 代采订单约定付款时间表DAO接口
 * @author ZhangTengfei
 * @version 2018-05-30
 */
@MyBatisDao
public interface BizOrderAppointedTimeDao extends CrudDao<BizOrderAppointedTime> {
	
}
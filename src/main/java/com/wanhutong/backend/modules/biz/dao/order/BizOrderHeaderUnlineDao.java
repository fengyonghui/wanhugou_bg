/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;

/**
 * 线下支付订单(线下独有)DAO接口
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@MyBatisDao
public interface BizOrderHeaderUnlineDao extends CrudDao<BizOrderHeaderUnline> {
	
}
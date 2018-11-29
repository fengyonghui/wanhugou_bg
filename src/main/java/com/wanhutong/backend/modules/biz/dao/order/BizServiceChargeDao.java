/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceCharge;

/**
 * 服务费--配送方式DAO接口
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@MyBatisDao
public interface BizServiceChargeDao extends CrudDao<BizServiceCharge> {
	
}
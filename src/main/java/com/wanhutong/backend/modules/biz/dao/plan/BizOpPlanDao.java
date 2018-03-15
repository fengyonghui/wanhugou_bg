/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.plan;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;

/**
 * 运营计划DAO接口
 * @author 张腾飞
 * @version 2018-03-15
 */
@MyBatisDao
public interface BizOpPlanDao extends CrudDao<BizOpPlan> {
	
}
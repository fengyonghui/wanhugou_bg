/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizFreightConfig;

/**
 * 服务费设置DAO接口
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
@MyBatisDao
public interface BizFreightConfigDao extends CrudDao<BizFreightConfig> {
	
}
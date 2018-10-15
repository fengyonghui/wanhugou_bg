/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.logistic;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.logistic.BizOrderLogistics;

/**
 * 运单配置DAO接口
 * @author wangby
 * @version 2018-09-26
 */
@MyBatisDao
public interface BizOrderLogisticsDao extends CrudDao<BizOrderLogistics> {
	
}
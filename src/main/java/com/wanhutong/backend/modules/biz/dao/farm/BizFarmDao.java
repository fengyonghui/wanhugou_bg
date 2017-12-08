/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.farm;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.farm.BizFarm;

/**
 * 单表生成DAO接口
 * @author ThinkGem
 * @version 2016-07-08
 */
@MyBatisDao
public interface BizFarmDao extends CrudDao<BizFarm> {
	
}
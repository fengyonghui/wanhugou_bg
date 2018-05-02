/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shelf;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shelf.BizVarietyFactor;

/**
 * 品类阶梯价DAO接口
 * @author ZhangTengfei
 * @version 2018-04-27
 */
@MyBatisDao
public interface BizVarietyFactorDao extends CrudDao<BizVarietyFactor> {
	
}
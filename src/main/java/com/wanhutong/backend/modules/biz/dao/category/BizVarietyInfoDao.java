/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.category;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;

/**
 * 产品种类管理DAO接口
 * @author liuying
 * @version 2018-02-24
 */
@MyBatisDao
public interface BizVarietyInfoDao extends CrudDao<BizVarietyInfo> {
	
}
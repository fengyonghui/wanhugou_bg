/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSearch;

/**
 * 找货定制DAO接口
 * @author liuying
 * @version 2018-01-20
 */
@MyBatisDao
public interface BizSearchDao extends CrudDao<BizSearch> {
	
}
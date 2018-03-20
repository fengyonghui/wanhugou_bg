/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.SysCustDetails;

/**
 * 采购商店铺DAO接口
 * @author zx
 * @version 2018-03-05
 */
@MyBatisDao
public interface SysCustDetailsDao extends CrudDao<SysCustDetails> {
	
}
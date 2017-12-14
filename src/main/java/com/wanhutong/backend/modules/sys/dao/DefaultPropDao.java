/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;

/**
 * 系统属性默认表DAO接口
 * @author liuying
 * @version 2017-12-14
 */
@MyBatisDao
public interface DefaultPropDao extends CrudDao<DefaultProp> {
	
}
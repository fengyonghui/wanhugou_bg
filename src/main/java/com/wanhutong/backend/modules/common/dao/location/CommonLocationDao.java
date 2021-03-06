/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.dao.location;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;

/**
 * 地理位置信息DAO接口
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@MyBatisDao
public interface CommonLocationDao extends CrudDao<CommonLocation> {
	
}
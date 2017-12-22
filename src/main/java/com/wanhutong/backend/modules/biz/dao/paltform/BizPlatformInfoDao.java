/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.paltform;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;

/**
 * Android, iOS, PC, 线下DAO接口
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@MyBatisDao
public interface BizPlatformInfoDao extends CrudDao<BizPlatformInfo> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.custom;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;

/**
 * 客户专员管理DAO接口
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@MyBatisDao
public interface BizCustomCenterConsultantDao extends CrudDao<BizCustomCenterConsultant> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.variety;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;

/**
 * 品类与用户 关联DAO接口
 * @author Oy
 * @version 2018-05-31
 */
@MyBatisDao
public interface BizVarietyUserInfoDao extends CrudDao<BizVarietyUserInfo> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.message;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageUser;

/**
 * 站内信关系DAO接口
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@MyBatisDao
public interface BizMessageUserDao extends CrudDao<BizMessageUser> {
	
}
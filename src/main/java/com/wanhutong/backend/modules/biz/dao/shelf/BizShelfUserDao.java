/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shelf;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shelf.BizShelfUser;

/**
 * 货架用户中间表DAO接口
 * @author 张腾飞
 * @version 2018-01-11
 */
@MyBatisDao
public interface BizShelfUserDao extends CrudDao<BizShelfUser> {
	
}
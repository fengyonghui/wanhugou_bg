/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizDrawBack;

/**
 * 退款记录DAO接口
 * @author 王冰洋
 * @version 2018-07-13
 */
@MyBatisDao
public interface BizDrawBackDao extends CrudDao<BizDrawBack> {
	
}
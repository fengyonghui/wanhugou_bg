/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;

/**
 * 服务费物流线路DAO接口
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@MyBatisDao
public interface BizServiceLineDao extends CrudDao<BizServiceLine> {
	
}
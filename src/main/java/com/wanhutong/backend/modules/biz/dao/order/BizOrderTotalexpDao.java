/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderTotalexp;

/**
 * 服务费记录表DAO接口
 * @author wangby
 * @version 2018-12-11
 */
@MyBatisDao
public interface BizOrderTotalexpDao extends CrudDao<BizOrderTotalexp> {
	
}
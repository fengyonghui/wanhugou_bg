/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;

/**
 * 订单备货单出库关系DAO接口
 * @author ZhangTengfei
 * @version 2018-09-20
 */
@MyBatisDao
public interface BizInventoryOrderRequestDao extends CrudDao<BizInventoryOrderRequest> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizPurchaserVendor;

/**
 * 采购商供应商关联关系DAO接口
 * @author ZhangTengfei
 * @version 2018-05-16
 */
@MyBatisDao
public interface BizPurchaserVendorDao extends CrudDao<BizPurchaserVendor> {
	
}
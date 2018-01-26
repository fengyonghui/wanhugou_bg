/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;

/**
 * 订单详情商品属性DAO接口
 * @author OuyangXiutian
 * @version 2018-01-25
 */
@MyBatisDao
public interface BizOrderSkuPropValueDao extends CrudDao<BizOrderSkuPropValue> {
	
}
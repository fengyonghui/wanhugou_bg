/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.product;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;

/**
 * 记录产品所有属性值DAO接口
 * @author zx
 * @version 2017-12-14
 */
@MyBatisDao
public interface BizProdPropValueDao extends CrudDao<BizProdPropValue> {
	
}
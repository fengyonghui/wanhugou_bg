/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;

/**
 * sku属性DAO接口
 * @author zx
 * @version 2017-12-14
 */
@MyBatisDao
public interface BizSkuPropValueDao extends CrudDao<BizSkuPropValue> {
	
}
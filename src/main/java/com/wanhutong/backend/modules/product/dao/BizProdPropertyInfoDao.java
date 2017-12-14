/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.product.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.product.entity.BizProdPropertyInfo;

/**
 * 属性表DAO接口
 * @author zx
 * @version 2017-12-14
 */
@MyBatisDao
public interface BizProdPropertyInfoDao extends CrudDao<BizProdPropertyInfo> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.product;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProdViewLog;

/**
 * 产品查看日志DAO接口
 * @author zx
 * @version 2018-02-22
 */
@MyBatisDao
public interface BizProdViewLogDao extends CrudDao<BizProdViewLog> {
	
}
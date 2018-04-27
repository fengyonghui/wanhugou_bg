/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuViewLog;

/**
 * 商品出厂价日志表DAO接口
 * @author Oy
 * @version 2018-04-23
 */
@MyBatisDao
public interface BizSkuViewLogDao extends CrudDao<BizSkuViewLog> {
	
}
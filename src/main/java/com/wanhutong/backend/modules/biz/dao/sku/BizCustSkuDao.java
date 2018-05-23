/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.sku;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.sku.BizCustSku;

/**
 * 采购商商品价格DAO接口
 * @author ZhangTengfei
 * @version 2018-05-15
 */
@MyBatisDao
public interface BizCustSkuDao extends CrudDao<BizCustSku> {
	
}
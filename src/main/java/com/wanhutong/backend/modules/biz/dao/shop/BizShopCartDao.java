/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shop;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopCart;

/**
 * 商品购物车DAO接口
 * @author OuyangXiutian
 * @version 2018-01-03
 */
@MyBatisDao
public interface BizShopCartDao extends CrudDao<BizShopCart> {
	
}
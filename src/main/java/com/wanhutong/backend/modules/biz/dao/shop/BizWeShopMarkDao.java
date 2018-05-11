/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shop;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizWeShopMark;

/**
 * 收藏微店DAO接口
 * @author Oytang
 * @version 2018-04-10
 */
@MyBatisDao
public interface BizWeShopMarkDao extends CrudDao<BizWeShopMark> {
	
}
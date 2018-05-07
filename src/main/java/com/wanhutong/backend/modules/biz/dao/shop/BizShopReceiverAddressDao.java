/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shop;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopReceiverAddress;

/**
 * 收货地址DAO接口
 * @author Oy
 * @version 2018-04-10
 */
@MyBatisDao
public interface BizShopReceiverAddressDao extends CrudDao<BizShopReceiverAddress> {
	
}
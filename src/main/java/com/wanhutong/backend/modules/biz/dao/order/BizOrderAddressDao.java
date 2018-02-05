/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;

/**
 * 订单地址DAO接口
 * @author OuyangXiutian
 * @version 2018-01-22
 */
@MyBatisDao
public interface BizOrderAddressDao extends CrudDao<BizOrderAddress> {

	public BizOrderAddress getAddress(BizOrderAddress bizOrderAddress);
}
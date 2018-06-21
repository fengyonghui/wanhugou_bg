/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import org.apache.ibatis.annotations.Param;

/**
 * 订单地址DAO接口
 * @author OuyangXiutian
 * @version 2018-01-22
 */
@MyBatisDao
public interface BizOrderAddressDao extends CrudDao<BizOrderAddress> {

	public BizOrderAddress getAddress(BizOrderAddress bizOrderAddress);

	/**
	 * 根据订单ID取订单收货地址
	 * @param orderId 订单ID
	 * @return 订单收货地址实体
	 */
	BizOrderAddress getOrderAddrByOrderId(@Param("orderId") Integer orderId);
}
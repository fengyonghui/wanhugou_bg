/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;

/**
 * 订单详情(销售订单)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@MyBatisDao
public interface BizOrderDetailDao extends CrudDao<BizOrderDetail> {
	public Integer findMaxLine(BizOrderDetail bizOrderDetail);
}
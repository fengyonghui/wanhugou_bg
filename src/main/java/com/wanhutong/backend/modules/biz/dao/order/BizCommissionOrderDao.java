/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizCommissionOrder;

import java.util.List;

/**
 * 佣金付款订单关系表DAO接口
 * @author wangby
 * @version 2018-10-18
 */
@MyBatisDao
public interface BizCommissionOrderDao extends CrudDao<BizCommissionOrder> {

    List<BizCommissionOrder> findListForCommonProcess(BizCommissionOrder bizCommissionOrder);
}
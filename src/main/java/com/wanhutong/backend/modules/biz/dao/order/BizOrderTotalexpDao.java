/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderTotalexp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务费记录表DAO接口
 * @author wangby
 * @version 2018-12-11
 */
@MyBatisDao
public interface BizOrderTotalexpDao extends CrudDao<BizOrderTotalexp> {

    void insertBatch(@Param("amountList") List<String> amountList, @Param("orderId") Integer orderId, @Param("userId") Integer userId, @Param("type") Integer type);
}
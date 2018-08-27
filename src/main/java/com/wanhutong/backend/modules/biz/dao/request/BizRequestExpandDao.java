/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestExpand;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 备货单扩展DAO接口
 * @author TengFei.zhang
 * @version 2018-07-26
 */
@MyBatisDao
public interface BizRequestExpandDao extends CrudDao<BizRequestExpand> {

    int incrPayTotal(@Param("id")int id, @Param("payTotal")BigDecimal payTotal);

    int updatePaymentOrderId(@Param("id") Integer id, @Param("paymentId")Integer paymentId);

    Integer getIdByRequestHeaderId(@Param("requestId") Integer requestId);


	
}
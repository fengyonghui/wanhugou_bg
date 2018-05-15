/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 采购订单表DAO接口
 * @author liuying
 * @version 2017-12-30
 */
@MyBatisDao
public interface BizPoHeaderDao extends CrudDao<BizPoHeader> {

    int updatePaymentOrderId(@Param("id") Integer id, @Param("paymentId")Integer paymentId);

    int updateBizStatus(@Param("id")Integer id, @Param("status")int status);

    int updateProcessId(@Param("headerId")Integer headerId, @Param("processId") Integer processId);

    int incrPayTotal(@Param("id")int id, @Param("payTotal")BigDecimal payTotal);
}
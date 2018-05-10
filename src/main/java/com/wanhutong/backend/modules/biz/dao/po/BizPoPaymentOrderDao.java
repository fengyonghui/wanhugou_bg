/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 采购付款单DAO接口
 * @author Ma.Qiang
 * @version 2018-05-04
 */
@MyBatisDao
public interface BizPoPaymentOrderDao extends CrudDao<BizPoPaymentOrder> {

    int updateProcessId(@Param("headerId")Integer headerId, @Param("processId") Integer processId);

}
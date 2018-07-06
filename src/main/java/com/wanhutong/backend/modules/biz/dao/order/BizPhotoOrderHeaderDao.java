/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5:代采订单 6:拍照下单)DAO接口
 *
 * @author Zhangtengfei
 * @version 2018-06-14
 */
@MyBatisDao
public interface BizPhotoOrderHeaderDao extends CrudDao<BizOrderHeader> {

    void updateMoney(BizOrderHeader bizOrderHeader);

    List<BizOrderHeader> findDeliverGoodsOrderList(BizOrderHeader bizOrderHeader);

    List<BizOrderHeader> findPhotoOrderList(@Param("invoiceId") Integer invoiceId);
}

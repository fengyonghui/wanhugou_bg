/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 线下支付订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)DAO接口
 *
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@MyBatisDao
public interface BizOrderHeaderUnlineDao extends CrudDao<BizOrderHeaderUnline> {

}

/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;

/**
 * 销售采购备货中间表DAO接口
 * @author 张腾飞
 * @version 2018-01-09
 */
@MyBatisDao
public interface BizPoOrderReqDao extends CrudDao<BizPoOrderReq> {
	
}
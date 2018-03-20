/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.cust;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;

/**
 * 用户钱包DAO接口
 * @author Ouyang
 * @version 2018-03-09
 */
@MyBatisDao
public interface BizCustCreditDao extends CrudDao<BizCustCredit> {
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.vend;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;

/**
 * 供应商拓展表DAO接口
 * @author liuying
 * @version 2018-02-24
 */
@MyBatisDao
public interface BizVendInfoDao extends CrudDao<BizVendInfo> {
	int recover(BizVendInfo bizVendInfo);
}
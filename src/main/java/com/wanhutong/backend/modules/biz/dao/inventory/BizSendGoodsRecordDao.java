/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;

/**
 * 供货记录表DAO接口
 * @author 张腾飞
 * @version 2018-01-03
 */
@MyBatisDao
public interface BizSendGoodsRecordDao extends CrudDao<BizSendGoodsRecord> {
	
}
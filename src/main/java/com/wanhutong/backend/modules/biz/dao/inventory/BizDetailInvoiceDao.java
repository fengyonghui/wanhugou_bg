/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;

/**
 * 发货单和订单详情关系DAO接口
 * @author 张腾飞
 * @version 2018-03-06
 */
@MyBatisDao
public interface BizDetailInvoiceDao extends CrudDao<BizDetailInvoice> {
	
}
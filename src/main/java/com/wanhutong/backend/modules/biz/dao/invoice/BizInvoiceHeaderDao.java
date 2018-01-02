/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.invoice;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;

/**
 * 发票抬头，发票内容，发票类型DAO接口
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@MyBatisDao
public interface BizInvoiceHeaderDao extends CrudDao<BizInvoiceHeader> {
	
}
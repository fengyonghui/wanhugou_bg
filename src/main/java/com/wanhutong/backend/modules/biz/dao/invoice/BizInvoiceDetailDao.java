/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.invoice;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;

/**
 * 发票详情(发票行号,order_header.id)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@MyBatisDao
public interface BizInvoiceDetailDao extends CrudDao<BizInvoiceDetail> {
	public Integer setPrivIds(Integer[] privIds);
}
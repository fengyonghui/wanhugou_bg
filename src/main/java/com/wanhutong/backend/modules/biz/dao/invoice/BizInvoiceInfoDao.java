/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.invoice;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceInfo;

/**
 * 记录客户发票信息(发票开户行,税号)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@MyBatisDao
public interface BizInvoiceInfoDao extends CrudDao<BizInvoiceInfo> {
	public Integer findMaxLine(BizInvoiceDetail bizInvoiceDetail);
}
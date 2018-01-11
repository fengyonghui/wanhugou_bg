/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.invoice;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceHeaderDao;

/**
 * 发票抬头，发票内容，发票类型Service
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceHeaderService extends CrudService<BizInvoiceHeaderDao, BizInvoiceHeader> {

	public BizInvoiceHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInvoiceHeader> findList(BizInvoiceHeader bizInvoiceHeader) {
		return super.findList(bizInvoiceHeader);
	}
	
	public Page<BizInvoiceHeader> findPage(Page<BizInvoiceHeader> page, BizInvoiceHeader bizInvoiceHeader) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizInvoiceHeader);
		}else {
			bizInvoiceHeader.getSqlMap().put("invoiceHeader", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizInvoiceHeader);
		}

	}
	
	@Transactional(readOnly = false)
	public void save(BizInvoiceHeader bizInvoiceHeader) {
		super.save(bizInvoiceHeader);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInvoiceHeader bizInvoiceHeader) {
		super.delete(bizInvoiceHeader);
	}
	
}
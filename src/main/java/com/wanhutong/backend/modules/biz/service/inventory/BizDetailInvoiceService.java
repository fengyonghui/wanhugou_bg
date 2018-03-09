/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.dao.inventory.BizDetailInvoiceDao;

/**
 * 发货单和订单详情关系Service
 * @author 张腾飞
 * @version 2018-03-06
 */
@Service
@Transactional(readOnly = true)
public class BizDetailInvoiceService extends CrudService<BizDetailInvoiceDao, BizDetailInvoice> {

	public BizDetailInvoice get(Integer id) {
		return super.get(id);
	}
	
	public List<BizDetailInvoice> findList(BizDetailInvoice bizDetailInvoice) {
		return super.findList(bizDetailInvoice);
	}
	
	public Page<BizDetailInvoice> findPage(Page<BizDetailInvoice> page, BizDetailInvoice bizDetailInvoice) {
		return super.findPage(page, bizDetailInvoice);
	}
	
	@Transactional(readOnly = false)
	public void save(BizDetailInvoice bizDetailInvoice) {
		super.save(bizDetailInvoice);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizDetailInvoice bizDetailInvoice) {
		super.delete(bizDetailInvoice);
	}
	
}
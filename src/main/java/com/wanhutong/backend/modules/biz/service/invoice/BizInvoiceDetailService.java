/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.invoice;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceDetailDao;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceInfoDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 发票详情(发票行号,order_header.id)Service
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceDetailService extends CrudService<BizInvoiceDetailDao, BizInvoiceDetail> {
	
	@Autowired
	private BizInvoiceInfoDao bizInvoiceInfoDao;

	@Autowired
	private BizInvoiceDetailDao bizInvoiceDetailDao;
	
	public BizInvoiceDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInvoiceDetail> findList(BizInvoiceDetail bizInvoiceDetail) {
		return super.findList(bizInvoiceDetail);
	}
	
	public Page<BizInvoiceDetail> findPage(Page<BizInvoiceDetail> page, BizInvoiceDetail bizInvoiceDetail) {
		return super.findPage(page, bizInvoiceDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizInvoiceDetail bizInvoiceDetail) {
		super.save(bizInvoiceDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInvoiceDetail bizInvoiceDetail) {
		super.delete(bizInvoiceDetail);
	}
	
	@Transactional(readOnly = false)
	public Integer findMaxLine(BizInvoiceDetail bizInvoiceDetail) {
		return bizInvoiceInfoDao.findMaxLine(bizInvoiceDetail);
	}
	
	@Transactional(readOnly = false)
	public Integer setPrivIds(Integer[] privIds){
		 return bizInvoiceDetailDao.setPrivIds(privIds);
	}
	
}
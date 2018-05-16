/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizPurchaserVendor;
import com.wanhutong.backend.modules.biz.dao.order.BizPurchaserVendorDao;

/**
 * 采购商供应商关联关系Service
 * @author ZhangTengfei
 * @version 2018-05-16
 */
@Service
@Transactional(readOnly = true)
public class BizPurchaserVendorService extends CrudService<BizPurchaserVendorDao, BizPurchaserVendor> {

	public BizPurchaserVendor get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPurchaserVendor> findList(BizPurchaserVendor bizPurchaserVendor) {
		return super.findList(bizPurchaserVendor);
	}
	
	public Page<BizPurchaserVendor> findPage(Page<BizPurchaserVendor> page, BizPurchaserVendor bizPurchaserVendor) {
		return super.findPage(page, bizPurchaserVendor);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPurchaserVendor bizPurchaserVendor) {
		super.save(bizPurchaserVendor);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPurchaserVendor bizPurchaserVendor) {
		super.delete(bizPurchaserVendor);
	}
	
}
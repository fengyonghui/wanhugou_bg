/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.sku.BizCustSku;
import com.wanhutong.backend.modules.biz.dao.sku.BizCustSkuDao;

/**
 * 采购商商品价格Service
 * @author ZhangTengfei
 * @version 2018-05-15
 */
@Service
@Transactional(readOnly = true)
public class BizCustSkuService extends CrudService<BizCustSkuDao, BizCustSku> {

	public BizCustSku get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCustSku> findList(BizCustSku bizCustSku) {
		return super.findList(bizCustSku);
	}
	
	public Page<BizCustSku> findPage(Page<BizCustSku> page, BizCustSku bizCustSku) {
		return super.findPage(page, bizCustSku);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCustSku bizCustSku) {
		super.save(bizCustSku);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCustSku bizCustSku) {
		super.delete(bizCustSku);
	}
	
}
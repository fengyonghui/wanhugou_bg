/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuPropValue;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuPropValueDao;

/**
 * sku属性Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizSkuPropValueService extends CrudService<BizSkuPropValueDao, BizSkuPropValue> {

	public BizSkuPropValue get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuPropValue> findList(BizSkuPropValue bizSkuPropValue) {
		return super.findList(bizSkuPropValue);
	}
	
	public Page<BizSkuPropValue> findPage(Page<BizSkuPropValue> page, BizSkuPropValue bizSkuPropValue) {
		return super.findPage(page, bizSkuPropValue);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuPropValue bizSkuPropValue) {
		super.save(bizSkuPropValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuPropValue bizSkuPropValue) {
		super.delete(bizSkuPropValue);
	}
	
}
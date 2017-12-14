/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.product.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.product.entity.BizProdPropValue;
import com.wanhutong.backend.modules.product.dao.BizProdPropValueDao;

/**
 * 记录产品所有属性值Service
 * @author zx
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class BizProdPropValueService extends CrudService<BizProdPropValueDao, BizProdPropValue> {

	public BizProdPropValue get(Integer id) {
		return super.get(id);
	}
	
	public List<BizProdPropValue> findList(BizProdPropValue bizProdPropValue) {
		return super.findList(bizProdPropValue);
	}
	
	public Page<BizProdPropValue> findPage(Page<BizProdPropValue> page, BizProdPropValue bizProdPropValue) {
		return super.findPage(page, bizProdPropValue);
	}
	
	@Transactional(readOnly = false)
	public void save(BizProdPropValue bizProdPropValue) {
		super.save(bizProdPropValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizProdPropValue bizProdPropValue) {
		super.delete(bizProdPropValue);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import com.wanhutong.backend.modules.biz.dao.product.BizProdPropValueDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProdPropValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;


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
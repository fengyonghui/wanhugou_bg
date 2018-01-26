/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderSkuPropValue;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderSkuPropValueDao;

/**
 * 订单详情商品属性Service
 * @author OuyangXiutian
 * @version 2018-01-25
 */
@Service
@Transactional(readOnly = true)
public class BizOrderSkuPropValueService extends CrudService<BizOrderSkuPropValueDao, BizOrderSkuPropValue> {

	public BizOrderSkuPropValue get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderSkuPropValue> findList(BizOrderSkuPropValue bizOrderSkuPropValue) {
		return super.findList(bizOrderSkuPropValue);
	}
	
	public Page<BizOrderSkuPropValue> findPage(Page<BizOrderSkuPropValue> page, BizOrderSkuPropValue bizOrderSkuPropValue) {
		return super.findPage(page, bizOrderSkuPropValue);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderSkuPropValue bizOrderSkuPropValue) {
		super.save(bizOrderSkuPropValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderSkuPropValue bizOrderSkuPropValue) {
		super.delete(bizOrderSkuPropValue);
	}
	
}
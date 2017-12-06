/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.dao.category.BizCatePropValueDao;

/**
 * 记录分类下所有属性值Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCatePropValueService extends CrudService<BizCatePropValueDao, BizCatePropValue> {

	public BizCatePropValue get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCatePropValue> findList(BizCatePropValue bizCatePropValue) {
		return super.findList(bizCatePropValue);
	}
	
	public Page<BizCatePropValue> findPage(Page<BizCatePropValue> page, BizCatePropValue bizCatePropValue) {
		return super.findPage(page, bizCatePropValue);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCatePropValue bizCatePropValue) {
		super.save(bizCatePropValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCatePropValue bizCatePropValue) {
		super.delete(bizCatePropValue);
	}
	
}
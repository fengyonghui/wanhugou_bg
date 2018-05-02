/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizVarietyFactor;
import com.wanhutong.backend.modules.biz.dao.shelf.BizVarietyFactorDao;

/**
 * 品类阶梯价Service
 * @author ZhangTengfei
 * @version 2018-04-27
 */
@Service
@Transactional(readOnly = true)
public class BizVarietyFactorService extends CrudService<BizVarietyFactorDao, BizVarietyFactor> {

	public BizVarietyFactor get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVarietyFactor> findList(BizVarietyFactor bizVarietyFactor) {
		return super.findList(bizVarietyFactor);
	}
	
	public Page<BizVarietyFactor> findPage(Page<BizVarietyFactor> page, BizVarietyFactor bizVarietyFactor) {
		return super.findPage(page, bizVarietyFactor);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVarietyFactor bizVarietyFactor) {
		super.save(bizVarietyFactor);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVarietyFactor bizVarietyFactor) {
		super.delete(bizVarietyFactor);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.dao.category.BizVarietyInfoDao;

/**
 * 产品种类管理Service
 * @author liuying
 * @version 2018-02-24
 */
@Service
@Transactional(readOnly = true)
public class BizVarietyInfoService extends CrudService<BizVarietyInfoDao, BizVarietyInfo> {

	@Autowired
	private BizVarietyInfoDao bizVarietyInfoDao;

	public BizVarietyInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVarietyInfo> findList(BizVarietyInfo bizVarietyInfo) {
		return super.findList(bizVarietyInfo);
	}
	
	public Page<BizVarietyInfo> findPage(Page<BizVarietyInfo> page, BizVarietyInfo bizVarietyInfo) {
		return super.findPage(page, bizVarietyInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVarietyInfo bizVarietyInfo) {
		super.save(bizVarietyInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVarietyInfo bizVarietyInfo) {
		super.delete(bizVarietyInfo);
	}

	public List<BizVarietyInfo> findNotSpecialList() {
		return bizVarietyInfoDao.findNotSpecialList();
	}
	
}
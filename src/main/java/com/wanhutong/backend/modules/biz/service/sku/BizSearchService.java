/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.sku.BizSearch;
import com.wanhutong.backend.modules.biz.dao.sku.BizSearchDao;

/**
 * 找货定制Service
 * @author liuying
 * @version 2018-01-20
 */
@Service
@Transactional(readOnly = true)
public class BizSearchService extends CrudService<BizSearchDao, BizSearch> {
	@Autowired
	private BizSearchDao bizSearchDao;

	public BizSearch get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSearch> findList(BizSearch bizSearch) {
		return super.findList(bizSearch);
	}
	
	public Page<BizSearch> findPage(Page<BizSearch> page, BizSearch bizSearch) {
		return super.findPage(page, bizSearch);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSearch bizSearch) {
		super.save(bizSearch);
	}
	@Transactional(readOnly = false)
	public void savePartNo(BizSearch bizSearch){
		bizSearchDao.updatePartNo(bizSearch);
	}
	@Transactional(readOnly = false)
	public void saveNone(BizSearch bizSearch){
		bizSearchDao.updatePartNo(bizSearch);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSearch bizSearch) {
		super.delete(bizSearch);
	}
	
}
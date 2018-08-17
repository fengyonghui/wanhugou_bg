/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestExpand;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestExpandDao;

/**
 * 备货单扩展Service
 * @author TengFei.zhang
 * @version 2018-07-26
 */
@Service
@Transactional(readOnly = true)
public class BizRequestExpandService extends CrudService<BizRequestExpandDao, BizRequestExpand> {

	public BizRequestExpand get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestExpand> findList(BizRequestExpand bizRequestExpand) {
		return super.findList(bizRequestExpand);
	}
	
	public Page<BizRequestExpand> findPage(Page<BizRequestExpand> page, BizRequestExpand bizRequestExpand) {
		return super.findPage(page, bizRequestExpand);
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestExpand bizRequestExpand) {
		super.save(bizRequestExpand);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestExpand bizRequestExpand) {
		super.delete(bizRequestExpand);
	}
	
}
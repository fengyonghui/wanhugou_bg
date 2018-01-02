/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.dao.po.BizPoHeaderDao;

/**
 * 采购订单表Service
 * @author liuying
 * @version 2017-12-30
 */
@Service
@Transactional(readOnly = true)
public class BizPoHeaderService extends CrudService<BizPoHeaderDao, BizPoHeader> {

	public BizPoHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPoHeader> findList(BizPoHeader bizPoHeader) {
		return super.findList(bizPoHeader);
	}
	
	public Page<BizPoHeader> findPage(Page<BizPoHeader> page, BizPoHeader bizPoHeader) {
		return super.findPage(page, bizPoHeader);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPoHeader bizPoHeader) {
		super.save(bizPoHeader);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPoHeader bizPoHeader) {
		super.delete(bizPoHeader);
	}
	
}
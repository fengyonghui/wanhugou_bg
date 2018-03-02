/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.vend;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendMark;
import com.wanhutong.backend.modules.biz.dao.vend.BizVendMarkDao;

/**
 * 供应商收藏Service
 * @author zx
 * @version 2018-03-02
 */
@Service
@Transactional(readOnly = true)
public class BizVendMarkService extends CrudService<BizVendMarkDao, BizVendMark> {

	public BizVendMark get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVendMark> findList(BizVendMark bizVendMark) {
		return super.findList(bizVendMark);
	}
	
	public Page<BizVendMark> findPage(Page<BizVendMark> page, BizVendMark bizVendMark) {
		return super.findPage(page, bizVendMark);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVendMark bizVendMark) {
		super.save(bizVendMark);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVendMark bizVendMark) {
		super.delete(bizVendMark);
	}
	
}
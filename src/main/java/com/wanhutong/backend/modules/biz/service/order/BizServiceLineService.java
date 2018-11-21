/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;
import com.wanhutong.backend.modules.biz.dao.order.BizServiceLineDao;

/**
 * 服务费物流线路Service
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Service
@Transactional(readOnly = true)
public class BizServiceLineService extends CrudService<BizServiceLineDao, BizServiceLine> {

	public BizServiceLine get(Integer id) {
		return super.get(id);
	}
	
	public List<BizServiceLine> findList(BizServiceLine bizServiceLine) {
		return super.findList(bizServiceLine);
	}
	
	public Page<BizServiceLine> findPage(Page<BizServiceLine> page, BizServiceLine bizServiceLine) {
		return super.findPage(page, bizServiceLine);
	}
	
	@Transactional(readOnly = false)
	public void save(BizServiceLine bizServiceLine) {
		super.save(bizServiceLine);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizServiceLine bizServiceLine) {
		super.delete(bizServiceLine);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderTotalexp;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderTotalexpDao;

/**
 * 服务费记录表Service
 * @author wangby
 * @version 2018-12-11
 */
@Service
@Transactional(readOnly = true)
public class BizOrderTotalexpService extends CrudService<BizOrderTotalexpDao, BizOrderTotalexp> {

	public BizOrderTotalexp get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderTotalexp> findList(BizOrderTotalexp bizOrderTotalexp) {
		return super.findList(bizOrderTotalexp);
	}
	
	public Page<BizOrderTotalexp> findPage(Page<BizOrderTotalexp> page, BizOrderTotalexp bizOrderTotalexp) {
		return super.findPage(page, bizOrderTotalexp);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderTotalexp bizOrderTotalexp) {
		super.save(bizOrderTotalexp);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderTotalexp bizOrderTotalexp) {
		super.delete(bizOrderTotalexp);
	}


}
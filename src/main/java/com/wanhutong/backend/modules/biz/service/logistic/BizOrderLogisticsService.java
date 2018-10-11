/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.logistic;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.logistic.BizOrderLogistics;
import com.wanhutong.backend.modules.biz.dao.logistic.BizOrderLogisticsDao;

/**
 * 运单配置Service
 * @author wangby
 * @version 2018-09-26
 */
@Service
@Transactional(readOnly = true)
public class BizOrderLogisticsService extends CrudService<BizOrderLogisticsDao, BizOrderLogistics> {

	public BizOrderLogistics get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderLogistics> findList(BizOrderLogistics bizOrderLogistics) {
		return super.findList(bizOrderLogistics);
	}
	
	public Page<BizOrderLogistics> findPage(Page<BizOrderLogistics> page, BizOrderLogistics bizOrderLogistics) {
		return super.findPage(page, bizOrderLogistics);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderLogistics bizOrderLogistics) {
		super.save(bizOrderLogistics);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderLogistics bizOrderLogistics) {
		super.delete(bizOrderLogistics);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.plan;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
import com.wanhutong.backend.modules.biz.dao.plan.BizOpPlanDao;

/**
 * 运营计划Service
 * @author 张腾飞
 * @version 2018-03-15
 */
@Service
@Transactional(readOnly = true)
public class BizOpPlanService extends CrudService<BizOpPlanDao, BizOpPlan> {

	public BizOpPlan get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOpPlan> findList(BizOpPlan bizOpPlan) {
		return super.findList(bizOpPlan);
	}
	
	public Page<BizOpPlan> findPage(Page<BizOpPlan> page, BizOpPlan bizOpPlan) {
		return super.findPage(page, bizOpPlan);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOpPlan bizOpPlan) {
		super.save(bizOpPlan);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOpPlan bizOpPlan) {
		super.delete(bizOpPlan);
	}
	
}
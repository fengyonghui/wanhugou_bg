/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import com.wanhutong.backend.modules.biz.dao.po.BizSchedulingPlanDao;

/**
 * 排产计划Service
 * @author 王冰洋
 * @version 2018-07-17
 */
@Service
@Transactional(readOnly = true)
public class BizSchedulingPlanService extends CrudService<BizSchedulingPlanDao, BizSchedulingPlan> {

	public BizSchedulingPlan get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSchedulingPlan> findList(BizSchedulingPlan bizSchedulingPlan) {
		return super.findList(bizSchedulingPlan);
	}
	
	public Page<BizSchedulingPlan> findPage(Page<BizSchedulingPlan> page, BizSchedulingPlan bizSchedulingPlan) {
		return super.findPage(page, bizSchedulingPlan);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSchedulingPlan bizSchedulingPlan) {
		super.save(bizSchedulingPlan);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSchedulingPlan bizSchedulingPlan) {
		super.delete(bizSchedulingPlan);
	}
	
}
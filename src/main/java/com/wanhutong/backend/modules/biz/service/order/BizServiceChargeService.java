/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceCharge;
import com.wanhutong.backend.modules.biz.dao.order.BizServiceChargeDao;

/**
 * 服务费--配送方式Service
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Service
@Transactional(readOnly = true)
public class BizServiceChargeService extends CrudService<BizServiceChargeDao, BizServiceCharge> {

	public BizServiceCharge get(Integer id) {
		return super.get(id);
	}
	
	public List<BizServiceCharge> findList(BizServiceCharge bizServiceCharge) {
		return super.findList(bizServiceCharge);
	}
	
	public Page<BizServiceCharge> findPage(Page<BizServiceCharge> page, BizServiceCharge bizServiceCharge) {
		return super.findPage(page, bizServiceCharge);
	}
	
	@Transactional(readOnly = false)
	public void save(BizServiceCharge bizServiceCharge) {
		super.save(bizServiceCharge);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizServiceCharge bizServiceCharge) {
		super.delete(bizServiceCharge);
	}
	
}
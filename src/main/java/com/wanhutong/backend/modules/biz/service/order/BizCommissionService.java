/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.dao.order.BizCommissionDao;

/**
 * 佣金付款表Service
 * @author wangby
 * @version 2018-10-18
 */
@Service
@Transactional(readOnly = true)
public class BizCommissionService extends CrudService<BizCommissionDao, BizCommission> {

	public BizCommission get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCommission> findList(BizCommission bizCommission) {
		return super.findList(bizCommission);
	}
	
	public Page<BizCommission> findPage(Page<BizCommission> page, BizCommission bizCommission) {
		return super.findPage(page, bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCommission bizCommission) {
		super.save(bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCommission bizCommission) {
		super.delete(bizCommission);
	}
	
}
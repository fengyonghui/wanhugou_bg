/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizCommissionOrder;
import com.wanhutong.backend.modules.biz.dao.order.BizCommissionOrderDao;

/**
 * 佣金付款订单关系表Service
 * @author wangby
 * @version 2018-10-18
 */
@Service
@Transactional(readOnly = true)
public class BizCommissionOrderService extends CrudService<BizCommissionOrderDao, BizCommissionOrder> {

	public BizCommissionOrder get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCommissionOrder> findList(BizCommissionOrder bizCommissionOrder) {
		return super.findList(bizCommissionOrder);
	}
	
	public Page<BizCommissionOrder> findPage(Page<BizCommissionOrder> page, BizCommissionOrder bizCommissionOrder) {
		return super.findPage(page, bizCommissionOrder);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCommissionOrder bizCommissionOrder) {
		super.save(bizCommissionOrder);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCommissionOrder bizCommissionOrder) {
		super.delete(bizCommissionOrder);
	}

	public List<BizCommissionOrder> findListForCommonProcess(BizCommissionOrder bizCommissionOrder) {
		return dao.findListForCommonProcess(bizCommissionOrder);
	}
}
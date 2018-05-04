/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.dao.po.BizPoPaymentOrderDao;

/**
 * 采购付款单Service
 * @author Ma.Qiang
 * @version 2018-05-04
 */
@Service
@Transactional(readOnly = true)
public class BizPoPaymentOrderService extends CrudService<BizPoPaymentOrderDao, BizPoPaymentOrder> {
	@Override
	public BizPoPaymentOrder get(Integer id) {
		return super.get(id);
	}
	@Override
	public List<BizPoPaymentOrder> findList(BizPoPaymentOrder bizPoPaymentOrder) {
		return super.findList(bizPoPaymentOrder);
	}
	@Override
	public Page<BizPoPaymentOrder> findPage(Page<BizPoPaymentOrder> page, BizPoPaymentOrder bizPoPaymentOrder) {
		return super.findPage(page, bizPoPaymentOrder);
	}
	@Override
	@Transactional(readOnly = false)
	public void save(BizPoPaymentOrder bizPoPaymentOrder) {
		super.save(bizPoPaymentOrder);
	}
	
	@Transactional(readOnly = false)
	@Override
	public void delete(BizPoPaymentOrder bizPoPaymentOrder) {
		super.delete(bizPoPaymentOrder);
	}

	/**
	 * 更新流程ID
	 * @param headerId
	 * @param processId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateProcessId(int headerId, int processId) {
		return dao.updateProcessId(headerId, processId);
	}
}
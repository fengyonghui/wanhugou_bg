/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderStatusDao;

/**
 * 订单状态修改日志Service
 * @author Oy
 * @version 2018-05-15
 */
@Service
@Transactional(readOnly = true)
public class BizOrderStatusService extends CrudService<BizOrderStatusDao, BizOrderStatus> {

	public BizOrderStatus get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderStatus> findList(BizOrderStatus bizOrderStatus) {
		return super.findList(bizOrderStatus);
	}
	
	public Page<BizOrderStatus> findPage(Page<BizOrderStatus> page, BizOrderStatus bizOrderStatus) {
		return super.findPage(page, bizOrderStatus);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderStatus bizOrderStatus) {
		super.save(bizOrderStatus);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderStatus bizOrderStatus) {
		super.delete(bizOrderStatus);
	}
	
}
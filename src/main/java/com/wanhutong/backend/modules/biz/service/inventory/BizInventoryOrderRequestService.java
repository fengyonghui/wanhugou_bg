/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventoryOrderRequestDao;

/**
 * 订单备货单出库关系Service
 * @author ZhangTengfei
 * @version 2018-09-20
 */
@Service
@Transactional(readOnly = true)
public class BizInventoryOrderRequestService extends CrudService<BizInventoryOrderRequestDao, BizInventoryOrderRequest> {

	public BizInventoryOrderRequest get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInventoryOrderRequest> findList(BizInventoryOrderRequest bizInventoryOrderRequest) {
		return super.findList(bizInventoryOrderRequest);
	}
	
	public Page<BizInventoryOrderRequest> findPage(Page<BizInventoryOrderRequest> page, BizInventoryOrderRequest bizInventoryOrderRequest) {
		return super.findPage(page, bizInventoryOrderRequest);
	}
	
	@Transactional(readOnly = false)
	public void save(BizInventoryOrderRequest bizInventoryOrderRequest) {
		super.save(bizInventoryOrderRequest);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInventoryOrderRequest bizInventoryOrderRequest) {
		super.delete(bizInventoryOrderRequest);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;

/**
 * 订单详情(销售订单)Service
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Service
@Transactional(readOnly = true)
public class BizOrderDetailService extends CrudService<BizOrderDetailDao, BizOrderDetail> {

	public BizOrderDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderDetail> findList(BizOrderDetail bizOrderDetail) {
		return super.findList(bizOrderDetail);
	}
	
	public Page<BizOrderDetail> findPage(Page<BizOrderDetail> page, BizOrderDetail bizOrderDetail) {
		return super.findPage(page, bizOrderDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderDetail bizOrderDetail) {
		super.save(bizOrderDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderDetail bizOrderDetail) {
		super.delete(bizOrderDetail);
	}
	
}
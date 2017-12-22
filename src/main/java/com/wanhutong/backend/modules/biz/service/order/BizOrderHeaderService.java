/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;

import static com.wanhutong.backend.common.utils.GenerateOrderUtils.getOrderNum;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {

	public BizOrderHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderHeader> findList(BizOrderHeader bizOrderHeader) {
		return super.findList(bizOrderHeader);
	}
	
	public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
		return super.findPage(page, bizOrderHeader);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderHeader bizOrderHeader) {
//		GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()),bizOrderHeader.getCustId().getId());
		String orderNum=GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()),bizOrderHeader.getCustomer().getId());
		bizOrderHeader.setOrderNum(orderNum);
		super.save(bizOrderHeader);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderHeader bizOrderHeader) {
		super.delete(bizOrderHeader);
	}

}
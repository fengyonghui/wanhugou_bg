/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderUnlineDao;

/**
 * 线下支付订单(线下独有)Service
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderUnlineService extends CrudService<BizOrderHeaderUnlineDao, BizOrderHeaderUnline> {

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	public BizOrderHeaderUnline get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderHeaderUnline> findList(BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findList(bizOrderHeaderUnline);
	}
	
	public Page<BizOrderHeaderUnline> findPage(Page<BizOrderHeaderUnline> page, BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findPage(page, bizOrderHeaderUnline);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.save(bizOrderHeaderUnline);
	}

	@Transactional(readOnly = false)
	public void delete(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.delete(bizOrderHeaderUnline);
	}
	
}
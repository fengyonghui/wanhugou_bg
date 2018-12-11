/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.util.List;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderTotalexp;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderTotalexpDao;

/**
 * 服务费记录表Service
 * @author wangby
 * @version 2018-12-11
 */
@Service
@Transactional(readOnly = true)
public class BizOrderTotalexpService extends CrudService<BizOrderTotalexpDao, BizOrderTotalexp> {

	private static final Integer ORDER_TOTAL_EXP = 1;

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	public BizOrderTotalexp get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderTotalexp> findList(BizOrderTotalexp bizOrderTotalexp) {
		return super.findList(bizOrderTotalexp);
	}
	
	public Page<BizOrderTotalexp> findPage(Page<BizOrderTotalexp> page, BizOrderTotalexp bizOrderTotalexp) {
		return super.findPage(page, bizOrderTotalexp);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderTotalexp bizOrderTotalexp) {
		super.save(bizOrderTotalexp);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderTotalexp bizOrderTotalexp) {
		super.delete(bizOrderTotalexp);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void insertBatch(List<String> amountList, Integer orderId) {
		User user = UserUtils.getUser();
		dao.insertBatch(amountList, orderId, user.getId(), ORDER_TOTAL_EXP);

		BigDecimal totalAmount = BigDecimal.ZERO;
		for (String amount : amountList) {
			totalAmount = totalAmount.add(BigDecimal.valueOf(Long.parseLong(amount))).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderId);
		Double exp = bizOrderHeader.getTotalExp();
		BigDecimal totalExp = BigDecimal.valueOf(exp).add(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
		bizOrderHeader.setTotalExp(totalExp.doubleValue());
		bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
	}

}
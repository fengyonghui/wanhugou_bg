/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAppointedTime;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderAppointedTimeDao;

/**
 * 代采订单约定付款时间表Service
 * @author ZhangTengfei
 * @version 2018-05-30
 */
@Service
@Transactional(readOnly = true)
public class BizOrderAppointedTimeService extends CrudService<BizOrderAppointedTimeDao, BizOrderAppointedTime> {

	public BizOrderAppointedTime get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderAppointedTime> findList(BizOrderAppointedTime bizOrderAppointedTime) {
		return super.findList(bizOrderAppointedTime);
	}
	
	public Page<BizOrderAppointedTime> findPage(Page<BizOrderAppointedTime> page, BizOrderAppointedTime bizOrderAppointedTime) {
		return super.findPage(page, bizOrderAppointedTime);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderAppointedTime bizOrderAppointedTime) {
		super.save(bizOrderAppointedTime);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderAppointedTime bizOrderAppointedTime) {
		super.delete(bizOrderAppointedTime);
	}
	
}
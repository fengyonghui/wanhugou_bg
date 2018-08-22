/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.dao.request.BizPoOrderReqDao;

/**
 * 销售采购备货中间表Service
 * @author 张腾飞
 * @version 2018-01-09
 */
@Service
@Transactional(readOnly = true)
public class BizPoOrderReqService extends CrudService<BizPoOrderReqDao, BizPoOrderReq> {

	@Autowired
	private BizPoOrderReqDao bizPoOrderReqDao;

	public BizPoOrderReq get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPoOrderReq> findList(BizPoOrderReq bizPoOrderReq) {
		return super.findList(bizPoOrderReq);
	}
	
	public Page<BizPoOrderReq> findPage(Page<BizPoOrderReq> page, BizPoOrderReq bizPoOrderReq) {
		return super.findPage(page, bizPoOrderReq);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPoOrderReq bizPoOrderReq) {
		super.save(bizPoOrderReq);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPoOrderReq bizPoOrderReq) {
		super.delete(bizPoOrderReq);
	}

	/**
	 * 通过po单子获取销售采购备货中间表
	 * @param bphId
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<BizPoOrderReq> getByPo(Integer bphId){
		return bizPoOrderReqDao.getByPo(bphId);
	}
}
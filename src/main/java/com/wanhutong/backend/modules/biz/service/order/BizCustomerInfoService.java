/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizCustomerInfo;
import com.wanhutong.backend.modules.biz.dao.order.BizCustomerInfoDao;

/**
 * 机构信息entityService
 * @author wangby
 * @version 2018-10-22
 */
@Service
@Transactional(readOnly = true)
public class BizCustomerInfoService extends CrudService<BizCustomerInfoDao, BizCustomerInfo> {

	public BizCustomerInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCustomerInfo> findList(BizCustomerInfo bizCustomerInfo) {
		return super.findList(bizCustomerInfo);
	}
	
	public Page<BizCustomerInfo> findPage(Page<BizCustomerInfo> page, BizCustomerInfo bizCustomerInfo) {
		return super.findPage(page, bizCustomerInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCustomerInfo bizCustomerInfo) {
		super.save(bizCustomerInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCustomerInfo bizCustomerInfo) {
		super.delete(bizCustomerInfo);
	}
	
}
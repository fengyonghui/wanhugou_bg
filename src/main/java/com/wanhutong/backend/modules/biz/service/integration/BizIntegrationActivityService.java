/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.integration;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.dao.integration.BizIntegrationActivityDao;

import javax.annotation.Resource;

/**
 * 积分活动Service
 * @author LX
 * @version 2018-09-16
 */
@Service
@Transactional(readOnly = true)
public class BizIntegrationActivityService extends CrudService<BizIntegrationActivityDao, BizIntegrationActivity> {
    @Resource
	private BizIntegrationActivityDao bizIntegrationActivityDao;

	public BizIntegrationActivity get(Integer id) {
		return super.get(id);
	}
	
	public List<BizIntegrationActivity> findList(BizIntegrationActivity bizIntegrationActivity) {
		return super.findList(bizIntegrationActivity);
	}
	
	public Page<BizIntegrationActivity> findPage(Page<BizIntegrationActivity> page, BizIntegrationActivity bizIntegrationActivity) {
		return super.findPage(page, bizIntegrationActivity);
	}
	
	@Transactional(readOnly = false)
	public void save(BizIntegrationActivity bizIntegrationActivity) {
		super.save(bizIntegrationActivity);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizIntegrationActivity bizIntegrationActivity) {
		super.delete(bizIntegrationActivity);
	}

	public BizIntegrationActivity getIntegrationByCode(String code){
         return bizIntegrationActivityDao.getIntegrationByCode(code);
	}
	
}
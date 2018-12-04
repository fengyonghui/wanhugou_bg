/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizFreightConfig;
import com.wanhutong.backend.modules.biz.dao.order.BizFreightConfigDao;

/**
 * 服务费设置Service
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
@Service
@Transactional(readOnly = true)
public class BizFreightConfigService extends CrudService<BizFreightConfigDao, BizFreightConfig> {

	public BizFreightConfig get(Integer id) {
		return super.get(id);
	}
	
	public List<BizFreightConfig> findList(BizFreightConfig bizFreightConfig) {
		return super.findList(bizFreightConfig);
	}
	
	public Page<BizFreightConfig> findPage(Page<BizFreightConfig> page, BizFreightConfig bizFreightConfig) {
		return super.findPage(page, bizFreightConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(BizFreightConfig bizFreightConfig) {
		super.save(bizFreightConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizFreightConfig bizFreightConfig) {
		super.delete(bizFreightConfig);
	}
	
}
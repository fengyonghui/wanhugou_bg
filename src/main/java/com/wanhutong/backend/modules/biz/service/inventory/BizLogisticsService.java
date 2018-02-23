/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.dao.inventory.BizLogisticsDao;

/**
 * 物流商类Service
 * @author 张腾飞
 * @version 2018-02-21
 */
@Service
@Transactional(readOnly = true)
public class BizLogisticsService extends CrudService<BizLogisticsDao, BizLogistics> {

	public BizLogistics get(Integer id) {
		return super.get(id);
	}
	
	public List<BizLogistics> findList(BizLogistics bizLogistics) {
		return super.findList(bizLogistics);
	}
	
	public Page<BizLogistics> findPage(Page<BizLogistics> page, BizLogistics bizLogistics) {
		return super.findPage(page, bizLogistics);
	}
	
	@Transactional(readOnly = false)
	public void save(BizLogistics bizLogistics) {
		super.save(bizLogistics);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizLogistics bizLogistics) {
		super.delete(bizLogistics);
	}
	
}
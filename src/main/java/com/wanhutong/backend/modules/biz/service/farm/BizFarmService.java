/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.farm;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.farm.BizFarm;
import com.wanhutong.backend.modules.biz.dao.farm.BizFarmDao;

/**
 * 单表生成Service
 * @author ThinkGem
 * @version 2016-07-08
 */
@Service
@Transactional(readOnly = true)
public class BizFarmService extends CrudService<BizFarmDao, BizFarm> {

	public BizFarm get(Integer id) {
		return super.get(id);
	}
	
	public List<BizFarm> findList(BizFarm bizFarm) {
		return super.findList(bizFarm);
	}
	
	public Page<BizFarm> findPage(Page<BizFarm> page, BizFarm bizFarm) {
		return super.findPage(page, bizFarm);
	}
	
	@Transactional(readOnly = false)
	public void save(BizFarm bizFarm) {
		super.save(bizFarm);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizFarm bizFarm) {
		super.delete(bizFarm);
	}
	
}
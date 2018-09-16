/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.integration;

import java.util.List;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.integration.BizMoneyRecodeDao;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 积分流水Service
 * @author LX
 * @version 2018-09-16
 */
@Service
@Transactional(readOnly = true)
public class BizMoneyRecodeService extends CrudService<BizMoneyRecodeDao, BizMoneyRecode> {

	public BizMoneyRecode get(Integer id) {
		return super.get(id);
	}
	
	public List<BizMoneyRecode> findList(BizMoneyRecode bizMoneyRecode) {
		return super.findList(bizMoneyRecode);
	}
	
	public Page<BizMoneyRecode> findPage(Page<BizMoneyRecode> page, BizMoneyRecode bizMoneyRecode) {
		return super.findPage(page, bizMoneyRecode);
	}
	
	@Transactional(readOnly = false)
	public void save(BizMoneyRecode bizMoneyRecode) {
		super.save(bizMoneyRecode);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizMoneyRecode bizMoneyRecode) {
		super.delete(bizMoneyRecode);
	}
	
}
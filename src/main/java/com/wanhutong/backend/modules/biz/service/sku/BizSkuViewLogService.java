/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuViewLog;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuViewLogDao;

/**
 * 商品出厂价日志表Service
 * @author Oy
 * @version 2018-04-23
 */
@Service
@Transactional(readOnly = true)
public class BizSkuViewLogService extends CrudService<BizSkuViewLogDao, BizSkuViewLog> {

	public BizSkuViewLog get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuViewLog> findList(BizSkuViewLog bizSkuViewLog) {
		return super.findList(bizSkuViewLog);
	}
	
	public Page<BizSkuViewLog> findPage(Page<BizSkuViewLog> page, BizSkuViewLog bizSkuViewLog) {
		return super.findPage(page, bizSkuViewLog);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuViewLog bizSkuViewLog) {
		super.save(bizSkuViewLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuViewLog bizSkuViewLog) {
		super.delete(bizSkuViewLog);
	}
	
}
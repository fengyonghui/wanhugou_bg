/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProdViewLog;
import com.wanhutong.backend.modules.biz.dao.product.BizProdViewLogDao;

/**
 * 产品查看日志Service
 * @author zx
 * @version 2018-02-22
 */
@Service
@Transactional(readOnly = true)
public class BizProdViewLogService extends CrudService<BizProdViewLogDao, BizProdViewLog> {

	public BizProdViewLog get(Integer id) {
		return super.get(id);
	}
	
	public List<BizProdViewLog> findList(BizProdViewLog bizProdViewLog) {
		return super.findList(bizProdViewLog);
	}
	
	public Page<BizProdViewLog> findPage(Page<BizProdViewLog> page, BizProdViewLog bizProdViewLog) {
		return super.findPage(page, bizProdViewLog);
	}
	
	@Transactional(readOnly = false)
	public void save(BizProdViewLog bizProdViewLog) {
		super.save(bizProdViewLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizProdViewLog bizProdViewLog) {
		super.delete(bizProdViewLog);
	}
	
}
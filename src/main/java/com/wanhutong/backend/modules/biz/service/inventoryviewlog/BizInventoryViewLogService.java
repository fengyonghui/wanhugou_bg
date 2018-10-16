/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventoryviewlog;

import java.util.List;

import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.dao.inventoryviewlog.BizInventoryViewLogDao;

/**
 * 库存盘点记录Service
 * @author zx
 * @version 2018-03-27
 */
@Service
@Transactional(readOnly = true)
public class BizInventoryViewLogService extends CrudService<BizInventoryViewLogDao, BizInventoryViewLog> {

	public BizInventoryViewLog get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInventoryViewLog> findList(BizInventoryViewLog bizInventoryViewLog) {
		return super.findList(bizInventoryViewLog);
	}
	
	public Page<BizInventoryViewLog> findPage(Page<BizInventoryViewLog> page, BizInventoryViewLog bizInventoryViewLog) {
		User user = UserUtils.getUser();
		bizInventoryViewLog.getSqlMap().put("viewLog",dataScopeFilter(user,"cent","u"));
		return super.findPage(page, bizInventoryViewLog);
	}
	
	@Transactional(readOnly = false)
	public void save(BizInventoryViewLog bizInventoryViewLog) {
		super.save(bizInventoryViewLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInventoryViewLog bizInventoryViewLog) {
		super.delete(bizInventoryViewLog);
	}
	
}
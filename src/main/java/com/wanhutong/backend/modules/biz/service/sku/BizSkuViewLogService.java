/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.sku;

import java.util.List;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
		User user = UserUtils.getUser();
		List<String> enNameList = Lists.newArrayList();
		List<Role> roleList = user.getRoleList();
		for (Role role : roleList) {
			enNameList.add(role.getEnname());
		}
		if (enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
			bizSkuViewLog.getSqlMap().put("skuType", BaseService.dataScopeFilter(user, "vend", "su"));
		}
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
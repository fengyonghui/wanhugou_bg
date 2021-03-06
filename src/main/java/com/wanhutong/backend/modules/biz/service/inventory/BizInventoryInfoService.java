/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventoryInfoDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仓库信息表Service
 * @author zhangtengfei
 * @version 2017-12-28
 */
@Service
@Transactional(readOnly = true)
public class BizInventoryInfoService extends CrudService<BizInventoryInfoDao, BizInventoryInfo> {

	@Autowired
	private CommonLocationService commonLocationService;

	public BizInventoryInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInventoryInfo> findList(BizInventoryInfo bizInventoryInfo) {
		User user =UserUtils.getUser();
		if (!user.isAdmin()) {
			bizInventoryInfo.getSqlMap().put("inventory", BaseService.dataScopeFilter(user, "o", "su"));
		}
		return super.findList(bizInventoryInfo);
	}

	public List<BizInventoryInfo> findAllList(BizInventoryInfo bizInventoryInfo) {
		return super.findList(bizInventoryInfo);
	}
	
	public Page<BizInventoryInfo> findPage(Page<BizInventoryInfo> page, BizInventoryInfo bizInventoryInfo) {
		User user =UserUtils.getUser();
		if (user.isAdmin()) {
			return super.findPage(page, bizInventoryInfo);
		} else {
			bizInventoryInfo.getSqlMap().put("inventory", BaseService.dataScopeFilter(user, "o", "su"));
			return super.findPage(page, bizInventoryInfo);
		}
	}
	public BizInventoryInfo findName(String name){
		BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
		bizInventoryInfo.setName(name);
		return super.get(bizInventoryInfo);
	}
	@Transactional(readOnly = false)
	public void save(BizInventoryInfo bizInventoryInfo) {
		if(bizInventoryInfo.getBizLocation()!=null){
			commonLocationService.updateCommonLocation(bizInventoryInfo.getBizLocation());
		}
		super.save(bizInventoryInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInventoryInfo bizInventoryInfo) {
		super.delete(bizInventoryInfo);
	}

	public BizInventoryInfo getInventoryByCustId(Integer centId) {
		return dao.getInventoryByCustId(centId);
	}
}
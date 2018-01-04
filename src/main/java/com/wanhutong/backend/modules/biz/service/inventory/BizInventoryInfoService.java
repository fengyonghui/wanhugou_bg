/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventoryInfoDao;

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
		return super.findList(bizInventoryInfo);
	}
	
	public Page<BizInventoryInfo> findPage(Page<BizInventoryInfo> page, BizInventoryInfo bizInventoryInfo) {
		return super.findPage(page, bizInventoryInfo);
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
	
}
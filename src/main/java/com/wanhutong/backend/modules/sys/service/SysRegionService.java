/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.dao.SysRegionDao;

/**
 * 角色区域表Service
 * @author zx
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class SysRegionService extends CrudService<SysRegionDao, SysRegion> {

	public SysRegion get(Integer id) {
		return super.get(id);
	}
	
	public List<SysRegion> findList(SysRegion sysRegion) {
		return super.findList(sysRegion);
	}
	
	public Page<SysRegion> findPage(Page<SysRegion> page, SysRegion sysRegion) {
		return super.findPage(page, sysRegion);
	}
	
	@Transactional(readOnly = false)
	public void save(SysRegion sysRegion) {
		super.save(sysRegion);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysRegion sysRegion) {
		super.delete(sysRegion);
	}
	
}
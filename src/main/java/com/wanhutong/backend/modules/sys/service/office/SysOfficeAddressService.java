/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.office;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;
import com.wanhutong.backend.modules.sys.dao.office.SysOfficeAddressDao;

/**
 * 地址管理(公司+详细地址)Service
 * @author OuyangXiutian
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class SysOfficeAddressService extends CrudService<SysOfficeAddressDao, SysOfficeAddress> {
	@Autowired
	private CommonLocationService commonLocationService;

	public SysOfficeAddress get(Integer id) {
		return super.get(id);
	}
	
	public List<SysOfficeAddress> findList(SysOfficeAddress sysOfficeAddress) {
		return super.findList(sysOfficeAddress);
	}
	
	public Page<SysOfficeAddress> findPage(Page<SysOfficeAddress> page, SysOfficeAddress sysOfficeAddress) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, sysOfficeAddress);
		}else {
			sysOfficeAddress.getSqlMap().put("officeAddress", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, sysOfficeAddress);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(SysOfficeAddress sysOfficeAddress) {
		if(sysOfficeAddress.getBizLocation()!=null){
			commonLocationService.updateCommonLocation(sysOfficeAddress.getBizLocation());
		}
		super.save(sysOfficeAddress);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysOfficeAddress sysOfficeAddress) {
		super.delete(sysOfficeAddress);
	}
	
}
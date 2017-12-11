/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.dao.PropertyInfoDao;

/**
 * 系统属性Service
 * @author liuying
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class PropertyInfoService extends CrudService<PropertyInfoDao, PropertyInfo> {

	public PropertyInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<PropertyInfo> findList(PropertyInfo propertyInfo) {
		return super.findList(propertyInfo);
	}
	
	public Page<PropertyInfo> findPage(Page<PropertyInfo> page, PropertyInfo propertyInfo) {
		return super.findPage(page, propertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(PropertyInfo propertyInfo) {
		super.save(propertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(PropertyInfo propertyInfo) {
		super.delete(propertyInfo);
	}
	
}
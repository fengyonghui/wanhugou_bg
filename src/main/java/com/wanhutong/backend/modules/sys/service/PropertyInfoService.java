/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;


import com.wanhutong.backend.modules.sys.entity.PropValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.PropertyInfo;
import com.wanhutong.backend.modules.sys.dao.PropertyInfoDao;

import javax.annotation.Resource;

/**
 * 系统属性Service
 * @author liuying
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class PropertyInfoService extends CrudService<PropertyInfoDao, PropertyInfo> {
	@Resource
	private PropValueService propValueService;

	public PropertyInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<PropertyInfo> findList(PropertyInfo propertyInfo) {
		List<PropertyInfo> list= super.findList(propertyInfo);
		PropValue propValue=new PropValue();
		for(PropertyInfo info:list){
			propValue.setId(null);
			propValue.setPropertyInfo(info);
			List<PropValue> valueList=propValueService.findList(propValue);
			info.setPropValueList(valueList);
		}
		
		return list;
	}
	
	public Page<PropertyInfo> findPage(Page<PropertyInfo> page, PropertyInfo propertyInfo) {
		return super.findPage(page, propertyInfo);
	}
	
	/* (non-Javadoc)
	 * @see com.wanhutong.backend.common.service.CrudService#save(com.wanhutong.backend.common.persistence.DataEntity)
	 */
	@Transactional(readOnly = false)
	public void save(PropertyInfo propertyInfo) {
	List<PropValue> propValueList=propertyInfo.getPropValueList();
	super.save(propertyInfo);
	for(PropValue propValue:propValueList) {
		
		propValue.setPropertyInfo(propertyInfo);
		propValueService.save(propValue);

	}
		
	}
	
	@Transactional(readOnly = false)
	public void delete(PropertyInfo propertyInfo) {
		List<PropValue> propValueList = propertyInfo.getPropValueList();
		super.delete(propertyInfo);
	}
	
}
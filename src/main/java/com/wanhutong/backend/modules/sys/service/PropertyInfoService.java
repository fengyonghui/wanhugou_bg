/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.utils.HanyuPinyinHelper;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import org.omg.PortableInterceptor.INACTIVE;
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
		return list;
	}
	public Map<Integer,List<PropValue>> findMapList(PropertyInfo propertyInfo){
		PropValue propValue=new PropValue();
		if (propertyInfo.getPropValue() != null && propertyInfo.getPropValue().getValue() != null && "".equals(propertyInfo.getPropValue().getValue())){
			propValue.setValue(propertyInfo.getPropValue().getValue());
		}
		List<PropertyInfo> list=findList(propertyInfo);
		Map<Integer,List<PropValue>> map=new HashMap<Integer,List<PropValue>>();
		for(PropertyInfo info:list){
			propValue.setId(null);
			propValue.setPropertyInfo(info);
			List<PropValue> valueList=propValueService.findList(propValue);
			map.put(info.getId(),valueList);
		}

		return map;
	}

	public Page<PropertyInfo> findPage(Page<PropertyInfo> page, PropertyInfo propertyInfo) {
		return super.findPage(page, propertyInfo);
	}

	@Transactional(readOnly = false)
	public void save(PropertyInfo propertyInfo) {
	List<PropValue> propValueList=propertyInfo.getPropValueList();
	super.save(propertyInfo);
		for(PropValue propValue:propValueList) {
		propValue.setPropertyInfo(propertyInfo);
		String code=HanyuPinyinHelper.getFirstLetters(propValue.getValue(), HanyuPinyinCaseType.UPPERCASE);
		propValue.setCode(code);
		propValueService.save(propValue);

	}
		
	}
	
	@Transactional(readOnly = false)
	public void delete(PropertyInfo propertyInfo) {
		List<PropValue> propValueList = propertyInfo.getPropValueList();
		super.delete(propertyInfo);
	}
	
}
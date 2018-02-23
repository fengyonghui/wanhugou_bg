/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.PropValue;
import com.wanhutong.backend.modules.sys.dao.PropValueDao;

/**
 * 系统属性值Service
 * @author liuying
 * @version 2017-12-11
 */
@Service
@Transactional(readOnly = true)
public class PropValueService extends CrudService<PropValueDao, PropValue> {
	@Autowired
	private PropValueDao propValueDao;

	public PropValue get(Integer id) {
		return super.get(id);
	}
	
	public List<PropValue> findList(PropValue propValue) {
		return super.findList(propValue);
	}
	
	public Page<PropValue> findPage(Page<PropValue> page, PropValue propValue) {
		return super.findPage(page, propValue);
	}
	
	@Transactional(readOnly = false)
	public void save(PropValue propValue) {
		super.save(propValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(PropValue propValue) {
		super.delete(propValue);
	}

	public List<PropValue> findPropValueList(PropValue propValue){
		return propValueDao.findPropValueList(propValue);
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.dao.DefaultPropDao;

/**
 * 系统属性默认表Service
 * @author liuying
 * @version 2017-12-14
 */
@Service
@Transactional(readOnly = true)
public class DefaultPropService extends CrudService<DefaultPropDao, DefaultProp> {

	public DefaultProp get(Integer id) {
		return super.get(id);
	}
	
	public List<DefaultProp> findList(DefaultProp defaultProp) {
		return super.findList(defaultProp);
	}
	
	public Page<DefaultProp> findPage(Page<DefaultProp> page, DefaultProp defaultProp) {
		return super.findPage(page, defaultProp);
	}
	
	@Transactional(readOnly = false)
	public void save(DefaultProp defaultProp) {
		super.save(defaultProp);
	}
	
	@Transactional(readOnly = false)
	public void delete(DefaultProp defaultProp) {
		super.delete(defaultProp);
	}
	
}
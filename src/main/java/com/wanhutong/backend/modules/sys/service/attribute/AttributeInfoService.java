/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.attribute;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfo;
import com.wanhutong.backend.modules.sys.dao.attribute.AttributeInfoDao;

/**
 * 标签属性Service
 * @author zx
 * @version 2018-03-21
 */
@Service
@Transactional(readOnly = true)
public class AttributeInfoService extends CrudService<AttributeInfoDao, AttributeInfo> {

	public AttributeInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<AttributeInfo> findList(AttributeInfo attributeInfo) {
		return super.findList(attributeInfo);
	}
	
	public Page<AttributeInfo> findPage(Page<AttributeInfo> page, AttributeInfo attributeInfo) {
		return super.findPage(page, attributeInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(AttributeInfo attributeInfo) {
		super.save(attributeInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(AttributeInfo attributeInfo) {
		super.delete(attributeInfo);
	}
	
}
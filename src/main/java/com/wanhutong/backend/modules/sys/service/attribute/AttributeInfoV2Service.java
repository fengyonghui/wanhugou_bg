/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.attribute;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.dao.attribute.AttributeInfoDao;
import com.wanhutong.backend.modules.sys.dao.attribute.AttributeInfoV2Dao;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfo;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签属性Service
 * @author zx
 * @version 2018-03-21
 */
@Service
@Transactional(readOnly = true)
public class AttributeInfoV2Service extends CrudService<AttributeInfoV2Dao, AttributeInfoV2> {

	public AttributeInfoV2 get(Integer id) {
		return super.get(id);
	}
	
	public List<AttributeInfoV2> findList(AttributeInfoV2 attributeInfo) {
		return super.findList(attributeInfo);
	}
	
	public Page<AttributeInfoV2> findPage(Page<AttributeInfoV2> page, AttributeInfoV2 attributeInfo) {
		return super.findPage(page, attributeInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(AttributeInfoV2 attributeInfo) {
		super.save(attributeInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(AttributeInfoV2 attributeInfo) {
		super.delete(attributeInfo);
	}
	
}
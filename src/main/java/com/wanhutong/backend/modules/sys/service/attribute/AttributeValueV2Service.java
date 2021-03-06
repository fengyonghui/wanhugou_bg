/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service.attribute;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV2Service;
import com.wanhutong.backend.modules.sys.dao.attribute.AttributeValueDao;
import com.wanhutong.backend.modules.sys.dao.attribute.AttributeValueV2Dao;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfo;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValue;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValueV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签属性值Service
 * @author zx
 * @version 2018-03-21
 */
@Service
@Transactional(readOnly = true)
public class AttributeValueV2Service extends CrudService<AttributeValueV2Dao, AttributeValueV2> {

	@Autowired
	private AttributeValueV2Dao attributeValueV2Dao;

	public AttributeValueV2 get(Integer id) {
		return super.get(id);
	}
	
	public List<AttributeValueV2> findList(AttributeValueV2 attributeValue) {
		return super.findList(attributeValue);
	}

	public List<AttributeValueV2> findSpecificList(AttributeValueV2 attributeValueV2){
		return attributeValueV2Dao.findSpecificList(attributeValueV2);
	}
	
	public Page<AttributeValueV2> findPage(Page<AttributeValueV2> page, AttributeValueV2 attributeValue) {
		return super.findPage(page, attributeValue);
	}
	
	@Transactional(readOnly = false)
	public void save(AttributeValueV2 attributeValue) {
		super.save(attributeValue);
	}
	
	@Transactional(readOnly = false)
	public void delete(AttributeValueV2 attributeValue) {
		super.delete(attributeValue);
	}

	/**
	 * 查找SKU的尺寸
	 * @param bizSkuInfo
	 * @return
	 */
	public AttributeValueV2 findSkuProp(BizSkuInfo bizSkuInfo,String prop) {
		AttributeValueV2 propValue = new AttributeValueV2();
		propValue.setObjectId(bizSkuInfo.getId());
		propValue.setObjectName(BizProductInfoV2Service.SKU_TABLE);
		AttributeInfoV2 attributeInfoV2 = new AttributeInfoV2();
		attributeInfoV2.setName(prop);
		propValue.setAttributeInfo(attributeInfoV2);
		return attributeValueV2Dao.findSkuSize(propValue);
	}

	/**
	 * 修改属性值
	 * @param id
	 * @param value
	 */
	@Transactional(readOnly = false)
	public void updateValue(Integer id, String value) {
		attributeValueV2Dao.updateValue(id,value);
	}
}
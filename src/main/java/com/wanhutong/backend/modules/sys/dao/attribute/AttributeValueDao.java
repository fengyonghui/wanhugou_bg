/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao.attribute;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeValue;

/**
 * 标签属性值DAO接口
 * @author zx
 * @version 2018-03-21
 */
@MyBatisDao
public interface AttributeValueDao extends CrudDao<AttributeValue> {
	
}
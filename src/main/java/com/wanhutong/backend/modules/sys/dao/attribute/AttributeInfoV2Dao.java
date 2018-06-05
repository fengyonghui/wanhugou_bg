/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao.attribute;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.attribute.AttributeInfoV2;

import java.util.List;

/**
 * 标签属性DAO接口
 * @author zx
 * @version 2018-03-21
 */
@MyBatisDao
public interface AttributeInfoV2Dao extends CrudDao<AttributeInfoV2> {

    List<AttributeInfoV2> findVList();
}
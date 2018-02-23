/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.PropValue;

import java.util.List;

/**
 * 系统属性值DAO接口
 * @author liuying
 * @version 2017-12-11
 */
@MyBatisDao
public interface PropValueDao extends CrudDao<PropValue> {

    List<PropValue> findPropValueList(PropValue propValue);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.product;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizVarietyAttr;

/**
 * 分类属性中间表DAO接口
 * @author ZhangTengfei
 * @version 2018-05-28
 */
@MyBatisDao
public interface BizVarietyAttrDao extends CrudDao<BizVarietyAttr> {
	
}
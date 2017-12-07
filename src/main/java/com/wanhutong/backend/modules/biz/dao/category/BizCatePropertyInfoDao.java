/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.category;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;

/**
 * 记录当前分类下的所有属性DAO接口
 * @author liuying
 * @version 2017-12-06
 */
@MyBatisDao
public interface BizCatePropertyInfoDao extends CrudDao<BizCatePropertyInfo> {
	
}
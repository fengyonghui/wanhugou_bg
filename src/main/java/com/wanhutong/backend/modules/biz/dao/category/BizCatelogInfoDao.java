/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.category;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;

/**
 * 目录分类表DAO接口
 * @author liuying
 * @version 2017-12-06
 */
@MyBatisDao
public interface BizCatelogInfoDao extends CrudDao<BizCatelogInfo> {
	
}
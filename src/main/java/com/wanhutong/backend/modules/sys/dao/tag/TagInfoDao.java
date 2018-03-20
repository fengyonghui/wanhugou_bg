/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao.tag;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.tag.TagInfo;

/**
 * 标签属性表DAO接口
 * @author zx
 * @version 2018-03-19
 */
@MyBatisDao
public interface TagInfoDao extends CrudDao<TagInfo> {
	
}
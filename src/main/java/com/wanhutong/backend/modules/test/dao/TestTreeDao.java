/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.test.dao;

import com.wanhutong.backend.common.persistence.TreeDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.test.entity.TestTree;

/**
 * 树结构生成DAO接口
 * @author ThinkGem
 * @version 2016-07-06
 */
@MyBatisDao
public interface TestTreeDao extends TreeDao<TestTree> {
	
}
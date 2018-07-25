/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;

/**
 * 确认排产表DAO接口
 * @author 王冰洋
 * @version 2018-07-24
 */
@MyBatisDao
public interface BizCompletePalnDao extends CrudDao<BizCompletePaln> {
	
}
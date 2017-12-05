/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.biz.dao.farm;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.biz.entity.farm.BizFarm;

/**
 * 单表生成DAO接口
 * @author ThinkGem
 * @version 2016-07-08
 */
@MyBatisDao
public interface BizFarmDao extends CrudDao<BizFarm> {
	
}
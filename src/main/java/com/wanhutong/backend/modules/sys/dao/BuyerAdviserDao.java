package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.BuyerAdviser;
/**
 * * 
* <p>Title: BuyerAdviserDao</p>
* <p>Description: 采购商客户专员关联dao</p>
* <p>Company: WHT</p> 
* @date 2018年1月11日
 */
@MyBatisDao
public interface BuyerAdviserDao extends CrudDao<BuyerAdviser> {
	
}

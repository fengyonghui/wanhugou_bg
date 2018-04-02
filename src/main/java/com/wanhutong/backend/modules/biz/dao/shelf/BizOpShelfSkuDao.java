/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shelf;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;

/**
 * 商品上架管理DAO接口
 * @author liuying
 * @version 2017-12-19
 */
@MyBatisDao
public interface BizOpShelfSkuDao extends CrudDao<BizOpShelfSku> {
	public void dateTimeUpdate(BizOpShelfSku bizOpShelfSku);
	void shelvesUpdate(BizOpShelfSku bizOpShelfSku);
}
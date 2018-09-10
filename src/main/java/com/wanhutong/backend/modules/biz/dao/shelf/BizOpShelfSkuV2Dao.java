/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shelf;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.product.BizProductMinMaxPrice;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品上架管理DAO接口
 * @author liuying
 * @version 2017-12-19
 */
@MyBatisDao
public interface BizOpShelfSkuV2Dao extends CrudDao<BizOpShelfSku> {
	public void dateTimeUpdate(BizOpShelfSku bizOpShelfSku);
	void shelvesUpdate(BizOpShelfSku bizOpShelfSku);
	List<BizOpShelfSku> selectSort();
	void sort(BizOpShelfSku opShelfSku);

	/**
	 *根据商品上架id获取根据同一货架，同一类商品其余商品list
	 * @param bizOpShelfSku 商品上架管理Entity
	 * @return
	 */
	List<BizOpShelfSku> findShelfSkuList(BizOpShelfSku bizOpShelfSku);


	BizProductMinMaxPrice findMinMaxPrice(BizOpShelfSku bizOpShelfSku);
}
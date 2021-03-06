/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.category;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.product.BizProdCate;

import java.util.List;

/**
 * 记录分类下所有属性值DAO接口
 * @author liuying
 * @version 2017-12-06
 */
@MyBatisDao
public interface BizCatePropValueDao extends CrudDao<BizCatePropValue> {

    List<BizCatePropValue> findCatePropInfoValue(BizProdCate bizProdCate);
	
}
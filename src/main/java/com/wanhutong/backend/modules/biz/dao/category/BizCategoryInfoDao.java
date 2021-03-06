/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.category;

import com.wanhutong.backend.common.persistence.TreeDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;

import java.util.List;

/**
 * 垂直商品类目表DAO接口
 * @author liuying
 * @version 2017-12-06
 */
@MyBatisDao
public interface BizCategoryInfoDao extends TreeDao<BizCategoryInfo> {

    public int deleteCatePropInfoReal(BizCategoryInfo bizCategoryInfo);

    List<BizCategoryInfo> findListByBrandId(BizCategoryInfo bizCategoryInfo);
}
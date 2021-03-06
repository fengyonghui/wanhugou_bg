/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.product;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 产品信息表DAO接口
 * @author zx
 * @version 2017-12-13
 */
@MyBatisDao
public interface BizProductInfoV3Dao extends CrudDao<BizProductInfo> {

    int deleteProdCate(BizProductInfo bizProductInfo);

    int insertProdCate(BizProductInfo bizProductInfo);

    int deleteProdPropInfoReal(BizProductInfo bizProductInfo);

    void updateItemNo(@Param("id")Integer id,@Param("itemNo")String itemNo);

    void updateMinAndMaxPrice(@Param("id")Integer id, @Param("minPrice")Double minPrice, @Param("maxPrice")Double maxPrice);

}
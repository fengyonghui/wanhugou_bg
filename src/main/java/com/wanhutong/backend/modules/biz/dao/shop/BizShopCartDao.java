/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shop;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopCart;
import org.apache.ibatis.annotations.Param;

/**
 * 商品购物车DAO接口
 * @author OuyangXiutian
 * @version 2018-01-03
 */
@MyBatisDao
public interface BizShopCartDao extends CrudDao<BizShopCart> {

    /**
     * //删除购物车数据
     * @param status
     * @param userId
     */
    void updateShopCartByUserId(@Param("status") Integer status, @Param("updateId") Integer updateId, @Param("userId") Integer userId);

    /**
     * 删除购物车中间表数据
     * @param status
     * @param userId
     */
    void updateCartSkuByUserId(@Param("status") Integer status, @Param("updateId") Integer updateId, @Param("userId") Integer userId);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存详情DAO接口
 * @author 张腾飞
 * @version 2017-12-29
 */
@MyBatisDao
public interface BizInventorySkuDao extends CrudDao<BizInventorySku> {
    void orderUpdate(BizInventorySku bizInventorySku);

    /**
     * 查询唯一商品库存
     * @param bizInventorySku
     * @return
     */
    BizInventorySku findOnly(BizInventorySku bizInventorySku);

    /**
     * 查询采购中心库存总数
     * @param centId
     * @return
     */
    Integer invSkuCount(Integer centId);

    /**
     * 根据SKU和采购中心取库存数量
     * @param skuId skuId
     * @param centId 采购中心ID
     * @return
     */
    Integer getStockQtyBySkuIdCentId(@Param("skuId") Integer skuId, @Param("centId")Integer centId);

    /**
     * 根据SKU和采购中心取库存数量
     * @param skuId skuId
     * @param invId 仓库ID
     * @return
     */
    Integer getStockQtyBySkuIdInvId(@Param("skuId") Integer skuId, @Param("invId")Integer invId);

    /**
     * 根据SKU和采购中心和仓库商品类型取库存数量
     * @param skuId
     * @param centId
     * @param skuType 1 本地库存商品 2 供应商库存商品
     * @return
     */
    Integer getStockQtyBySkuIdCentIdSkuType(@Param("skuId") Integer skuId, @Param("centId") Integer centId, @Param("skuType") Integer skuType);

    void updateStockQty(@Param("id") Integer id, @Param("stockQty") Integer stockQty);

    /**
     * 根据采购中心和商品查找库存总数
     * @param centId
     * @param skuId
     * @return
     */
    int findStockTotal(@Param("centId") Integer centId, @Param("skuId") Integer skuId);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 库存调拨详情DAO接口
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@MyBatisDao
public interface BizSkuTransferDetailDao extends CrudDao<BizSkuTransferDetail> {

    /**
     * 修改入库数量
     * @param id
     * @param inQty
     */
    void updateInQty(@Param("id")Integer id, @Param("inQty")Integer inQty);

    /**
     * 根据采购中心ID和商品ID查询可出库的调拨单详情
     * @param centId
     * @param skuId
     * @return
     */
    List<BizSkuTransferDetail> findInventorySkuByskuIdAndcentId(@Param("centId")Integer centId, @Param("skuId")Integer skuId);

    /**
     * 根据订单详情ID查询已出库的调拨单信息
     * @param orderDetailId
     * @return
     */
    List<BizSkuTransferDetail> findInvTransferByOrderDetailId(@Param("orderDetailId")Integer orderDetailId);

    /**
     * 修改已卖出数量
     * @param id
     * @param sentQty
     */
    void updateSentQty(@Param("id")Integer id, @Param("sentQty")Integer sentQty);
}
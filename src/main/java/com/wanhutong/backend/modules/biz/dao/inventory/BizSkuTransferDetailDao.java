/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import org.apache.ibatis.annotations.Param;

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
}
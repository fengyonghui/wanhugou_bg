/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventoryviewlog;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import org.apache.ibatis.annotations.Param;

/**
 * 库存盘点记录DAO接口
 * @author zx
 * @version 2018-03-27
 */
@MyBatisDao
public interface BizInventoryViewLogDao extends CrudDao<BizInventoryViewLog> {

    void updateSkuId(@Param("needSkuId") Integer needSkuId, @Param("id") Integer id);
}
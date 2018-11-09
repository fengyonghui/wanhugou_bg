/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import org.apache.ibatis.annotations.Param;

/**
 * 库存调拨DAO接口
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@MyBatisDao
public interface BizSkuTransferDao extends CrudDao<BizSkuTransfer> {

    /**
     * 根据仓库ID查询该采购中心下的调拨单条数
     * @param invId
     * @return
     */
    int findCountByToCent(@Param("invId")Integer invId);
	
}
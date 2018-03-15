/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.StockAmountDto;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;

/**
 * 仓库信息表DAO接口
 * @author zhangtengfei
 * @version 2017-12-28
 */
@MyBatisDao
public interface BizInventoryInfoDao extends CrudDao<BizInventoryInfo> {

    StockAmountDto getStockAmountByCustId(Integer custId);
}
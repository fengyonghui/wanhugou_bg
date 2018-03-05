/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;

/**
 * 商品库存详情DAO接口
 * @author 张腾飞
 * @version 2017-12-29
 */
@MyBatisDao
public interface BizInventorySkuDao extends CrudDao<BizInventorySku> {
    void orderUpdate(BizInventorySku bizInventorySku);
}
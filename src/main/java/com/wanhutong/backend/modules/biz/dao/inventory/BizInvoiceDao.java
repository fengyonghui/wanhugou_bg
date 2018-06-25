/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;

/**
 * 发货单DAO接口
 * @author 张腾飞
 * @version 2018-03-05
 */
@MyBatisDao
public interface BizInvoiceDao extends CrudDao<BizInvoice> {
    /**
     * 客户专员的 统计
     * */
    void findBizOrderHeader(BizInvoice bizInvoice);
}
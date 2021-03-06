/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发货单DAO接口
 * @author 张腾飞
 * @version 2018-03-05
 */
@MyBatisDao
public interface BizInvoiceDao extends CrudDao<BizInvoice> {
    /**
     * 物流单信息详情
     * @param bizInvoice
     * @return
     */
    List<BizOrderDetail> findLogisticsDetail(BizInvoice bizInvoice);

    List<String> findDeliverNoByReqId(@Param("requestId") Integer requestId);

    /**
     * 通过发货单id获取对应订单列表
     * @param invoiceId
     * @return
     */
    List<BizOrderHeader> findOrderHeaderByInvoiceId(@Param("invoiceId") Integer invoiceId);
}
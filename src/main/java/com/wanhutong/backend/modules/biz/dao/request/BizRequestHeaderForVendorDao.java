/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.logistic.AddressVoEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 备货清单DAO接口
 * @author ZhangTengfei
 * @version 2018-07-19
 */
@MyBatisDao
public interface BizRequestHeaderForVendorDao extends CrudDao<BizRequestHeader> {

    int updateProcessId(@Param("headerId") Integer headerId, @Param("processId") Integer processId);

    int findContByFromOffice(Integer fromOfficeId);

    List<BizRequestHeader>findListForPoHeader(BizRequestHeader bizRequestHeader);

    Integer findSellCount( @Param("centId") Integer centId, @Param("skuId") Integer skuId);

    int incrPayTotal(@Param("id")int id, @Param("payTotal")BigDecimal payTotal);

    int updatePaymentOrderId(@Param("id") Integer id, @Param("paymentId")Integer paymentId);

    int updateBizStatus(@Param("id") Integer id,@Param("status") Integer status, @Param("updateBy") User updateBy, @Param("updateDate") Date updateDate);

    /**
     * 该备货单下所有商品的总采购数量，总排产数量（分为按订单排产的总排产量和按商品排产的总排产量）
     * @param id
     * @return
     */
    BizRequestHeader getTotalQtyAndSchedulingNum(@Param("id") Integer id);

    /**
     * 供应商确认排产后，更新排产表中排产状态
     * @param requestHeader
     */
    void updateSchedulingType(BizRequestHeader requestHeader);

    List<AddressVoEntity> findOfficeRegion(@Param("officeId") Integer officeId, @Param("type") Integer type);

    List<AddressVoEntity> findOrderRegion(@Param("orderId") Integer orderId, @Param("type") Integer type);

    List<BizRequestHeader> inventoryPage(BizRequestHeader bizRequestHeader);

}
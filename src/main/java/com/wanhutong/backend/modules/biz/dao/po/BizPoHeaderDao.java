/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizProductStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 采购订单表DAO接口
 * @author liuying
 * @version 2017-12-30
 */
@MyBatisDao
public interface BizPoHeaderDao extends CrudDao<BizPoHeader> {

    int updatePaymentOrderId(@Param("id") Integer id, @Param("paymentId")Integer paymentId);

    int updateBizStatus(@Param("id")Integer id, @Param("status")int status);

    int updateProcessId(@Param("headerId")Integer headerId, @Param("processId") Integer processId);

    int incrPayTotal(@Param("id")int id, @Param("payTotal")BigDecimal payTotal);

    /**
     * 供应商供应总额统计
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    List<BizOrderStatisticsDto> vendorProductPrice(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("vendName")String vendName);

    /**
     * 供应商供应SKU总额统计
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    List<BizOrderStatisticsDto> vendorSkuPrice(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("officeId")Integer officeId);

    /**
     * 该采购单下所有商品的总采购数量，总排产数量，总已确认排产数
     * @param id
     * @return
     */
    BizPoHeader getTotalNum(@Param("id") Integer id);
}
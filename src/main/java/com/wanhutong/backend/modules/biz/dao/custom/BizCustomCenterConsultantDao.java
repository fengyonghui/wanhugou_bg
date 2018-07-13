/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.custom;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户专员管理DAO接口
 * @author Ouyang Xiutian
 * @version 2018-01-13
 */
@MyBatisDao
public interface BizCustomCenterConsultantDao extends CrudDao<BizCustomCenterConsultant> {

    List<BizCustomCenterConsultant> userFindList(BizCustomCenterConsultant bizCustomCenterConsultant);
    /**
     * 客户专员关联采购商有效订单查询
     * @param startDate 选择区间的开始时间
     * @param endDate   选择区间的结束时间
     * @param consultantId 客户专员Id
     * @return
     */
    List<BizCustomCenterConsultant> customOrderList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("consultantId") Integer consultantId);

    /**
     * 采购专员关联下具有有效订单的采购商的数量统计
     * @param startDate
     * @param endDate
     * @param purchasingId
     * @return
     */
    List<BizCustomCenterConsultant> consultantOrderList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("purchasingId") Integer purchasingId);
}
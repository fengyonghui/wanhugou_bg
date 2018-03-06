/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@MyBatisDao
public interface BizOrderHeaderDao extends CrudDao<BizOrderHeader> {
    public List<BizOrderHeader> findListFirstOrder(BizOrderHeader bizOrderHeader);
    public void updateMoney(BizOrderHeader bizOrderHeader);

    /**
     * 按月获取订单的总金额和订单数量
     *
     * @param month 月份
     * @param status 订单状态
     * @param officeType 机构类型
     * @return 订单统计数据
     */
    List<BizOrderStatisticsDto> getOrderTotalAndCountByCreateTimeMonthStatus( @Param("month")String month, @Param("status") Integer status, @Param("officeType") String officeType);
}

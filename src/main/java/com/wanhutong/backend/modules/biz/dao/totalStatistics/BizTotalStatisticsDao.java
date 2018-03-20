/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.totalStatistics;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 统计总的会员数，采购中心数，订单数量，总额、商品数量 平均客单价Entity
 * @author 张腾飞
 * @version 2018-03-13
 */
@MyBatisDao
public interface BizTotalStatisticsDao {

    /**
     * 会员数
     * @return
     */
    Integer getTotalCustCount();

    /**
     * 采购中心数，网供数，配资中心数
     * @return
     */
    Integer getTotalCentCount(Integer type);

    /**
     * 订单数，总额，已收货款
     * @return
     */
    BizTotalStatisticsDto getTotalStatisticsDto();

    /**
     * 商品数量
     * @return
     */
    Integer getTotalSkuCount();
}

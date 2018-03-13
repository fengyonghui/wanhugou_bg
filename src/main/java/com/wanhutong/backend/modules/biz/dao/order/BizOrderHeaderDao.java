/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizProductStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserSaleStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)DAO接口
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@MyBatisDao
public interface BizOrderHeaderDao extends CrudDao<BizOrderHeader> {
    List<BizOrderHeader> findListFirstOrder(BizOrderHeader bizOrderHeader);

    void updateMoney(BizOrderHeader bizOrderHeader);

    Integer findCount(BizOrderHeader bizOrderHeader);

    /**
     * 按月获取订单的总金额和订单数量
     *
     * @param month      月份
     * @param statusList 订单状态
     * @param officeType 机构类型
     * @return 订单统计数据
     */
    List<BizOrderStatisticsDto> getValidOrderTotalAndCountByCreateTimeMonth(@Param("month") String month, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList, @Param("officeType") String officeType);

    /**
     * 按月获取订单销售额相关的产品信息
     *
     * @param month 月份
     * @return 产品统计数据
     */
    List<BizProductStatisticsDto> getProductStatisticData(@Param("month") String month, @Param("variId") Integer variId, @Param("purchasingId") Integer purchasingId);

    /**
     * 按月获取用户相关的注册信息
     *
     * @param month 月份
     * @return 用户统计数据
     */
    List<BizUserStatisticsDto> getUserStatisticData(String month);

    /**
     * 根据月份取用户业绩统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getUserSaleStatisticData(@Param("month") String month, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticData(@Param("month") String month, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);


    /**
     * 按日获取订单的总金额和订单数量
     *
     * @param day        日期
     * @param statusList 订单状态
     * @param officeType 机构类型
     * @return 订单统计数据
     */
    List<BizOrderStatisticsDto> getValidOrderTotalAndCountByCreateTimeDay(@Param("day") String day, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList, @Param("officeType") String officeType);

    /**
     * 按日获取订单销售额相关的产品信息
     *
     * @param day 日期
     * @return 产品统计数据
     */
    List<BizProductStatisticsDto> getProductStatisticDataDay(@Param("day") String day, @Param("variId") Integer variId, @Param("purchasingId") Integer purchasingId);

    /**
     * 按日获取用户相关的注册信息
     *
     * @param day 取数据的日期
     * @return 用户统计数据
     */
    List<BizUserStatisticsDto> getUserStatisticDataDay(String day);

    /**
     * 根据日期取用户业绩统计相关数据
     *
     * @param day          取数据的日期
     * @param purchasingId
     * @param usId         用户ID
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getUserSaleStatisticDataDay(@Param("day") String day, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @param day 取数据的日期
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticDataDay(@Param("day") String day, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);


    /**
     * 按日获取订单的总金额和订单数量
     *
     * @param startDate  开始时间
     * @param statusList 订单状态
     * @param officeType 机构类型
     * @return 订单统计数据
     */
    List<BizOrderStatisticsDto> getValidOrderTotalAndCountByCreateTimeBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList, @Param("officeType") String officeType);


    /**
     * 按区间获取订单销售额相关的产品信息
     *
     * @param startDate 开始时间
     * @return 产品统计数据
     */
    List<BizProductStatisticsDto> getProductStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("variId") Integer variId, @Param("purchasingId") Integer purchasingId);

    /**
     * 按区间获取用户相关的注册信息
     *
     * @param startDate 开始时间
     * @return 用户统计数据
     */
    List<BizUserStatisticsDto> getUserStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据区间取用户业绩统计相关数据
     *
     * @param startDate    开始时间
     * @param purchasingId
     * @param usId         用户ID
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getUserSaleStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @param startDate 开始时间
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId);

    /**
     * 根据月份和采购中心取客户专员数据
     *
     * @param month
     * @param purchasingId
     * @return
     */
    List<BizUserSaleStatisticsDto> getUserTableStatisticData(@Param("month") String month, @Param("purchasingId") Integer purchasingId);


    /**
     * 取平台用户统计数据
     *
     * @param type  数据类型  年:0/月:1
     * @param month 数据类型  月份
     * @return
     */
    List<BizUserStatisticsDto> getUserStatisticDataPlatform(@Param("type") String type, @Param("month") String month, @Param("centerType") String centerType);


    /**
     * 用户平台订单统计
     *
     * @param startDate   时间
     * @param validStatus
     * @param type
     * @return
     */
    List<BizOrderStatisticsDto> getValidOrderTotalAndCount(
            @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("statusList") List<OrderHeaderBizStatusEnum> validStatus,
            @Param("type") String type, @Param("centerType") String centerType, @Param("orderType") String orderType, @Param("officeId") Integer officeId);


    /**
     * 按区间获取商品新增数量
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 产品统计数据
     */
    Integer getSkuStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("variId") Integer variId);
}

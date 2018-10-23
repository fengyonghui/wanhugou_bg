/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizPlatformDataOverviewDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizProductStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserSaleStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.order.BizDrawBack;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
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
     * 按月获取订单的总金额和订单数量
     *
     * @param month      月份
     * @param officeId   机构ID
     * @return 订单统计数据
     */
    BizOrderStatisticsDto getValidOrderTotalAndCountByCreateTimeMonthOfficeId(@Param("month") String month, @Param("officeId") Integer officeId);

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
     * 按月获取用户相关的注册信息
     *
     * @param month 月份
     * @return 用户统计数据
     */
    BizUserStatisticsDto getUserStatisticDataByOfficeId(@Param("month")String month, @Param("officeId")Integer officeId);


    /**
     * 按月获取有效用户相关的注册信息
     *
     * @param month 月份
     * @return 用户统计数据
     */
    BizUserStatisticsDto getValidUserStatisticDataByOfficeId(@Param("month")String month, @Param("officeId")Integer officeId);


    /**
     * 根据月份取用户业绩统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getUserSaleStatisticData(@Param("month") String month, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList);

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticData(@Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList);


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
    List<BizUserSaleStatisticsDto> getUserSaleStatisticDataDay(@Param("day") String day, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList);

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @param day 取数据的日期
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticDataDay(@Param("day") String day, @Param("purchasingId") Integer purchasingId, @Param("usId") Integer usId, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList);


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
    List<BizProductStatisticsDto> getProductStatisticDataBetween(@Param("startDate") String startDate,
                                                                 @Param("endDate") String endDate,
                                                                 @Param("variId") Integer variId,
                                                                 @Param("purchasingId") Integer purchasingId,
                                                                 @Param("type") Integer type
                                                                );

   /**
     * 按区间获取商品趋势相关的信息
     *
     * @param startDate 开始时间
     * @return 产品统计数据
     */
    List<BizProductStatisticsDto> skuTendencyDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                                         @Param("variId") Integer variId, @Param("purchasingId") Integer purchasingId,
                                                         @Param("type") Integer type,
                                                         @Param("timeType") String timeType,
                                                         @Param("itemNo") String itemNo
                                                        );

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
    List<BizUserSaleStatisticsDto> getUserSaleStatisticDataBetween(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("purchasingId") Integer purchasingId,
            @Param("usId") Integer usId,
            @Param("statusList") List<OrderHeaderBizStatusEnum> statusList
    );

    /**
     * 根据用户取用户业绩统计相关数据
     *
     * @param startDate 开始时间
     * @return 根据不同用户分类的统计数据
     */
    List<BizUserSaleStatisticsDto> getSingleUserSaleStatisticDataBetween(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("purchasingId") Integer purchasingId,
            @Param("usId") Integer usId,
            @Param("statusList") List<OrderHeaderBizStatusEnum> statusList
    );

    /**
     * 根据月份和采购中心取客户专员数据
     *
     * @param month
     * @param purchasingId
     * @return
     */
    List<BizUserSaleStatisticsDto> getUserTableStatisticData(
            @Param("month") String month,
            @Param("purchasingId") Integer purchasingId,
            @Param("statusList") List<OrderHeaderBizStatusEnum> statusList
    );


    /**
     * 取平台用户统计数据
     *
     * @param type  数据类型  年:0/月:1
     * @param startDate 数据类型  月份
     * @return
     */
    List<BizUserStatisticsDto> getUserStatisticDataPlatform(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("centerType") String centerType);

    List<BizUserStatisticsDto> getAllUserStatisticDataPlatform(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("centerType") String centerType);


    /**
     * 用户平台订单统计 根据机构区分
     *
     * @param startDate   时间
     * @param type
     * @return
     */
    List<BizOrderStatisticsDto> getPlatformValidOrderTotalAndCount(
            @Param("startDate") String startDate, @Param("endDate") String endDate,
            @Param("type") String type);

    /**
     * 用户平台订单统计 根据机构区分
     *
     * @param startDate   时间
     * @param validStatus
     * @param type
     * @return
     */
    List<BizOrderStatisticsDto> getValidOrderTotalAndCount(
            @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("statusList") List<OrderHeaderBizStatusEnum> validStatus,
            @Param("type") String type, @Param("centerType") String centerType, @Param("orderType") String orderType, @Param("officeId") Integer officeId, @Param("userId") Integer userId);

    /**
     * 用户平台订单统计 所有数据, 不区分机构
     *
     * @param startDate   时间
     * @param validStatus
     * @param type
     * @return
     */
    List<BizOrderStatisticsDto> getAllValidOrderTotalAndCount(
            @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("statusList") List<OrderHeaderBizStatusEnum> validStatus,
            @Param("type") String type, @Param("centerType") String centerType, @Param("orderType") String orderType, @Param("officeId") Integer officeId);


    /**
     * 按区间获取商品新增数量
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 商品新增数量
     */
    Integer getSkuStatisticDataBetween(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("variId") Integer variId);

    /**
     * 按区间获取商品新增数量
     *
     * @param variId 类别ID
     * @return 商品新增数量
     */
    List<BizProductStatisticsDto> getSkuAllStatisticData(@Param("variId")Integer variId);

    /**
     * 取平台概览信息
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    List<BizPlatformDataOverviewDto> platformDataOverview(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("statusList") List<OrderHeaderBizStatusEnum> statusList);

    /**
     * 取个人平台概览信息
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    List<BizPlatformDataOverviewDto> singlePlatformDataOverview(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("statusList") List<OrderHeaderBizStatusEnum> statusList,
            @Param("officeId")Integer officeId);

    /**
     * 查询线下支付订单
     * @return
     */
    List<BizOrderHeader> findUnlineOrder();

    /**
     * 根据office id 取库存
     * @param id
     * @return
     */
    BigDecimal getStockAmountByOfficeId(Integer id);

    /**
     * 根据采购商ID 取采购频率的原始数据
     * @param custId
     * @param invalidStatus
     * @return
     */
    List<Integer> findOrderCountFrequency(@Param("custId") Integer custId, @Param("statusList") List<OrderHeaderBizStatusEnum> invalidStatus);


    /**
     * 订单采购列表
     * @param
     * */
    List<BizOrderHeader> headerFindList(BizOrderHeader bizOrderHeader);
 /**
     * 订单采购列表
     * @param
     * */
    List<BizOrderHeader> headerFindListForPhotoOrder(BizOrderHeader bizOrderHeader);

    /**
     * 用于C端订单列表
     * */
    public List<BizOrderHeader> cendfindList(BizOrderHeader bizOrderHeader);

    /**
     *  客户专员查看 采购累计订单频次
     * */
    List<BizOrderHeader> findUserOrderCountSecond(BizChatRecord bizChatRecord);

    /**
     * 商品管理 的下单量
     * */
    List<BizOrderHeader> findOrderCount(BizOrderHeader bizOrderHeader);

    /**
     * 客户专员的 统计
     * */
    BizOrderHeader findOrderUserCount(BizOrderHeader bizOrderHeader);
    /**
     * 品类主管 管理 商品统计
     * */
    BizOrderHeader categorySkuStatistics(User user);

    /**
     * 通过订单获取退款记录entity
     *
     * @param bizOrderHeader
     */
    BizDrawBack findDrawBack(BizOrderHeader bizOrderHeader);

    /**
     * 通过orderNum获取订单Entity
     *
     * @param orderNum
     */
    BizOrderHeader getByOrderNum(String orderNum);

    /**
     * 备货单商品的销售单
     * @param skuIdList
     * @param centId
     * @return
     */
    List<BizOrderHeader> findOrderForVendReq(@Param("skuIdList") List<Integer> skuIdList, @Param("centId") Integer centId);


    /**
     * 更新订单业务状态
     * @param id
     * @param status
     * @param updateBy
     * @param updateDate
     * @return
     */
    int updateBizStatus(@Param("id") Integer id,@Param("status") Integer status, @Param("updateBy") User updateBy, @Param("updateDate") Date updateDate);

//    /**
//     * 更新审核流程id
//     * @param headerId
//     * @param processId
//     * @return
//     */
//    int updateProcessId(@Param("headerId") Integer headerId, @Param("processId") Integer processId);

    /**
     * 根据商品id获取相应orderDetailId
     * @param poHeaderId
     * @param skuInfoId
     * @return
     */
    Integer getOrderDetailIdBySkuInfoId(@Param("poHeaderId") Integer poHeaderId, @Param("skuInfoId") Integer skuInfoId);

    /**
     * 系统管理员查看待审核订单
     * @param bizOrderHeader
     * @return
     */
    List<BizOrderHeader> findListNotCompleteAudit(BizOrderHeader bizOrderHeader);

    Integer findCountByCentId(@Param("centId") Integer centId);
}

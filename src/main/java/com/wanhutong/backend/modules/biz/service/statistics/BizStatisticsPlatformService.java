package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.plan.BizOpPlanDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizPlatformDataOverviewDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.EchartsSeriesDto;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


/**
 * 统计相关Service
 *
 * @author Ma.Qiang
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class BizStatisticsPlatformService {

    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;

    @Resource
    private OfficeDao officeDao;

    @Resource
    private BizOpPlanDao bizOpPlanDao;

    @Resource
    private UserDao userDao;

    @Resource
    private BizPayRecordService bizPayRecordService;

    /**
     * 请求参数日期格式
     */
    public static final String PARAM_DATE_FORMAT = "yyyy-MM";
    /**
     * 默认起始日期
     */
    public static final String DEFAULT_START_DATE = "2017-09-01";

    /**
     * 默认日期格式
     */
    public static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据月份取订单统计相关数据
     *
     * @param startDate 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizOrderStatisticsDto> orderPlatformStatisticData(String startDate, String endDate, String type, String centerType, String orderType) {
        return bizOrderHeaderDao.getPlatformValidOrderTotalAndCount(startDate, endDate, type);
    }

    /**
     * 根据月份取订单统计相关数据
     *
     * @param startDate 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizOrderStatisticsDto> orderStatisticData(String startDate, String endDate, String type, String centerType, String orderType) {
//        if (StringUtils.isBlank(centerType) || Integer.valueOf(centerType) == 0) {
//            return bizOrderHeaderDao.getAllValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null);
//        }
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null, null);
    }

    /**
     * 根据月份取用户统计相关数据
     *
     * @param startDate 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizUserStatisticsDto> userStatisticData(String type, String startDate, String endDate, String centerType) {
        if (StringUtils.isBlank(type) || Integer.valueOf(type) == 0) {
            return bizOrderHeaderDao.getUserStatisticDataPlatform(type, startDate, endDate + " 23:59:59", centerType);
        }
        return bizOrderHeaderDao.getAllUserStatisticDataPlatform(type, startDate, endDate + " 23:59:59", centerType);
    }


    public List<BizOrderStatisticsDto> orderStatisticDataByOffice(String startDate, String endDate, String type, String centerType, String orderType, Integer id) {
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, id, null);
    }

    public List<BizOrderStatisticsDto> getJoinPurchaseData(Integer centerId, String startDate) {
        String fromDate = startDate + "-01 00:00:00";
        String endDate = startDate + "-13 23:59:59";
        return bizOrderHeaderDao.getJoinPurchaseData(centerId, fromDate, endDate);
    }

    public List<BizOrderStatisticsDto> getJoinPurchaseDataSingle(Integer consultantId, String formatDate) {
        return bizOrderHeaderDao.getJoinPurchaseDataSingle(consultantId, formatDate);
    }

    public List<BizOrderStatisticsDto> orderStatisticDataByUser(String startDate, String endDate, String type, String centerType, String orderType, Integer userId) {
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null, userId);
    }




    /**
     * 获取个人平台业务数据
     */
    public Map<String, List<BizPlatformDataOverviewDto>> getSinglePlatformData(String startDate, String endDate, Integer officeId) {
        String[] dateStrArr = startDate.split("-");

        List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = bizOrderHeaderDao.singlePlatformDataOverview(startDate, endDate + " 23:59:59", OrderHeaderBizStatusEnum.INVALID_STATUS, officeId);
        Set<String> nameSet = Sets.newHashSet();
        for (BizPlatformDataOverviewDto b : bizPlatformDataOverviewDtos) {
            nameSet.add(b.getName());
        }

        User user = new User();
        user.setOffice(new Office(officeId));
        Role role = new Role();
        role.setName("采购专员");
        user.setRole(role);
        List<User> userList = userDao.findList(user);
        userList.removeIf(u -> u.getName().contains("测试"));
        userList.forEach(u -> {
            if (!nameSet.contains(u.getName())) {
                BizPlatformDataOverviewDto bizPlatformDataOverviewDto = new BizPlatformDataOverviewDto();
                bizPlatformDataOverviewDto.setName(u.getName());
                bizPlatformDataOverviewDto.setUserId(u.getId());
                bizPlatformDataOverviewDto.setOfficeId(u.getId());
                bizPlatformDataOverviewDtos.add(bizPlatformDataOverviewDto);
            }
        });

        Map<String, List<BizPlatformDataOverviewDto>> resultMap = Maps.newHashMap();
        bizPlatformDataOverviewDtos.forEach(o -> {
            List<BizOrderStatisticsDto> currentBizOrderStatisticsDtoList =
                    orderStatisticDataByUser(endDate, endDate + " 23:59:59", null, null, null, o.getUserId());
            // 采购额
            BizOpPlan bizOpPlan = new BizOpPlan();
            bizOpPlan.setObjectName("sys_user");
            bizOpPlan.setObjectId(String.valueOf(o.getUserId()));
            bizOpPlan.setYear(dateStrArr[0]);
            bizOpPlan.setMonth(dateStrArr[1]);
            bizOpPlan.setDay("0");
            List<BizOpPlan> planList = bizOpPlanDao.findList(bizOpPlan);
            if (CollectionUtils.isNotEmpty(planList)) {
                bizOpPlan = planList.get(0);
            }

            List<BizOrderStatisticsDto> joinPurchaseData = getJoinPurchaseDataSingle(o.getUserId(), dateStrArr[0] + dateStrArr[1]);
            BizOrderStatisticsDto joinPurchaseOrderData = new BizOrderStatisticsDto();
            if (CollectionUtils.isNotEmpty(joinPurchaseData)) {
                joinPurchaseOrderData = joinPurchaseData.get(0);
            }

            //月计划联营订单总额
            o.setJointOrderPlanAmountTotal(bizOpPlan.getJointOrderAmount() == null ? BigDecimal.ZERO : bizOpPlan.getJointOrderAmount());
            //月联营订单总额
            o.setJointOrderAmountTotal(joinPurchaseOrderData.getJoinRemitAmount());
            //月计划代采订单总额
            o.setPurchaseOrderPlanAmountTotal(bizOpPlan.getPurchaseOrderAmount() == null ? BigDecimal.ZERO : bizOpPlan.getPurchaseOrderAmount());
            //月采订单总额
            o.setPurchaseOrderAmountTotal(joinPurchaseOrderData.getPurchaseRemitAmount());

            //月计划订单总额
            //o.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
            o.setProcurement(o.getJointOrderPlanAmountTotal().add(o.getPurchaseOrderPlanAmountTotal()));
            o.setProcurementDay(
                    CollectionUtils.isEmpty(currentBizOrderStatisticsDtoList) ?
                            BigDecimal.ZERO
                            : currentBizOrderStatisticsDtoList.get(0).getTotalMoney());
            // 库存金额
            o.setCurrentDate(endDate);
            List<BizPlatformDataOverviewDto> tempDtoList = resultMap.putIfAbsent(o.getProvince(), Lists.newArrayList(o));
            if (tempDtoList != null) {
                tempDtoList.add(o);
            }
        });
        return resultMap;

    }

    /**
     * 获取平台业务数据
     */
    public Map<String, List<BizPlatformDataOverviewDto>> getPlatformData(String startDate, String endDate, String currentDate) throws ParseException {
        String[] dateStrArr = startDate.split("-");

        List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = bizOrderHeaderDao.platformDataOverview(startDate, endDate + " 23:59:59", OrderHeaderBizStatusEnum.INVALID_STATUS);
        Set<String> nameSet = Sets.newHashSet();
        for (BizPlatformDataOverviewDto b : bizPlatformDataOverviewDtos) {
            nameSet.add(b.getName());
        }

        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");

        Date sDate = sdfDay.parse(startDate);

        List<Office> listByType = officeDao.findListByTypeList(Lists.newArrayList(
                OfficeTypeEnum.PURCHASINGCENTER.getType(),
                OfficeTypeEnum.WITHCAPITAL.getType(),
                OfficeTypeEnum.NETWORKSUPPLY.getType(),
                OfficeTypeEnum.NETWORK.getType()
        ));
        listByType.removeIf(office -> office.getName().contains("测试"));
        listByType.forEach(office -> {
            if (!nameSet.contains(office.getName())) {
                BizPlatformDataOverviewDto bizPlatformDataOverviewDto = new BizPlatformDataOverviewDto();
                bizPlatformDataOverviewDto.setName(office.getName());
                bizPlatformDataOverviewDto.setOfficeType(Integer.valueOf(office.getType()));
                bizPlatformDataOverviewDto.setOfficeId(office.getId());
                bizPlatformDataOverviewDto.setProvince(office.getProvince());
                // 取库存
                bizPlatformDataOverviewDto.setStockAmount(bizOrderHeaderDao.getStockAmountByOfficeId(office.getId()));
                bizPlatformDataOverviewDtos.add(bizPlatformDataOverviewDto);
            }
        });

        //合计值
        BigDecimal totalProcurement = BigDecimal.ZERO;
        BigDecimal totalJointOrderPlanAmountTotal = BigDecimal.ZERO;
        BigDecimal totalJointOrderAmountTotal = BigDecimal.ZERO;
        BigDecimal totalPurchaseOrderPlanAmountTotal = BigDecimal.ZERO;
        BigDecimal totalPurchaseOrderAmountTotal = BigDecimal.ZERO;
        BigDecimal totalReceiveTotal = BigDecimal.ZERO;
        BigDecimal totalAccumulatedSalesMonth = BigDecimal.ZERO;
        BigDecimal totalProcurementDay = BigDecimal.ZERO;
        BigDecimal totalNewUser = BigDecimal.ZERO;
        BigDecimal totalServiceChargePlan = BigDecimal.ZERO;
        BigDecimal totalServiceCharge = BigDecimal.ZERO;
        BigDecimal totalStockAmount = BigDecimal.ZERO;
        String totalCurrentDate = "";

        Map<String, List<BizPlatformDataOverviewDto>> resultMap = Maps.newHashMap();
        for (int i = 0; i < bizPlatformDataOverviewDtos.size(); i++) {
            BizPlatformDataOverviewDto o = bizPlatformDataOverviewDtos.get(i);
            List<BizOrderStatisticsDto> currentBizOrderStatisticsDtoList =
                    orderStatisticDataByOffice(currentDate, currentDate + " 23:59:59", null, null, null, o.getOfficeId());
            // 采购额
            BizOpPlan bizOpPlan = new BizOpPlan();
            bizOpPlan.setObjectName("sys_office");
            bizOpPlan.setObjectId(String.valueOf(o.getOfficeId()));
            bizOpPlan.setYear(dateStrArr[0]);
            bizOpPlan.setMonth(dateStrArr[1]);
            bizOpPlan.setDay("0");
            List<BizOpPlan> planList = bizOpPlanDao.findList(bizOpPlan);
            if (CollectionUtils.isNotEmpty(planList)) {
                bizOpPlan = planList.get(0);
            }

            List<BizOrderStatisticsDto> joinPurchaseData = getJoinPurchaseData(o.getOfficeId(), dateStrArr[0] + "-" + dateStrArr[1]);
            BizOrderStatisticsDto joinPurchaseOrderData = new BizOrderStatisticsDto();
            if (CollectionUtils.isNotEmpty(joinPurchaseData)) {
                joinPurchaseOrderData = joinPurchaseData.get(0);
            }

            BizUserStatisticsDto userStatisticDataByOfficeId = bizOrderHeaderDao.getValidUserStatisticDataByOfficeId(sdfMonth.format(sDate), o.getOfficeId());
            //o.setNewUser(userStatisticDataByOfficeId == null ? BigDecimal.ZERO : BigDecimal.valueOf(userStatisticDataByOfficeId.getCount()));
            //o.setNewUserPlan(bizOpPlan.getNewUser() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bizOpPlan.getNewUser()));
            BizOrderStatisticsDto serviceChargeDto = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeMonthOfficeId(sdfMonth.format(sDate) + "%", o.getOfficeId());
            o.setServiceCharge(serviceChargeDto == null ? new BigDecimal("0.00") : serviceChargeDto.getServiceCharge());
            o.setServiceChargePlan(bizOpPlan.getServiceCharge() == null ? new BigDecimal("0.00") : BigDecimal.valueOf(bizOpPlan.getServiceCharge()));

            //月计划联营订单总额
            o.setJointOrderPlanAmountTotal(bizOpPlan.getJointOrderAmount() == null ? new BigDecimal("0.00") : bizOpPlan.getJointOrderAmount());
            //月联营订单总额
            o.setJointOrderAmountTotal(joinPurchaseOrderData.getJoinRemitAmount() == null ? new BigDecimal("0.00") : joinPurchaseOrderData.getJoinRemitAmount());

            //月计划代采订单总额
            o.setPurchaseOrderPlanAmountTotal(bizOpPlan.getPurchaseOrderAmount() == null ? new BigDecimal("0.00") : bizOpPlan.getPurchaseOrderAmount());
            //月代采订单总额
            o.setPurchaseOrderAmountTotal(joinPurchaseOrderData.getPurchaseRemitAmount() == null ? new BigDecimal("0.00") : joinPurchaseOrderData.getPurchaseRemitAmount());

            //有效会员开单量
            o.setNewUser(joinPurchaseOrderData.getValidCustomerNum() == null ? new BigDecimal("0.00") : joinPurchaseOrderData.getValidCustomerNum());
            o.setNewUserPlan(bizOpPlan.getNewUser() == null ? new BigDecimal("0.00") : BigDecimal.valueOf(bizOpPlan.getNewUser()));

            //o.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
            //o.setProcurement(o.getJointOrderPlanAmountTotal().add(o.getPurchaseOrderPlanAmountTotal()));
            if (o.getJointOrderPlanAmountTotal().add(o.getPurchaseOrderPlanAmountTotal()).compareTo(BigDecimal.ZERO) == 0) {
                //旧数据问题处理
                o.setProcurement(bizOpPlan.getAmount() == null ? new BigDecimal("0.00") : bizOpPlan.getAmount());
            } else {
                o.setProcurement(o.getJointOrderPlanAmountTotal().add(o.getPurchaseOrderPlanAmountTotal()));
            }
            o.setProcurementDay(
                    CollectionUtils.isEmpty(currentBizOrderStatisticsDtoList) ?
                            new BigDecimal("0.00")
                            : currentBizOrderStatisticsDtoList.get(0).getTotalMoney());
            // 库存金额
            o.setCurrentDate(endDate);



            List<BizPlatformDataOverviewDto> tempDtoList = resultMap.putIfAbsent(o.getProvince(), Lists.newArrayList(o));
            if (tempDtoList != null) {
                tempDtoList.add(o);
            }

            totalProcurement = totalProcurement.add(o.getProcurement());
            totalJointOrderPlanAmountTotal = totalJointOrderPlanAmountTotal.add(o.getJointOrderPlanAmountTotal());
            totalJointOrderAmountTotal = totalJointOrderAmountTotal.add(o.getJointOrderAmountTotal());
            totalPurchaseOrderPlanAmountTotal = totalPurchaseOrderPlanAmountTotal.add(o.getPurchaseOrderPlanAmountTotal());
            totalPurchaseOrderAmountTotal = totalPurchaseOrderAmountTotal.add(o.getPurchaseOrderAmountTotal());
            totalReceiveTotal = totalReceiveTotal.add(o.getReceiveTotal());
            totalAccumulatedSalesMonth = totalAccumulatedSalesMonth.add(o.getAccumulatedSalesMonth());
            totalProcurementDay = totalProcurementDay.add(o.getProcurementDay());
            totalNewUser = totalNewUser.add(o.getNewUser());
            totalServiceChargePlan = totalServiceChargePlan.add(o.getServiceChargePlan());
            totalServiceCharge = totalServiceCharge.add(o.getServiceCharge());
            totalStockAmount = totalStockAmount.add(o.getStockAmount());
            totalCurrentDate = endDate;

        }

//        bizPlatformDataOverviewDtos.forEach(o -> {
//            List<BizOrderStatisticsDto> currentBizOrderStatisticsDtoList =
//                    orderStatisticDataByOffice(currentDate, currentDate + " 23:59:59", null, null, null, o.getOfficeId());
//            // 采购额
//            BizOpPlan bizOpPlan = new BizOpPlan();
//            bizOpPlan.setObjectName("sys_office");
//            bizOpPlan.setObjectId(String.valueOf(o.getOfficeId()));
//            bizOpPlan.setYear(dateStrArr[0]);
//            bizOpPlan.setMonth(dateStrArr[1]);
//            bizOpPlan.setDay("0");
//            List<BizOpPlan> planList = bizOpPlanDao.findList(bizOpPlan);
//            if (CollectionUtils.isNotEmpty(planList)) {
//                bizOpPlan = planList.get(0);
//            }
//
//            List<BizOrderStatisticsDto> joinPurchaseData = getJoinPurchaseData(o.getOfficeId(), dateStrArr[0] + dateStrArr[1]);
//            BizOrderStatisticsDto joinPurchaseOrderData = new BizOrderStatisticsDto();
//            if (CollectionUtils.isNotEmpty(joinPurchaseData)) {
//                joinPurchaseOrderData = joinPurchaseData.get(0);
//            }
//
//            BizUserStatisticsDto userStatisticDataByOfficeId = bizOrderHeaderDao.getValidUserStatisticDataByOfficeId(sdfMonth.format(sDate), o.getOfficeId());
//            o.setNewUser(userStatisticDataByOfficeId == null ? BigDecimal.ZERO : BigDecimal.valueOf(userStatisticDataByOfficeId.getCount()));
//            o.setNewUserPlan(bizOpPlan.getNewUser() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bizOpPlan.getNewUser()));
//            BizOrderStatisticsDto serviceChargeDto = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeMonthOfficeId(sdfMonth.format(sDate) + "%", o.getOfficeId());
//            o.setServiceCharge(serviceChargeDto == null ? BigDecimal.ZERO : serviceChargeDto.getProfitPrice());
//            o.setServiceChargePlan(bizOpPlan.getServiceCharge() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bizOpPlan.getServiceCharge()));
//
//            //月计划联营订单总额
//            o.setJointOrderPlanAmountTotal(bizOpPlan.getJointOrderAmount() == null ? BigDecimal.ZERO : bizOpPlan.getJointOrderAmount());
//            //月联营订单总额
//            o.setJointOrderAmountTotal(joinPurchaseOrderData.getJoinSaleAmount() == null ? BigDecimal.ZERO : joinPurchaseOrderData.getJoinSaleAmount());
//
//            //月计划代采订单总额
//            o.setPurchaseOrderPlanAmountTotal(bizOpPlan.getPurchaseOrderAmount() == null ? BigDecimal.ZERO : bizOpPlan.getPurchaseOrderAmount());
//            //月代采订单总额
//            o.setPurchaseOrderAmountTotal(joinPurchaseOrderData.getPurchaseSaleAmount() == null ? BigDecimal.ZERO : joinPurchaseOrderData.getPurchaseSaleAmount());
//
//            //o.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
//            o.setProcurement(o.getJointOrderPlanAmountTotal().add(o.getPurchaseOrderPlanAmountTotal()));
//            o.setProcurementDay(
//                    CollectionUtils.isEmpty(currentBizOrderStatisticsDtoList) ?
//                            BigDecimal.ZERO
//                            : currentBizOrderStatisticsDtoList.get(0).getTotalMoney());
//            // 库存金额
//            o.setCurrentDate(endDate);
//
//
//
//            List<BizPlatformDataOverviewDto> tempDtoList = resultMap.putIfAbsent(o.getProvince(), Lists.newArrayList(o));
//            if (tempDtoList != null) {
//                tempDtoList.add(o);
//            }
//
//            //月代采订单总额
//            //totalPurchaseOrderAmountTotal.add(o.getPurchaseOrderAmountTotal());
//            totalPurchaseOrderAmountTotal = totalPurchaseOrderAmountTotal.add(o.getPurchaseOrderAmountTotal());
//
//
//            totalProcurement.add(o.getProcurement());
//            totalJointOrderPlanAmountTotal.add(o.getJointOrderPlanAmountTotal());
//            totalJointOrderAmountTotal.add(o.getJointOrderAmountTotal());
//            totalPurchaseOrderPlanAmountTotal.add(o.getPurchaseOrderPlanAmountTotal());
//            //totalPurchaseOrderAmountTotal.add(o.getPurchaseOrderAmountTotal());
//            totalReceiveTotal.add(o.getReceiveTotal());
//            //totalYieldRate = "---"
//            totalAccumulatedSalesMonth.add(o.getAccumulatedSalesMonth());
//            totalProcurementDay.add(o.getProcurementDay());
//            //totalDifferenceTotalMonth.add(o.getDifferenceTotalMonth());
//            //totalRemainingDays.set(o.getRemainingDays());
//            //totalDayMinReturned.add(o.getDayMinReturned());
//            totalServiceChargePlan.add(o.getServiceChargePlan());
//            totalServiceCharge.add(o.getServiceCharge());
//            //totalServiceChargeRate = "---";
//            totalStockAmount.add(o.getStockAmount());
//
//        });
        BizPlatformDataOverviewDto statisticsTotal = new BizPlatformDataOverviewDto();
        statisticsTotal.setProcurement(totalProcurement);
        statisticsTotal.setJointOrderPlanAmountTotal(totalJointOrderPlanAmountTotal);
        statisticsTotal.setJointOrderAmountTotal(totalJointOrderAmountTotal);
        statisticsTotal.setPurchaseOrderPlanAmountTotal(totalPurchaseOrderPlanAmountTotal);
        statisticsTotal.setPurchaseOrderAmountTotal(totalPurchaseOrderAmountTotal);
        statisticsTotal.setReceiveTotal(totalReceiveTotal);
        statisticsTotal.setAccumulatedSalesMonth(totalAccumulatedSalesMonth);
        statisticsTotal.setProcurementDay(totalProcurementDay);
        statisticsTotal.setNewUser(totalNewUser);
        statisticsTotal.setServiceChargePlan(totalServiceChargePlan);
        statisticsTotal.setServiceCharge(totalServiceCharge);
        statisticsTotal.setStockAmount(totalStockAmount);
        statisticsTotal.setCurrentDate(totalCurrentDate);
        List<BizPlatformDataOverviewDto> statisticsTotalList = new ArrayList<BizPlatformDataOverviewDto>();
        statisticsTotalList.add(statisticsTotal);
        resultMap.put("statisticsTotal", statisticsTotalList);

        return resultMap;
    }

    /**
     * 根据采购商ID 取采购频率的原始数据
     * @param custId
     * @return
     */
    public List<Integer> findOrderCountFrequency(Integer custId) {
        return bizOrderHeaderDao.findOrderCountFrequency(custId, OrderHeaderBizStatusEnum.INVALID_STATUS);
    }

    public Map<String, Object> getReceiveData(String startDate, String endDate, String centerType) {
        List<BizOrderStatisticsDto> dataList = bizPayRecordService.getReceiveData(startDate, endDate + " 23:59:59", centerType);

        Set<String> officeNameSet = Sets.newHashSet();
        Map<String, Object> dataMap = Maps.newHashMap();
        Map<String, Object> resultMap = Maps.newHashMap();
        dataList.forEach(o -> {
            officeNameSet.add(o.getOfficeName());
            dataMap.put(o.getOfficeName(), o.getReceiveTotal());
        });
        officeNameSet.removeAll(Collections.singleton(null));

        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        if (dataMap.size() > 0) {
            List<Object> resultDataList = Lists.newArrayList();
            officeNameSet.forEach(o -> {
                resultDataList.add(dataMap.get(o));
            });
            echartsSeriesDto.setData(resultDataList);

            EchartsSeriesDto.ItemStyle itemStyle = new EchartsSeriesDto.ItemStyle();
            EchartsSeriesDto.Normal normal = new EchartsSeriesDto.Normal();
            EchartsSeriesDto.Label label = new EchartsSeriesDto.Label();
            label.setShow(true);
            label.setTextStyle(
                    "fontWeight:'bolder'," +
                            "fontSize : '12'," +
                            "position : 'top'," +
                            "fontFamily : '微软雅黑'");
            normal.setLabel(label);
            itemStyle.setNormal(normal);
            echartsSeriesDto.setItemStyle(itemStyle);
        }
        resultMap.put("echartsSeriesDto", echartsSeriesDto);
        resultMap.put("officeNameSet", officeNameSet);
        resultMap.put("ret", CollectionUtils.isNotEmpty(dataList));
        return resultMap;
    }

    public Map<String, Object> singleReceiveData(String startDate, String endDate, String officeId) {
        List<BizOrderStatisticsDto> dataList = bizPayRecordService.getSingleReceiveData(startDate, endDate + " 23:59:59", officeId);

        Set<String> officeNameSet = Sets.newHashSet();
        Map<String, Object> dataMap = Maps.newHashMap();
        Map<String, Object> resultMap = Maps.newHashMap();
        dataList.forEach(o -> {
            officeNameSet.add(o.getOfficeName());
            dataMap.put(o.getOfficeName(), o.getReceiveTotal());
        });
        officeNameSet.removeAll(Collections.singleton(null));

        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        if (dataMap.size() > 0) {
            List<Object> resultDataList = Lists.newArrayList();
            officeNameSet.forEach(o -> {
                resultDataList.add(dataMap.get(o));
            });
            echartsSeriesDto.setData(resultDataList);

            EchartsSeriesDto.ItemStyle itemStyle = new EchartsSeriesDto.ItemStyle();
            EchartsSeriesDto.Normal normal = new EchartsSeriesDto.Normal();
            EchartsSeriesDto.Label label = new EchartsSeriesDto.Label();
            label.setShow(true);
            label.setTextStyle(
                    "fontWeight:'bolder'," +
                            "fontSize : '12'," +
                            "position : 'top'," +
                            "fontFamily : '微软雅黑'");
            normal.setLabel(label);
            itemStyle.setNormal(normal);
            echartsSeriesDto.setItemStyle(itemStyle);
        }
        resultMap.put("echartsSeriesDto", echartsSeriesDto);
        resultMap.put("officeNameSet", officeNameSet);
        resultMap.put("ret", CollectionUtils.isNotEmpty(dataList));
        return resultMap;
    }

    public Map<String, Object> singleUserRegisterData(String startDate, String endDate, String officeId) {
        List<BizUserStatisticsDto> dataList = officeDao.singleUserRegisterData(startDate, endDate + " 23:59:59", officeId);

        Set<String> officeNameSet = Sets.newHashSet();
        Map<String, Object> dataMap = Maps.newHashMap();
        Map<String, Object> resultMap = Maps.newHashMap();
        dataList.forEach(o -> {
            officeNameSet.add(o.getName());
            dataMap.put(o.getName(), o.getCount());
        });
        officeNameSet.removeAll(Collections.singleton(null));

        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        if (dataMap.size() > 0) {
            List<Object> resultDataList = Lists.newArrayList();
            officeNameSet.forEach(o -> {
                resultDataList.add(dataMap.get(o));
            });
            echartsSeriesDto.setData(resultDataList);

            EchartsSeriesDto.ItemStyle itemStyle = new EchartsSeriesDto.ItemStyle();
            EchartsSeriesDto.Normal normal = new EchartsSeriesDto.Normal();
            EchartsSeriesDto.Label label = new EchartsSeriesDto.Label();
            label.setShow(true);
            label.setTextStyle(
                    "fontWeight:'bolder'," +
                            "fontSize : '12'," +
                            "position : 'top'," +
                            "fontFamily : '微软雅黑'");
            normal.setLabel(label);
            itemStyle.setNormal(normal);
            echartsSeriesDto.setItemStyle(itemStyle);
        }
        resultMap.put("echartsSeriesDto", echartsSeriesDto);
        resultMap.put("officeNameSet", officeNameSet);
        resultMap.put("ret", CollectionUtils.isNotEmpty(dataList));
        return resultMap;
    }
}

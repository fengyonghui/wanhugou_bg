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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

            o.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
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
                OfficeTypeEnum.NETWORKSUPPLY.getType()
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

        Map<String, List<BizPlatformDataOverviewDto>> resultMap = Maps.newHashMap();
        bizPlatformDataOverviewDtos.forEach(o -> {
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

            BizUserStatisticsDto userStatisticDataByOfficeId = bizOrderHeaderDao.getUserStatisticDataByOfficeId(sdfMonth.format(sDate), o.getOfficeId());
            o.setNewUser(userStatisticDataByOfficeId == null ? BigDecimal.ZERO : BigDecimal.valueOf(userStatisticDataByOfficeId.getCount()));
            o.setNewUserPlan(bizOpPlan.getNewUser() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bizOpPlan.getNewUser()));
            BizOrderStatisticsDto serviceChargeDto = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeMonthOfficeId(sdfMonth.format(sDate) + "%", o.getOfficeId());
            o.setServiceCharge(serviceChargeDto == null ? BigDecimal.ZERO : serviceChargeDto.getProfitPrice());
            o.setServiceChargePlan(bizOpPlan.getServiceCharge() == null ? BigDecimal.ZERO : BigDecimal.valueOf(bizOpPlan.getServiceCharge()));

            o.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
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

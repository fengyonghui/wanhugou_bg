package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.plan.BizOpPlanDao;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
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

    /**
     * 请求参数日期格式
     */
    public static final String PARAM_DATE_FORMAT = "yyyy-MM";


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
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null);
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
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, id);
    }


    /**
     * 获取个人平台业务数据
     */
    public Map<String, List<BizPlatformDataOverviewDto>> getSinglePlatformData(String startDate, String endDate, Integer officeId) {
        String[] dateStrArr = startDate.split("-");

        List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = bizOrderHeaderDao.singlePlatformDataOverview(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, officeId);
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
            List<BizOrderStatisticsDto> currentBizOrderStatisticsDtoList = Lists.newArrayList();
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
    public Map<String, List<BizPlatformDataOverviewDto>> getPlatformData(String startDate, String endDate, String currentDate) {
        String[] dateStrArr = startDate.split("-");

        List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = bizOrderHeaderDao.platformDataOverview(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS);
        Set<String> nameSet = Sets.newHashSet();
        for (BizPlatformDataOverviewDto b : bizPlatformDataOverviewDtos) {
            nameSet.add(b.getName());
        }


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
}

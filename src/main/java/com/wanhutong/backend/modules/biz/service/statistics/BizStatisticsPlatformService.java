package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventoryInfoDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.plan.BizOpPlanDao;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.entity.plan.BizOpPlan;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.dao.SysRegionDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


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
    private SysRegionDao sysRegionDao;

    @Resource
    private BizOpPlanDao bizOpPlanDao;

    @Resource
    private BizInventoryInfoDao bizInventoryInfoDao;

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
        if (StringUtils.isBlank(centerType) || Integer.valueOf(centerType) == 0) {
            return bizOrderHeaderDao.getAllValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null);
        }
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
     * 获取平台业务数据
     */
    public Map<String, List<BizPlatformDataOverviewDto>> getPlatformData(String startDate, String endDate, String currentDate) {
        String[] dateStrArr = startDate.split("-");
        List<Office> listByType = officeDao.findListByType("8");
        listByType.removeIf(o -> o.getName().contains("测试"));
        Map<String, List<BizPlatformDataOverviewDto>> resultMap = Maps.newHashMap();
        listByType.forEach(o -> {
            SysRegion sysRegion = sysRegionDao.queryOfficeProvinceById(o.getId());

            List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = orderStatisticDataByOffice(startDate, endDate + " 23:59:59", null, null, null, o.getId());
            List<BizOrderStatisticsDto> currentBizOrderStatisticsDtoList = orderStatisticDataByOffice(currentDate, currentDate + " 23:59:59", null, null, null, o.getId());
            BizPlatformDataOverviewDto bizPlatformDataOverviewDto = new BizPlatformDataOverviewDto();
            BizOrderStatisticsDto bizOrderStatisticsDto = null;
            if (CollectionUtils.isNotEmpty(bizOrderStatisticsDtoList)) {
                bizOrderStatisticsDto = bizOrderStatisticsDtoList.get(0);
            }
            bizPlatformDataOverviewDto.setName(o.getName());
            // 采购额
            BizOpPlan bizOpPlan = new BizOpPlan();
            bizOpPlan.setObjectName("sys_office");
            bizOpPlan.setObjectId(String.valueOf(o.getId()));
            bizOpPlan.setYear(dateStrArr[0]);
            bizOpPlan.setMonth(dateStrArr[1]);
            bizOpPlan.setDay("0");
            List<BizOpPlan> planList = bizOpPlanDao.findList(bizOpPlan);
            if (CollectionUtils.isNotEmpty(planList)) {
                bizOpPlan = planList.get(0);
            }

            bizPlatformDataOverviewDto.setProcurement(new BigDecimal(bizOpPlan.getAmount() == null ? "0" : bizOpPlan.getAmount()));
            bizPlatformDataOverviewDto.setAccumulatedSalesMonth(bizOrderStatisticsDto == null ? BigDecimal.ZERO :bizOrderStatisticsDto.getTotalMoney());
            bizPlatformDataOverviewDto.setProcurementDay(
                    CollectionUtils.isEmpty(currentBizOrderStatisticsDtoList) ?
                            BigDecimal.ZERO
                            : currentBizOrderStatisticsDtoList.get(0).getTotalMoney());
            // 库存金额
            StockAmountDto stockAmountByCustId = bizInventoryInfoDao.getStockAmountByCustId(o.getId());
            bizPlatformDataOverviewDto.setStockAmount(new BigDecimal(stockAmountByCustId == null ? "0" : stockAmountByCustId.getStockAmount()));
            bizPlatformDataOverviewDto.setCurrentDate(endDate);
            List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = resultMap.putIfAbsent(sysRegion == null ? "未知" : sysRegion.getName(), Lists.newArrayList(bizPlatformDataOverviewDto));
            if (bizPlatformDataOverviewDtos != null) {
                bizPlatformDataOverviewDtos.add(bizPlatformDataOverviewDto);
            }
        });
        return resultMap;
    }
}

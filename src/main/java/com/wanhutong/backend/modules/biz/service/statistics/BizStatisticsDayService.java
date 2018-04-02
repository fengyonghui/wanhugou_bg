package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.biz.dao.category.BizVarietyInfoDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class BizStatisticsDayService {

    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;

    @Resource
    private BizVarietyInfoDao bizVarietyInfoDao;
    @Resource
    private OfficeDao officeDao;


    /**
     * 日统计请求参数日期格式
     */
    public static final String DAY_PARAM_DATE_FORMAT = "yyyy-MM-dd";


    /**
     * 根据月份取产品统计相关数据
     *
     * @param date 取数据的月份
     * @return 根据不同产品分类的统计数据
     */
    public List<BizProductStatisticsDto> productStatisticData (String date, Integer variId, Integer purchasingId) {
        return bizOrderHeaderDao.getProductStatisticDataDay(date, variId, purchasingId);
    }


    /**
     * 根据月份取订单统计相关数据
     *
     * @param date 取数据的日期
     * @return 根据不同机构分类的统计数据
     */
    public Map<String, BizOrderStatisticsDto> orderStatisticData(String date, String centerType) {
        List<BizOrderStatisticsDto> orderTotalAndCountByCreateTimeMonthStatus = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeDay(date, OrderHeaderBizStatusEnum.INVALID_STATUS, centerType);
        Map<String, BizOrderStatisticsDto> resultMap = Maps.newHashMap();
        orderTotalAndCountByCreateTimeMonthStatus.forEach(o -> {
            resultMap.putIfAbsent(o.getOfficeName(), o);
        });
        return resultMap;
    }


    /**
     * 封装成页面图表所需要的数据格式
     *
     * @param officeNameSet 所有机构名称
     * @param dataMap       数据集合
     * @param date         选择的日期
     * @return 封装数据
     */
    public EchartsSeriesDto genEchartsSeriesDto(Set<String> officeNameSet, Map<String, BizOrderStatisticsDto> dataMap, LocalDateTime date, String barChartType) {
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
            List<Object> dataList = Lists.newArrayList();
        if (dataMap.size() > 0) {
            officeNameSet.forEach(o -> {
                BizOrderStatisticsDto bizOrderStatisticsDto = dataMap.get(o);
                switch (OrderStatisticsDataTypeEnum.parse(StringUtils.isNotBlank(barChartType) ? Integer.valueOf(barChartType) : 1)) {
                    case SALES_GROWTH_RATE:
                        break;
                    case SALEROOM:
                        dataList.add(bizOrderStatisticsDto != null ? bizOrderStatisticsDto.getTotalMoney() : 0);
                        break;
                    case ORDER_COUNT:
                        dataList.add(bizOrderStatisticsDto != null ? bizOrderStatisticsDto.getOrderCount() : 0);
                        break;
                    default:
                        break;
                }
            });
            echartsSeriesDto.setData(dataList);
        }else {
            echartsSeriesDto.setData(Lists.newArrayList());
        }
        echartsSeriesDto.setName(date.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT));
        return echartsSeriesDto;
    }

    /**
     * 根据月份取用户统计相关数据
     *
     * @param day 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizUserStatisticsDto> userStatisticData(String day) {
        return bizOrderHeaderDao.getUserStatisticDataDay(day);
    }

    /**
     * 根据月份取用户业绩统计相关数据
     *
     * @param day 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    public List<BizUserSaleStatisticsDto> userSaleStatisticData(String day, Integer purchasingId) {
        return bizOrderHeaderDao.getUserSaleStatisticDataDay(day, purchasingId, null, OrderHeaderBizStatusEnum.INVALID_STATUS);
    }

    /**
     * 取所有商品类别
     * @return 所有商品类别
     */
    public List<BizVarietyInfo> getBizVarietyInfoList() {
        return bizVarietyInfoDao.findAllList(new BizVarietyInfo());
    }
    /**
     * 取所有采购商 type = 8
     * @return 所有采购商
     */
    public List<Office> getBizPurchasingList(String type) {
        return officeDao.findListByType(type);
    }

    /**
     * 取某一个销售的销售业绩
     * @param salesmanId 销售员ID
     */
    public List<BizUserSaleStatisticsDto> singleUserSaleStatisticData(Integer salesmanId) {
        return bizOrderHeaderDao.getSingleUserSaleStatisticDataDay(null, null, salesmanId, OrderHeaderBizStatusEnum.INVALID_STATUS);
    }
}

package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.biz.dao.category.BizVarietyInfoDao;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.totalStatistics.BizTotalStatisticsDao;
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
public class BizStatisticsService {

    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;

    @Resource
    private BizVarietyInfoDao bizVarietyInfoDao;
    @Resource
    private OfficeDao officeDao;
    @Resource
    private BizTotalStatisticsDao bizTotalStatisticsDao;

    /**
     * 请求参数日期格式
     */
    public static final String PARAM_DATE_FORMAT = "yyyy-MM";


    /**
     * 根据月份取产品统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同产品分类的统计数据
     */
    public List<BizProductStatisticsDto> productStatisticData (String month, Integer variId, Integer purchasingId) {
        return bizOrderHeaderDao.getProductStatisticData(month, variId, purchasingId);
    }


    /**
     * 根据月份取订单统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public Map<String, BizOrderStatisticsDto> orderStatisticData(String month) {
        List<BizOrderStatisticsDto> orderTotalAndCountByCreateTimeMonthStatus = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeMonth(month, OrderHeaderBizStatusEnum.VALID_STATUS, OfficeTypeEnum.PURCHASINGCENTER.getType());
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
     * @param month         选择的日期
     * @return 封装数据
     */
    public EchartsSeriesDto genEchartsSeriesDto(Set<String> officeNameSet, Map<String, BizOrderStatisticsDto> dataMap, LocalDateTime month, String barChartType) {
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        if (dataMap.size() > 0) {
            List<Object> dataList = Lists.newArrayList();
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
            echartsSeriesDto.setName(month.toString(BizStatisticsService.PARAM_DATE_FORMAT));
            return echartsSeriesDto;
        }
        return null;
    }

    /**
     * 根据月份取用户统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizUserStatisticsDto> userStatisticData(String month) {
        return bizOrderHeaderDao.getUserStatisticData(month);
    }

    /**
     * 根据月份取用户业绩统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    public List<BizUserSaleStatisticsDto> userSaleStatisticData(String month, Integer purchasingId) {
        return bizOrderHeaderDao.getUserSaleStatisticData(month, purchasingId, null);
    }
    /**
     * 根据月份和采购中心取客户专员数据
     * @param month
     * @param purchasingId
     * @return
     */
    public List<BizUserSaleStatisticsDto> userTableStatisticData(String month, Integer purchasingId) {
        return bizOrderHeaderDao.getUserTableStatisticData(month, purchasingId);
    }

    /**
     * 取所有商品类别
     * @return 所有商品类别
     */
    public List<BizVarietyInfo> getBizVarietyInfoList() {
        return bizVarietyInfoDao.findAllList(new BizVarietyInfo());
    }
    /**
     * 根据type取 office
     * @return office
     */
    public List<Office> getOfficeList(String type) {
        Office office = new Office();
        office.setType(type);
        List<Office> list = officeDao.findList(office);
        list.removeIf(o -> o.getName().contains("测试".intern()));
        return list;
    }

    /**
     * 取某一个销售的销售业绩
     * @param salesmanId 销售员ID
     */
    public List<BizUserSaleStatisticsDto> singleUserSaleStatisticData(Integer salesmanId) {
        return bizOrderHeaderDao.getSingleUserSaleStatisticData(null, null, salesmanId);
    }

    public BizTotalStatisticsDto getBizTotalStatisticsDto(){
        BizTotalStatisticsDto totalStatisticsDto = bizTotalStatisticsDao.getTotalStatisticsDto();
        Integer skuCount = bizTotalStatisticsDao.getTotalSkuCount();
        Integer custCount = bizTotalStatisticsDao.getTotalCustCount();
        Integer centCount = bizTotalStatisticsDao.getTotalCentCount();
        Integer orderCount = totalStatisticsDto.getOrderCount();
        totalStatisticsDto.setCustCount(custCount);
        totalStatisticsDto.setCentCount(centCount);
        totalStatisticsDto.setSkuCount(skuCount);
        totalStatisticsDto.setAvgPrice(totalStatisticsDto.getReceiveMoney().divide(new BigDecimal(orderCount),2,BigDecimal.ROUND_HALF_UP));

        return totalStatisticsDto;
    }
}

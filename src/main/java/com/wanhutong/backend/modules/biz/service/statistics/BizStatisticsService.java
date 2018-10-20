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
import java.util.*;


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
    public Map<String, BizOrderStatisticsDto> orderStatisticData(String month, String centerType) {
        List<BizOrderStatisticsDto> orderTotalAndCountByCreateTimeMonthStatus = bizOrderHeaderDao.getValidOrderTotalAndCountByCreateTimeMonth(
                month + "%",
                OrderHeaderBizStatusEnum.INVALID_STATUS,
                centerType
        );
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
                    case PROFIT:
                        dataList.add(bizOrderStatisticsDto != null ? bizOrderStatisticsDto.getProfitPrice() : 0);
                        break;
                    case UNIVALENCE:
                        dataList.add(bizOrderStatisticsDto != null ? bizOrderStatisticsDto.getUnivalence() : 0);
                        break;
                    case RECEIVE:
                        dataList.add(bizOrderStatisticsDto != null ? bizOrderStatisticsDto.getReceiveTotal() : 0);
                        break;
                    default:
                        break;
                }
            });
            echartsSeriesDto.setData(dataList);
            echartsSeriesDto.setName(month.toString(BizStatisticsService.PARAM_DATE_FORMAT));

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
     * 根据月份取用户统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public BizUserStatisticsDto userStatisticDataByOfficeId(String month, Integer officeId) {
        return bizOrderHeaderDao.getUserStatisticDataByOfficeId(month, officeId);
    }

    /**
     * 根据月份取用户业绩统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同用户分类的统计数据
     */
    public List<BizUserSaleStatisticsDto> userSaleStatisticData(String month, Integer purchasingId) {
        return bizOrderHeaderDao.getUserSaleStatisticData(StringUtils.isBlank(month) ? null : month + "%", purchasingId, null, OrderHeaderBizStatusEnum.INVALID_STATUS);
    }
    /**
     * 根据月份和采购中心取客户专员数据
     * @param month
     * @param purchasingId
     * @return
     */
    public List<BizUserSaleStatisticsDto> userTableStatisticData(String month, Integer purchasingId) {
        return bizOrderHeaderDao.getUserTableStatisticData(month, purchasingId, OrderHeaderBizStatusEnum.INVALID_STATUS);
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
        List<Office> list = officeDao.findListByType(type);
        list.removeIf(o -> o.getName().contains("测试".intern()));
        return list;
    }
    /**
     * 根据type取 office
     * @return office
     */
    public List<Office> getOfficeList(String... type) {
        List<Office> list = officeDao.findListByTypeList(Lists.newArrayList(type));
        list.removeIf(o -> o.getName().contains("测试".intern()));
        return list;
    }

    /**
     * 取某一个销售的销售业绩
     * @param salesmanId 销售员ID
     */
    public List<BizUserSaleStatisticsDto> singleUserSaleStatisticData(Integer salesmanId) {
        return bizOrderHeaderDao.getSingleUserSaleStatisticData(null, salesmanId, OrderHeaderBizStatusEnum.INVALID_STATUS);
    }

    public Map<String,BizTotalStatisticsDto> getBizTotalStatisticsDto(){
        BizTotalStatisticsDto totalStatisticsDto = bizTotalStatisticsDao.getTotalStatisticsDto();
        Integer skuCount = bizTotalStatisticsDao.getTotalSkuCount();
        Integer custCount = bizTotalStatisticsDao.getTotalCustCount();
        Integer centCount = bizTotalStatisticsDao.getTotalCentCount(Integer.parseInt(OfficeTypeEnum.PURCHASINGCENTER.getType()));
        Integer orderCount = totalStatisticsDto.getOrderCount();
        totalStatisticsDto.setCustCount(custCount);
        totalStatisticsDto.setCentCount(centCount);
        totalStatisticsDto.setSkuCount(skuCount);
        totalStatisticsDto.setAvgPrice(totalStatisticsDto.getReceiveMoney().divide(new BigDecimal(orderCount),2,BigDecimal.ROUND_HALF_UP));

        Map<String,BizTotalStatisticsDto> map = new HashMap<>();

        BizTotalStatisticsDto one = new BizTotalStatisticsDto();
        one.setUnit("个");
        one.setCount(totalStatisticsDto.getCustCount().toString());
        map.put("会员总数",one);
        BizTotalStatisticsDto two = new BizTotalStatisticsDto();
        two.setCount(((Integer)(totalStatisticsDto.getCentCount()-2)).toString());
        two.setUnit("个");
        map.put("采购中心数",two);
        BizTotalStatisticsDto three = new BizTotalStatisticsDto();
        three.setCount(totalStatisticsDto.getSkuCount().toString());
        three.setUnit("件");
        map.put("商品数量",three);
        BizTotalStatisticsDto four = new BizTotalStatisticsDto();
        four.setCount(totalStatisticsDto.getOrderCount().toString());
        four.setUnit("单");
        map.put("订单数",four);
        BizTotalStatisticsDto five = new BizTotalStatisticsDto();
        five.setCount(totalStatisticsDto.getTotalMoney().toString());
        five.setUnit("元");
        map.put("总额",five);
        BizTotalStatisticsDto six = new BizTotalStatisticsDto();
        six.setCount(totalStatisticsDto.getReceiveMoney().toString());
        six.setUnit("元");
        map.put("已收货款",six);
        BizTotalStatisticsDto seven = new BizTotalStatisticsDto();
        seven.setCount(totalStatisticsDto.getAvgPrice().toString());
        seven.setUnit("元");
        map.put("平均客单价",seven);
        return map;
    }
}

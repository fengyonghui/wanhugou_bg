package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsBetweenService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsDayService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 统计相关Controller
 *
 * @author Ma.Qiang
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/statistics/between")
public class BizStatisticsBetweenController extends BaseController {

    @Resource
    private BizStatisticsBetweenService bizStatisticsBetweenService;

    /**
     * 查询数据月数
     */
    private static final int DATA_DAY_COUNT = 1;

    /**
     * 默认除法运算精度
     */
    private static final int DEF_DIV_SCALE = 2;

    /**
     * 最大数据长度
     */
    private static final int MAX_DATA_LENGTH = 10;


    /**
     * 用户业绩相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"userSale", ""})
    public String userSale(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsBetweenService.getBizPurchasingList("8"));

        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsUserSaleBetween";
    }

    /**
     * 用户业绩相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"userSaleData", ""})
    @ResponseBody
    public String userSaleData(HttpServletRequest request, String startDate, String endDate, Integer purchasingId, String usName) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }

        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);
        List<String> nameList = Lists.newArrayList();
        Map<String, Integer> usNameIdMap = Maps.newHashMap();

        List<Object> seriesDataList = Lists.newArrayList();
        List<Object> seriesTotalDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        EchartsSeriesDto echartsSeriesTotalDto = new EchartsSeriesDto();
        bizProductStatisticsDtos.forEach(o -> {
            seriesDataList.add(o.getOrderCount());
            seriesTotalDataList.add(o.getTotalMoney());
            nameList.add(o.getName());
            usNameIdMap.put(o.getName(), o.getUsId());
        });
        echartsSeriesDto.setName("订单量");
        echartsSeriesDto.setData(seriesDataList);

        echartsSeriesTotalDto.setName("销售额");
        echartsSeriesTotalDto.setData(seriesTotalDataList);
        echartsSeriesTotalDto.setyAxisIndex(1);

        // 封装某一个用户的业绩图
        Integer usId = null;
        if (StringUtils.isNotBlank(usName)) {
            usId = usNameIdMap.get(usName);
        }
        if (usId == null && CollectionUtils.isNotEmpty(bizProductStatisticsDtos)) {
            usId = bizProductStatisticsDtos.get(0).getUsId();
            usName = bizProductStatisticsDtos.get(0).getName();
        }

        Map<String, Object> paramMap = Maps.newHashMap();

        List<BizUserSaleStatisticsDto> bizUserSaleStatisticsDtos = null;
        if (usId != null) {
            bizUserSaleStatisticsDtos = bizStatisticsBetweenService.singleUserSaleStatisticData(startDate, endDate, usId);
            if (bizUserSaleStatisticsDtos.size() > MAX_DATA_LENGTH) {
                bizUserSaleStatisticsDtos = bizUserSaleStatisticsDtos.subList(0, MAX_DATA_LENGTH);
            }
            List<Object> singleSeriesDataList = Lists.newArrayList();
            List<Object> singleSeriesTotalDataList = Lists.newArrayList();
            EchartsSeriesDto singleEchartsSeriesDto = new EchartsSeriesDto();
            EchartsSeriesDto singleEchartsSeriesTotalDto = new EchartsSeriesDto();
            List<String> monthList = Lists.newArrayList();
            bizUserSaleStatisticsDtos.forEach(o -> {
                singleSeriesDataList.add(o.getOrderCount());
                singleSeriesTotalDataList.add(o.getTotalMoney());
                monthList.add(o.getCreateTime());
            });

            singleEchartsSeriesDto.setName("订单量");
            singleEchartsSeriesDto.setData(singleSeriesDataList);

            singleEchartsSeriesTotalDto.setName("销售额");
            singleEchartsSeriesTotalDto.setData(singleSeriesTotalDataList);
            singleEchartsSeriesTotalDto.setyAxisIndex(1);
            paramMap.put("singleSeriesList", Lists.newArrayList(singleEchartsSeriesDto, singleEchartsSeriesTotalDto));
            paramMap.put("monthList", monthList);

        }

        paramMap.put("seriesList", Lists.newArrayList(echartsSeriesDto, echartsSeriesTotalDto));
        paramMap.put("usName", usName);
        List<String> selectNameList = Lists.newArrayList(nameList);
        if (StringUtils.isNotBlank(usName)) {
            selectNameList.remove(usName);
        }
        paramMap.put("selectNameList", selectNameList);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return JSONObject.fromObject(paramMap).toString();
    }


    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"user", ""})
    public String user(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsUserBetween";
    }

    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"userData", ""})
    @ResponseBody
    public String userData(HttpServletRequest request, String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userStatisticData(startDate, endDate);
        List<String> nameList = Lists.newArrayList();

        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        bizProductStatisticsDtos.forEach(o -> {
            seriesDataList.add(o.getCount());
            nameList.add(o.getName());
        });
        echartsSeriesDto.setName("用户量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return JSONObject.fromObject(paramMap).toString();
    }


    /**
     * 产品相关统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:product:view")
    @RequestMapping(value = {"product", ""})
    public String product(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("varietyList", bizStatisticsBetweenService.getBizVarietyInfoList());
        request.setAttribute("purchasingList", bizStatisticsBetweenService.getBizPurchasingList("8"));
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsProductBetween";
    }

    /**
     * 产品相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:product:view")
    @RequestMapping(value = {"productData", ""})
    @ResponseBody
    public String product(HttpServletRequest request, String startDate, String endDate, Integer variId, int dataType, Integer purchasingId) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        // 月份集合
        List<BizProductStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.productStatisticData(startDate, endDate, variId, purchasingId);
        List<String> nameList = Lists.newArrayList();

        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        bizProductStatisticsDtos.forEach(o -> {
            switch (OrderStatisticsDataTypeEnum.parse(dataType)) {
                case SALEROOM:
                    seriesDataList.add(o.getTotalMoney());
                    break;
                case ORDER_COUNT:
                    seriesDataList.add(o.getCount());
                    break;
                default:
                    break;
            }

            nameList.add(o.getName().concat("-").concat(o.getItemNo()));
        });
        echartsSeriesDto.setName("商品销量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return JSONObject.fromObject(paramMap).toString();
    }


    /**
     * 订单相关统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"order", ""})
    public String order(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsOrderBetween";
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate     开始时间
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderData", ""})
    @ResponseBody
    public String orderData(HttpServletRequest request, String startDate, String endDate, String lineChartType, String barChartType) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        return JSONObject.fromObject(getOrderData(startDate, endDate, lineChartType, barChartType)).toString();
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate     开始时间
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderData(String startDate, String endDate, String lineChartType, String barChartType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long betweenDays = 6L;
        try {
            Date startDateObject = sdf.parse(startDate);
            Date endDateObject = sdf.parse(endDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDateObject);
            long time1 = cal.getTimeInMillis();
            cal.setTime(endDateObject);
            long time2 = cal.getTimeInMillis();
            betweenDays = (time2 - time1) / (1000 * 3600 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long finalBetweenDays = betweenDays;
        // 月份集合
        List<LocalDateTime> monthDateList = Lists.newArrayList();
        LocalDateTime selectMonth = StringUtils.isBlank(startDate) ? LocalDateTime.now() : LocalDateTime.parse(startDate);
        monthDateList.add(selectMonth);

        // 主要数据集合
        Map<String, Map<String, BizOrderStatisticsDto>> dataMap = Maps.newLinkedHashMap();
        // 月份字符串集合
        List<String> monthList = Lists.newArrayList();
        monthDateList.forEach(o -> {
            dataMap.put(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                    bizStatisticsBetweenService.orderStatisticData(
                            o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                            o.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)));
            monthList.add(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT));
        });
        Collections.reverse(monthList);

        Set<String> officeNameSet = Sets.newHashSet();
        monthDateList.forEach(o -> {
            officeNameSet.addAll(dataMap.get(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)) != null ? dataMap.get(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).keySet() : CollectionUtils.EMPTY_COLLECTION);
        });
        officeNameSet.removeAll(Collections.singleton(null));

        Map<String, Object> paramMap = Maps.newHashMap();

        paramMap.put("officeNameSet", officeNameSet);
        paramMap.put("monthList", monthList);

        // echarts 数据实体
        ArrayList<EchartsSeriesDto> seriesList = Lists.newArrayList();
        for (int i = dataMap.size() - 1; i >= 0; i--) {
            seriesList.add(bizStatisticsBetweenService.genEchartsSeriesDto(officeNameSet, dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)), monthDateList.get(i), barChartType));
        }

        seriesList.removeAll(Collections.singleton(null));

        paramMap.put("seriesList", seriesList);

        ArrayList<EchartsSeriesDto> lineSeriesList = Lists.newArrayList();

        switch (OrderStatisticsDataTypeEnum.parse(StringUtils.isNotBlank(lineChartType) ? Integer.valueOf(lineChartType) : 1)) {
            case SALEROOM:
                officeNameSet.forEach(o -> {
                    EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
                    echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

                    List<Object> dataList = Lists.newArrayList();
                    for (int i = dataMap.size() - 1; i >= 0; i--) {
                        dataList.add(dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o) != null ? dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o).getTotalMoney() : 0);
                    }

                    echartsSeriesDto.setData(dataList);
                    echartsSeriesDto.setName(o);
                    lineSeriesList.add(echartsSeriesDto);
                });
                break;
            case SALES_GROWTH_RATE:
                // 销售额增长率
                officeNameSet.forEach(o -> {
                    EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
                    echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

                    List<Object> dataList = Lists.newArrayList();

                    for (int i = dataMap.size() - 1; i >= 0; i--) {
                        // 当前日期
                        LocalDateTime currentMonth = monthDateList.get(i);
                        // 当前日期数据
                        BigDecimal currentData = dataMap.get(currentMonth.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o) != null ? dataMap.get(currentMonth.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o).getTotalMoney() : BigDecimal.valueOf(0);
                        // 前一天
                        LocalDateTime lastMonth = currentMonth.minusDays(i);
                        // 前一天数据
                        Map<String, BizOrderStatisticsDto> lastDataMap = dataMap.get(lastMonth.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT));
                        if (lastDataMap == null) {
                            lastDataMap = bizStatisticsBetweenService.orderStatisticData(
                                    lastMonth.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                                    lastMonth.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT));
                        }
                        BigDecimal lastData = lastDataMap.get(o) != null ? lastDataMap.get(o).getTotalMoney() : BigDecimal.valueOf(0);
                        // 增长率
                        BigDecimal growthRate = lastData.intValue() == 0 ? BigDecimal.valueOf(0) : currentData.subtract(lastData).divide(lastData, DEF_DIV_SCALE).multiply(BigDecimal.valueOf(100));
                        dataList.add(growthRate);
                    }
                    echartsSeriesDto.setData(dataList);
                    echartsSeriesDto.setName(o);
                    lineSeriesList.add(echartsSeriesDto);
                });
                break;
            default:
                break;
        }

        paramMap.put("rateSeriesList", lineSeriesList);
        paramMap.put("dataMap", dataMap);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesList));

        return paramMap;
    }

    /**
     * 商品新增数量统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:sku:view")
    @RequestMapping(value = {"sku", ""})
    public String sku(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("varietyList", bizStatisticsBetweenService.getBizVarietyInfoList());
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        /*Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));*/
        return "modules/biz/statistics/bizStatisticsSkuBetween";
    }

    /**
     * 产品相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:sku:view")
    @RequestMapping(value = {"skuData", ""})
    @ResponseBody
    public String sku(HttpServletRequest request, String month,String type, Integer variId) throws ParseException {

        List<Object> seriesDataList = Lists.newArrayList();
        List<String> nameList = Lists.newArrayList();
        if (type.equals("1")) {
            String[] week = {"第一周", "第二周", "第三周", "第四周", "第五周"};
            for (int i = 0; i < week.length; i++) {
                nameList.add(week[i]);
            }
            // 月份下星期
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            seriesDataList.add(bizStatisticsBetweenService.skuStatisticData(month + "-01", month + "-07", variId));
            Date firstOpenDate = sdf.parse(month + "-01");
            Date firstCloseDate = sdf.parse(month + "-07");
            cal.setTime(firstOpenDate);
            cal.add(Calendar.DATE, 7);
            String secondStartDate = sdf.format(cal.getTime());
            cal.setTime(firstCloseDate);
            cal.add(Calendar.DATE, 7);
            String secondEndDate = sdf.format(cal.getTime());
            seriesDataList.add(bizStatisticsBetweenService.skuStatisticData(secondStartDate, secondEndDate, variId));
            cal.setTime(sdf.parse(secondStartDate));
            cal.add(Calendar.DATE, 7);
            String thirdStartDate = sdf.format(cal.getTime());
            cal.setTime(sdf.parse(secondEndDate));
            cal.add(Calendar.DATE, 7);
            String thirdEndDate = sdf.format(cal.getTime());
            seriesDataList.add(bizStatisticsBetweenService.skuStatisticData(thirdStartDate, thirdEndDate, variId));
            cal.setTime(sdf.parse(thirdStartDate));
            cal.add(Calendar.DATE, 7);
            String fourthStartDate = sdf.format(cal.getTime());
            cal.setTime(sdf.parse(thirdEndDate));
            cal.add(Calendar.DATE, 7);
            String fourthEndDate = sdf.format(cal.getTime());
            seriesDataList.add(bizStatisticsBetweenService.skuStatisticData(fourthStartDate, fourthEndDate, variId));
            cal.setTime(sdf.parse(fourthStartDate));
            cal.add(Calendar.DATE, 7);
            String fifthStartDate = sdf.format(cal.getTime());
            cal.setTime(sdf.parse(fourthEndDate));
            cal.add(Calendar.DATE, 7);
            String fifthEndDate = sdf.format(cal.getTime());
            seriesDataList.add(bizStatisticsBetweenService.skuStatisticData(fifthStartDate, fifthEndDate, variId));
        }else {
            List<BizProductStatisticsDto> statisticsDtoList = bizStatisticsBetweenService.skuAllStatisticData(variId);
            for (BizProductStatisticsDto bizProductStatisticsDto:statisticsDtoList) {
                seriesDataList.add(bizProductStatisticsDto.getUpSkuCount());
                nameList.add(bizProductStatisticsDto.getMonthDate());
            }
        }

        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        echartsSeriesDto.setName("商品新增数量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("nameList",nameList);
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        System.out.println(JSONObject.fromObject(paramMap).toString());
        return JSONObject.fromObject(paramMap).toString();
    }

}

package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping(value = "${adminPath}/biz/statistics")
public class BizStatisticsController extends BaseController {

    @Resource
    private BizStatisticsService bizStatisticsService;

    /**
     * 查询数据月数
     */
    private static final int DATA_MONTH_COUNT = 3;

    /**
     * 默认除法运算精度
     */
    private static final int DEF_DIV_SCALE = 2;


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
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsUserSale";
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
    public String userSaleData(HttpServletRequest request, String month, Integer purchasingId, String usName) {
        // 月份集合
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);
        List<String> nameList = Lists.newArrayList();
        Map<String, Integer> usNameIdMap = Maps.newHashMap();

        List<Object> seriesDataList = Lists.newArrayList();
        List<String> userMonthList = Lists.newArrayList();
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
            bizUserSaleStatisticsDtos = bizStatisticsService.singleUserSaleStatisticData(usId);

            List<Object> singleSeriesDataList = Lists.newArrayList();
            List<Object> singleSeriesTotalDataList = Lists.newArrayList();
            EchartsSeriesDto singleEchartsSeriesDto = new EchartsSeriesDto();
            EchartsSeriesDto singleEchartsSeriesTotalDto = new EchartsSeriesDto();
            List<String> monthList = Lists.newArrayList();

        bizUserSaleStatisticsDtos.forEach(o -> {
            singleSeriesDataList.add(o.getOrderCount());
            singleSeriesTotalDataList.add(o.getTotalMoney());
            monthList.add(o.getCreateTime());
            userMonthList.add(o.getCreateTime().concat("+").concat(o.getTotalMoney()+"").concat("+").concat(o.getProfitPrice()+"")
                    .concat("+").concat(o.getAddCustCount()+"").concat("+").concat(o.getCustCount()+""));
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
//        singleEchartsSeriesTotalDto.setName("销售额");
//        singleEchartsSeriesTotalDto.setData(singleSeriesTotalDataList);
//        singleEchartsSeriesTotalDto.setyAxisIndex(1);

//        Map<String, Object> paramMap = Maps.newHashMap();
//        paramMap.put("seriesList", Lists.newArrayList(echartsSeriesDto, echartsSeriesTotalDto));
//        paramMap.put("singleSeriesList", Lists.newArrayList(singleEchartsSeriesDto, singleEchartsSeriesTotalDto));
        paramMap.put("usName", usName);
//        paramMap.put("monthList", monthList);
        paramMap.put("userMonthList", userMonthList);
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
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsUser";
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
    public String userData(HttpServletRequest request, String month) {
        // 月份集合
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userStatisticData(month);
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
        request.setAttribute("varietyList", bizStatisticsService.getBizVarietyInfoList());
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsProduct";
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
    public String product(HttpServletRequest request, String month, Integer variId, int dataType, Integer purchasingId) {
        // 月份集合
        List<BizProductStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.productStatisticData(month, variId, purchasingId);
        List<String> nameList = Lists.newArrayList();
        List<String> AllList = Lists.newArrayList();

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
            AllList.add(o.getVendorName().concat("-").concat(o.getItemNo()).concat("-").concat(o.getCount()+"").concat("-")
                .concat(o.getTotalMoney()+"").concat("-").concat(o.getClickCount()+""));
        });
        echartsSeriesDto.setName("商品销量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("AllList", AllList);
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
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsOrder";
    }

    /**
     * 订单相关统计数据
     *
     * @param month         月份
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderData", ""})
    @ResponseBody
    public String orderData(HttpServletRequest request, String month, String lineChartType, String barChartType) {
        return JSONObject.fromObject(getOrderData(month, lineChartType, barChartType)).toString();
    }

    /**
     * 订单相关统计数据
     *
     * @param month         月份
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderData(String month, String lineChartType, String barChartType) {
        // 月份集合
        List<LocalDateTime> monthDateList = Lists.newArrayList();
        LocalDateTime selectMonth = StringUtils.isBlank(month) ? LocalDateTime.now() : LocalDateTime.parse(month);
        monthDateList.add(selectMonth);
        for (int i = 0; i < DATA_MONTH_COUNT; i++) {
            if (i != 0) {
                LocalDateTime localDateTime = selectMonth.minusMonths(i);
                if(localDateTime.isAfter(new LocalDateTime("2017-08-31"))) { // TODO 临时代码
                    monthDateList.add(localDateTime);
                }
            }
        }

        // 主要数据集合
        Map<String, Map<String, BizOrderStatisticsDto>> dataMap = Maps.newLinkedHashMap();
        // 月份字符串集合
        List<String> monthList = Lists.newArrayList();
        monthDateList.forEach(o -> {
            dataMap.put(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), bizStatisticsService.orderStatisticData(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)));
            monthList.add(o.toString(BizStatisticsService.PARAM_DATE_FORMAT));
        });
        Collections.reverse(monthList);

        Set<String> officeNameSet = Sets.newHashSet();
        monthDateList.forEach(o -> {
            officeNameSet.addAll(dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)) != null ? dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)).keySet() : CollectionUtils.EMPTY_COLLECTION);
        });
        officeNameSet.removeAll(Collections.singleton(null));

        Map<String, Object> paramMap = Maps.newHashMap();

        paramMap.put("officeNameSet", officeNameSet);
        paramMap.put("monthList", monthList);

        // echarts 数据实体
        ArrayList<EchartsSeriesDto> seriesList = Lists.newArrayList();
        for (int i = dataMap.size() - 1; i >= 0; i--) {
            seriesList.add(bizStatisticsService.genEchartsSeriesDto(officeNameSet, dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)), monthDateList.get(i), barChartType));
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
                        dataList.add(dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o) != null ? dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o).getTotalMoney() : 0);
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
                        // 当前月
                        LocalDateTime currentMonth = monthDateList.get(i);
                        // 当前月数据
                        BigDecimal currentData = dataMap.get(currentMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o) != null ? dataMap.get(currentMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o).getTotalMoney() : BigDecimal.valueOf(0);
                        // 上个月
                        LocalDateTime lastMonth = currentMonth.minusMonths(i);
                        // 上个月数据
                        Map<String, BizOrderStatisticsDto> lastDataMap = dataMap.get(lastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT));
                        if (lastDataMap == null) {
                            lastDataMap = bizStatisticsService.orderStatisticData(lastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT));
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
     * 采购中心订单表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTable")
    public String orderTable (HttpServletRequest request){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsOrderTable";
    }
    /**
     * 采购中心订单统计表格数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "centOrderTable")
    public Map<String, BizOrderStatisticsDto> centOrderTable(HttpServletRequest request, String month, Model model) throws ParseException {
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        Date nowDate = sdf.parse(month);
        c.setTime(nowDate);
        c.add(Calendar.MONTH,-1);
        Date m = c.getTime();
        String mon = sdf.format(m);
        //当前月
        Map<String, BizOrderStatisticsDto> resultMap = bizStatisticsService.orderStatisticData(month);
        //上个月
        Map<String, BizOrderStatisticsDto> UpResultMap = bizStatisticsService.orderStatisticData(mon);
        //合并
        HashMap<String, BizOrderStatisticsDto> map = new HashMap<>();
        for (Map.Entry<String,BizOrderStatisticsDto> entry:UpResultMap.entrySet()) {
            String key = entry.getKey();
            BizOrderStatisticsDto value = entry.getValue();
            BizOrderStatisticsDto ordStaDto = new BizOrderStatisticsDto();
            ordStaDto.setUpOrderCount(value.getOrderCount());
            ordStaDto.setUpTotalMoney(value.getTotalMoney()==null?new BigDecimal(0):value.getTotalMoney());
            ordStaDto.setUpProfitPrice(value.getProfitPrice()==null?new BigDecimal(0):value.getProfitPrice());
            map.put(key,ordStaDto);
        }
        for (Map.Entry<String,BizOrderStatisticsDto> entry:resultMap.entrySet()){
            String key = entry.getKey();
            BizOrderStatisticsDto value = entry.getValue();
            if (map.get(key) != null) {
                map.get(key).setOrderCount(value.getOrderCount());
                map.get(key).setTotalMoney(value.getTotalMoney()==null?new BigDecimal(0):value.getTotalMoney());
                map.get(key).setProfitPrice(value.getProfitPrice()==null?new BigDecimal(0):value.getProfitPrice());
            }else {
                BizOrderStatisticsDto ordStaDto = new BizOrderStatisticsDto();
                ordStaDto.setOrderCount(value.getOrderCount());
                ordStaDto.setTotalMoney(value.getTotalMoney()==null?new BigDecimal(0):value.getTotalMoney());
                ordStaDto.setProfitPrice(value.getProfitPrice()==null?new BigDecimal(0):value.getProfitPrice());
                map.put(key,ordStaDto);
            }
        }

        model.addAttribute("map",map);
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return map;
    }

    /**
     * 产品统计（个人 表格数据）
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "productAnalysisTables")
    public String productAnalysisTables (HttpServletRequest request, Integer variId, Integer purchasingId){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("varietyList", bizStatisticsService.getBizVarietyInfoList());
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        List<BizProductStatisticsDto> productStatisticsList = bizStatisticsService.productStatisticData(LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT), 1, null);
        request.setAttribute("productStatisticsList", productStatisticsList);
        return "modules/biz/statistics/bizStatisticsProductTables";
    }
    /**
     * 采购顾问表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "userTable")
    public String userTable (HttpServletRequest request){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsUserTable";
    }

    /**
     * 采购顾问统计表格数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "centUserTable")
    public List<BizUserSaleStatisticsDto> centUserTable(HttpServletRequest request, String month,Integer purchasingId, Model model){
        List<BizUserSaleStatisticsDto> bizUserSaleStatisticsDtos = bizStatisticsService.userTableStatisticData(month, purchasingId);
        return bizUserSaleStatisticsDtos;
    }


    /**
     * 顾问个人订单统计（个人 表格数据）
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTables")
    public String orderTables (HttpServletRequest request,String month){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        List<BizUserSaleStatisticsDto> productStatisticsTableList = bizStatisticsService.userSaleStatisticData(LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT), null);
        request.setAttribute("productStatisticsTableList", productStatisticsTableList);
        String s = userSaleData(null, month, null, null);
        System.out.println(s);
        return "modules/biz/statistics/statisticsUserTables";
    }
    /**
     * 统计总的会员数，采购中心数，网供数，配资中心数，订单数量，总额、已收货款，商品数量 平均客单价
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "getBizTotalStatisticsDto")
    public String getBizTotalStatisticsDto(Model model){
        BizTotalStatisticsDto bizTotalStatisticsDto = bizStatisticsService.getBizTotalStatisticsDto();
        model.addAttribute("totalStatistics",bizTotalStatisticsDto);
        return "modules/biz/statistics/bizTotalStatistics";
    }

}

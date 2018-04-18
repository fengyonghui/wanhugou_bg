package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
     * 用户业绩相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"userSaleDataTable", ""})
    public String userSaleDataTable(HttpServletRequest request, String month, Integer purchasingId, String usName) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", month);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        return "modules/biz/statistics/bizStatisticsUserSaleTable";
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
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"customTable", ""})
    public String customTable(HttpServletRequest request, String month) {
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userStatisticData(month);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", month);
        return "modules/biz/statistics/bizStatisticsCustomTable";
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
     * 产品相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:product:view")
    @RequestMapping(value = {"productDataDownload", ""})
    @ResponseBody
    public void productDataDownload(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String imgUrl,
                                    String month,
                                    Integer variId,
                                    Integer purchasingId) throws IOException {
        List<BizProductStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.productStatisticData(month, variId, purchasingId);

        String fileName = "产品统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell = header.createCell(0);
        hCell.setCellValue("产品名称");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue("销售额");
        HSSFCell hCell2 = header.createCell(2);
        hCell2.setCellValue("销量");


        for (BizProductStatisticsDto o : bizProductStatisticsDtos) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex ++;
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(o.getName().concat("-").concat(o.getItemNo()));
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(o.getTotalMoney().toString());
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(o.getCount());
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String u = url[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            //生成图片
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 1, 16, (short) 20, 45);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
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
    public String orderData(HttpServletRequest request, String month, String lineChartType, String barChartType, String centerType) {
        return JSONObject.fromObject(getOrderData(month, lineChartType, barChartType, centerType)).toString();
    }

    /**
     * 订单相关统计数据
     *
     * @param month         月份
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderData(String month, String lineChartType, String barChartType, String centerType) {
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
            dataMap.put(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), bizStatisticsService.orderStatisticData(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), centerType));
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
                            lastDataMap = bizStatisticsService.orderStatisticData(lastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT), centerType);
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
            case UNIVALENCE:
                // 平均单价
                officeNameSet.forEach(o -> {
                    EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
                    echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

                    List<Object> dataList = Lists.newArrayList();
                    for (int i = dataMap.size() - 1; i >= 0; i--) {
                        dataList.add(
                                dataMap.get(
                                        monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o) != null
                                        ? dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o).getUnivalence() : 0);
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
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "centOrderDataDownload")
    public void centOrderDataDownload(HttpServletRequest request, String month) throws ParseException {




    }    /**
     * 采购中心订单统计表格数据
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "centOrderTable")
    public String centOrderTable(HttpServletRequest request, String month, String centerType) throws ParseException {
        Calendar c=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
        Date nowDate = sdf.parse(month);
        c.setTime(nowDate);
        c.add(Calendar.MONTH,-1);
        Date m = c.getTime();
        String mon = sdf.format(m);
        //当前月
        Map<String, BizOrderStatisticsDto> resultMap = bizStatisticsService.orderStatisticData(month, centerType);
        //上个月
        Map<String, BizOrderStatisticsDto> UpResultMap = bizStatisticsService.orderStatisticData(mon, centerType);
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
                map.get(key).setBuyPrice(value.getBuyPrice()==null?new BigDecimal(0):value.getBuyPrice());
            }else {
                BizOrderStatisticsDto ordStaDto = new BizOrderStatisticsDto();
                ordStaDto.setOrderCount(value.getOrderCount());
                ordStaDto.setTotalMoney(value.getTotalMoney()==null?new BigDecimal(0):value.getTotalMoney());
                ordStaDto.setProfitPrice(value.getProfitPrice()==null?new BigDecimal(0):value.getProfitPrice());
                ordStaDto.setBuyPrice(value.getBuyPrice()==null?new BigDecimal(0):value.getBuyPrice());
                map.put(key,ordStaDto);
            }
        }
        return JSONObject.fromObject(map).toString();
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
        Map<String,BizTotalStatisticsDto> totalMap = bizStatisticsService.getBizTotalStatisticsDto();
        model.addAttribute("totalMap",totalMap);
        model.addAttribute("time",new Date());
        return "modules/biz/statistics/bizTotalStatistics";
    }
    /**
     * 统计总的会员数，采购中心数，网供数，配资中心数，订单数量，总额、已收货款，商品数量 平均客单价
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "bizTotalStatisticsDtoDownload")
    public void bizTotalStatisticsDtoDownload(Model model, HttpServletResponse response) throws IOException {
        Map<String,BizTotalStatisticsDto> totalMap = bizStatisticsService.getBizTotalStatisticsDto();
        String fileName = "万户通平台总体情况.xls";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);
        HSSFRow header = sheet.createRow(0);
        HSSFRow vRow = sheet.createRow(1);

        int cIndex = 0;
        for (String key : totalMap.keySet()) {
            HSSFCell hCell = header.createCell(cIndex);
            HSSFCell vCell = vRow.createCell(cIndex);
            hCell.setCellValue(key);
            vCell.setCellValue(totalMap.get(key).getCount() + totalMap.get(key).getUnit());
            cIndex++;
        }



        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }

    /**
     * 用户业绩相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"userSaleDataDownload", ""})
    public void userSaleDataDownload(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String imgUrl,
                                     String imgUrl1,
                                     String month,
                                     Integer purchasingId) throws IOException {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);

        String fileName = "业绩统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);
        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell cell0 = header.createCell(0);
        cell0.setCellValue("姓名");
        HSSFCell cell1 = header.createCell(1);
        cell1.setCellValue("销售额");
        HSSFCell cell2 = header.createCell(2);
        cell2.setCellValue("订单量");


        for (BizUserSaleStatisticsDto o : bizProductStatisticsDtos) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex ++;
            HSSFCell sCell0 = row.createCell(0);
            sCell0.setCellValue(o.getName());
            HSSFCell sCell1 = row.createCell(1);
            sCell1.setCellValue(o.getTotalMoney().toString());
            HSSFCell sCell2 = row.createCell(2);
            sCell2.setCellValue(o.getOrderCount());
        }

        // 将图片写入流中
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String u = url[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            //生成图片
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5, (short) 4, 5, (short) 20, 30);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }


    /**
     * 采购中心订单表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTableDataDownload")
    public void orderTableDataDownload(HttpServletRequest request,
                                       HttpServletResponse response,
                                       String month,
                                       String dataType,
                                       String imgUrl,
                                       String imgUrl1,
                                       String centerType
    ) throws IOException {
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
            dataMap.put(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), bizStatisticsService.orderStatisticData(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), centerType));
            monthList.add(o.toString(BizStatisticsService.PARAM_DATE_FORMAT));
        });
        Collections.reverse(monthList);

        Set<String> officeNameSet = Sets.newHashSet();
        monthDateList.forEach(o -> {
            officeNameSet.addAll(dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)) != null ? dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)).keySet() : CollectionUtils.EMPTY_COLLECTION);
        });
        officeNameSet.removeAll(Collections.singleton(null));

        String fileName = "订单统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        int hCellIndex = 0;
        HSSFCell hCell = header.createCell(hCellIndex);
        hCell.setCellValue("采购中心");
        hCellIndex++;

        for (String o : monthList) {
            HSSFCell cell = header.createCell(hCellIndex);
            cell.setCellValue(o);
            hCellIndex++;
        }

        for (String officeName : officeNameSet) {
            HSSFRow row = sheet.createRow(rowIndex);
            int cellIndex = 0;
            HSSFCell lCell = row.createCell(cellIndex);
            cellIndex++;
            lCell.setCellValue(officeName);
            for (String o : monthList) {
                Map<String, BizOrderStatisticsDto> stringBizOrderStatisticsDtoMap = dataMap.get(o);
                HSSFCell cell = row.createCell(cellIndex);
                if (stringBizOrderStatisticsDtoMap != null && stringBizOrderStatisticsDtoMap.get(officeName) != null) {
                    BizOrderStatisticsDto bizOrderStatisticsDto = stringBizOrderStatisticsDtoMap.get(officeName);
                    switch (OrderStatisticsDataTypeEnum.parse(Integer.valueOf(dataType))) {
                        case ORDER_COUNT:
                            cell.setCellValue(bizOrderStatisticsDto.getOrderCount());
                            break;
                        case SALEROOM:
                            cell.setCellValue(bizOrderStatisticsDto.getTotalMoney().toString());
                            break;
                        default:
                            break;
                    }
                } else {
                    cell.setCellValue("0");
                }
                cellIndex++;
            }
            rowIndex++;
        }

        // 将图片写入流中
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String[] url1 = imgUrl1.split(",");
            String u = url[1];
            String u1 = url1[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            byte[] buffer1 = decoder.decodeBuffer(u1);
            //生成图片
            outStream.write(buffer);
            outStream1.write(buffer1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 5, 5, (short) 20, 45);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        HSSFClientAnchor anchor1 = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 5, 35, (short) 20, 75);
        patri.createPicture(anchor1, wb.addPicture(
                outStream1.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));


        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }


    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"userDataDownload", ""})
    @ResponseBody
    public void userDataDownload(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String month,
                                 String imgUrl
    ) throws IOException {
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userStatisticData(month);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        String fileName = "新增用户统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell0 = header.createCell(0);
        hCell0.setCellValue("采购中心");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue("新增用户");

        for (BizUserStatisticsDto b : bizProductStatisticsDtos) {
            HSSFRow row = sheet.createRow(rowIndex);
            HSSFCell cell0 = row.createCell(0);
            cell0.setCellValue(b.getName());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(b.getCount());
            rowIndex ++;
        }

        try {
            String[] url = imgUrl.split(",");
            String u = url[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            //生成图片
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }



        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 4, 5, (short) 20, 35);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }


    /**
     * 利润统计
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profit", ""})
    public String profit(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsProfit";
    }

    /**
     * 利润统计数据
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profitData", ""})
    @ResponseBody
    public String profitData(HttpServletRequest request, HttpServletResponse response, String month, String centerType) {
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
            dataMap.put(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), bizStatisticsService.orderStatisticData(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), centerType));
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
            seriesList.add(
                    bizStatisticsService.genEchartsSeriesDto(
                            officeNameSet,
                            dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)),
                            monthDateList.get(i),
                            String.valueOf(OrderStatisticsDataTypeEnum.PROFIT.getCode())));
        }

        seriesList.removeAll(Collections.singleton(null));

        paramMap.put("seriesList", seriesList);

        ArrayList<EchartsSeriesDto> lineSeriesList = Lists.newArrayList();

        officeNameSet.forEach(o -> {
            EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
            echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

            List<Object> dataList = Lists.newArrayList();
            for (int i = dataMap.size() - 1; i >= 0; i--) {
                dataList.add(dataMap.get(
                        monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o) != null ?
                        dataMap.get(monthDateList.get(i).toString(BizStatisticsService.PARAM_DATE_FORMAT)).get(o).getProfitPrice() : 0);
            }

            echartsSeriesDto.setData(dataList);
            echartsSeriesDto.setName(o);

            lineSeriesList.add(echartsSeriesDto);
        });

        paramMap.put("rateSeriesList", lineSeriesList);
        paramMap.put("dataMap", dataMap);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesList));

        return JSONObject.fromObject(paramMap).toString();
    }

    /**
     * 利润统计数据下载
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profitDataDownload", ""})
    public void profitDataDownload(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String month,
                                   String imgUrl,
                                   String imgUrl1,
                                   String centerType
    ) throws IOException {
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
            dataMap.put(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), bizStatisticsService.orderStatisticData(o.toString(BizStatisticsService.PARAM_DATE_FORMAT), centerType));
            monthList.add(o.toString(BizStatisticsService.PARAM_DATE_FORMAT));
        });
        Collections.reverse(monthList);

        Set<String> officeNameSet = Sets.newHashSet();
        monthDateList.forEach(o -> {
            officeNameSet.addAll(dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)) != null ? dataMap.get(o.toString(BizStatisticsService.PARAM_DATE_FORMAT)).keySet() : CollectionUtils.EMPTY_COLLECTION);
        });
        officeNameSet.removeAll(Collections.singleton(null));

        String fileName = "利润统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        int hCellIndex = 0;
        HSSFCell hCell = header.createCell(hCellIndex);
        hCell.setCellValue("采购中心");
        hCellIndex++;

        for (String o : monthList) {
            HSSFCell cell = header.createCell(hCellIndex);
            cell.setCellValue(o);
            hCellIndex++;
        }

        for (String officeName : officeNameSet) {
            HSSFRow row = sheet.createRow(rowIndex);
            int cellIndex = 0;
            HSSFCell lCell = row.createCell(cellIndex);
            cellIndex++;
            lCell.setCellValue(officeName);
            for (String o : monthList) {
                Map<String, BizOrderStatisticsDto> stringBizOrderStatisticsDtoMap = dataMap.get(o);
                HSSFCell cell = row.createCell(cellIndex);
                if (stringBizOrderStatisticsDtoMap != null && stringBizOrderStatisticsDtoMap.get(officeName) != null) {
                    BizOrderStatisticsDto bizOrderStatisticsDto = stringBizOrderStatisticsDtoMap.get(officeName);
                    cell.setCellValue(bizOrderStatisticsDto.getProfitPrice().toString());
                } else {
                    cell.setCellValue("0");
                }
                cellIndex++;
            }
            rowIndex++;
        }

        // 将图片写入流中
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String[] url1 = imgUrl1.split(",");
            String u = url[1];
            String u1 = url1[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            byte[] buffer1 = decoder.decodeBuffer(u1);
            //生成图片
            outStream.write(buffer);
            outStream1.write(buffer1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 5, 5, (short) 20, 20);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        HSSFClientAnchor anchor1 = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 5, 20, (short) 20, 40);
        patri.createPicture(anchor1, wb.addPicture(
                outStream1.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));


        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }

    /**
     * 个人利润统计
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"singleUserProfit", ""})
    public String singleUserProfit(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("month", LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        return "modules/biz/statistics/bizStatisticsSingleUserProfit";
    }

    /**
     * 个人利润统计
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"singleUserProfitData", ""})
    @ResponseBody
    public String singleUserProfitData(HttpServletRequest request, HttpServletResponse response, String month, String usName, Integer purchasingId) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);
        List<String> nameList = Lists.newArrayList();
        Map<String, Integer> usNameIdMap = Maps.newHashMap();

        List<Object> seriesDataList = Lists.newArrayList();
        List<String> userMonthList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        bizProductStatisticsDtos.forEach(o -> {
            seriesDataList.add(o.getProfitPrice());
            nameList.add(o.getName());
            usNameIdMap.put(o.getName(), o.getUsId());
        });
        echartsSeriesDto.setName("利润");
        echartsSeriesDto.setData(seriesDataList);


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
            EchartsSeriesDto singleEchartsSeriesDto = new EchartsSeriesDto();
            List<String> monthList = Lists.newArrayList();

            bizUserSaleStatisticsDtos.forEach(o -> {
                singleSeriesDataList.add(o.getProfitPrice());
                monthList.add(o.getCreateTime());
                userMonthList.add(o.getCreateTime().concat("+").concat(o.getProfitPrice()+"").concat("+").concat(o.getProfitPrice()+"")
                        .concat("+").concat(o.getAddCustCount()+"").concat("+").concat(o.getCustCount()+""));
            });

            singleEchartsSeriesDto.setName("利润");
            singleEchartsSeriesDto.setData(singleSeriesDataList);

            paramMap.put("singleSeriesList", Lists.newArrayList(singleEchartsSeriesDto));
            paramMap.put("monthList", monthList);

        }

        paramMap.put("seriesList", Lists.newArrayList(echartsSeriesDto));
        paramMap.put("usName", usName);
        List<String> selectNameList = Lists.newArrayList(nameList);
        if (StringUtils.isNotBlank(usName)) {
            selectNameList.remove(usName);
        }
        paramMap.put("selectNameList", selectNameList);
        paramMap.put("usName", usName);
        paramMap.put("userMonthList", userMonthList);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return JSONObject.fromObject(paramMap).toString();
    }


    /**
     * 用户销售利润相关统计数据下载
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"singleUserProfitDataDownload", ""})
    public void singleUserProfitDataDownload(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String imgUrl,
                                     String imgUrl1,
                                     String month,
                                     Integer purchasingId) throws IOException {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);

        String fileName = "利润统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);
        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell cell0 = header.createCell(0);
        cell0.setCellValue("姓名");
        HSSFCell cell1 = header.createCell(1);
        cell1.setCellValue("利润");


        for (BizUserSaleStatisticsDto o : bizProductStatisticsDtos) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex ++;
            HSSFCell sCell0 = row.createCell(0);
            sCell0.setCellValue(o.getName());
            HSSFCell sCell1 = row.createCell(1);
            sCell1.setCellValue(o.getProfitPrice().toString());
        }

        // 将图片写入流中
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String[] url1 = imgUrl1.split(",");
            String u = url[1];
            String u1 = url1[1];
            //Base64解码
            byte[] buffer = decoder.decodeBuffer(u);
            byte[] buffer1 = decoder.decodeBuffer(u1);
            //生成图片
            outStream.write(buffer);
            outStream1.write(buffer1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5, (short) 4, 5, (short) 20, 30);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

//        HSSFClientAnchor anchor1 = new HSSFClientAnchor(5, 5, 5, 5, (short) 20, 21, (short) 25, 35);
//        patri.createPicture(anchor1, wb.addPicture(
//                outStream1.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));

        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }

    /**
     * 个人销售利润表格
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"singleUserProfitDataTable", ""})
    public String singleUserProfitDataTable(String month, HttpServletResponse response, HttpServletRequest request, Integer purchasingId) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsService.userSaleStatisticData(month, purchasingId);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("month", month);
        return "modules/biz/statistics/bizStatisticsSingleUserProfitTable";
    }
}

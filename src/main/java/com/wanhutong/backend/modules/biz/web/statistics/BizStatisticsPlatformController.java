package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsDayService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsPlatformService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 统计相关Controller
 *
 * @author Ma.Qiang
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/statistics/platform")
public class BizStatisticsPlatformController extends BaseController {

    @Resource
    private BizStatisticsPlatformService bizStatisticsPlatformService;
    @Resource
    private BizStatisticsService bizStatisticsService;

    /**
     * 默认除法运算精度
     */
    private static final int DEF_DIV_SCALE = 2;

    /**
     * 最大数据长度
     */
    private static final int MAX_DATA_LENGTH = 10;

    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"overview", ""})
    public String overview(HttpServletRequest request, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        Map<String, List<BizPlatformDataOverviewDto>> list = null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date parseDate = StringUtils.isBlank(date) ? new Date() : simpleDateFormat.parse(date);
            calendar.setTime(parseDate);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = calendar.getTime();
            list = bizStatisticsPlatformService.getPlatformData(
                    simpleDateFormat.format(startDate),
                    simpleDateFormat.format(parseDate),
                    simpleDateFormat.format(parseDate)
            );

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (StringUtils.isBlank(date)) {
            date = simpleDateFormat.format(new Date());
        }
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("date", date);
        request.setAttribute("dataList", list);
        return "modules/biz/statistics/bizPlatformDataOverview";
    }

    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"overviewDownload", ""})
    public void overviewDownload(HttpServletRequest request, HttpServletResponse response, String date) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        Map<String, List<BizPlatformDataOverviewDto>> dataMap = null;
        try {
            Calendar calendar = Calendar.getInstance();
            Date parseDate = StringUtils.isBlank(date) ? new Date() : simpleDateFormat.parse(date);
            calendar.setTime(parseDate);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = calendar.getTime();
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = calendar.getTime();
            dataMap = bizStatisticsPlatformService.getPlatformData(
                    simpleDateFormat.format(startDate),
                    simpleDateFormat.format(parseDate),
                    simpleDateFormat.format(parseDate)
            );

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (StringUtils.isBlank(date)) {
            date = simpleDateFormat.format(new Date());
        }
        request.setAttribute("date", date);
        request.setAttribute("dataList", dataMap);


        String fileName = "平台数据概览.xls";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        HSSFRow header0 = sheet.createRow(0);
        HSSFRow header1 = sheet.createRow(1);
        HSSFRow header2 = sheet.createRow(2);
        HSSFCell hCell0 = header0.createCell(0);
        hCell0.setCellValue("");
        HSSFCell hCell1 = header0.createCell(1);
        hCell1.setCellValue("万户通平台业务数据");

        HSSFCell h1Cell0 = header1.createCell(0);
        h1Cell0.setCellValue("省份");
        HSSFCell h1Cell1 = header1.createCell(1);
        h1Cell1.setCellValue("所属采购中心");
        HSSFCell h1Cell2 = header1.createCell(2);
        h1Cell2.setCellValue("目标分析");

        HSSFCell h2Cell2 = header2.createCell(2);
        h2Cell2.setCellValue("月计划采购额(元)");
        HSSFCell h2Cell3 = header2.createCell(3);
        h2Cell3.setCellValue("月累计销量");
        HSSFCell h2Cell4 = header2.createCell(4);
        h2Cell4.setCellValue("日销售额(元)");
        HSSFCell h2Cell5 = header2.createCell(5);
        h2Cell5.setCellValue("达成率");
        HSSFCell h2Cell6 = header2.createCell(6);
        h2Cell6.setCellValue("月累计差异");
        HSSFCell h2Cell7 = header2.createCell(7);
        h2Cell7.setCellValue("剩余天数");
        HSSFCell h2Cell8 = header2.createCell(8);
        h2Cell8.setCellValue("每日最低回款额");
        HSSFCell h2Cell9 = header2.createCell(9);
        h2Cell9.setCellValue("库存金额");


        // 合并单元格起始行, 终止行, 起始列, 终止列
        CellRangeAddress h0 =new CellRangeAddress(0, 0, 1, 9);
        sheet.addMergedRegion(h0);
         CellRangeAddress h1a =new CellRangeAddress(1, 2, 0, 0);
        sheet.addMergedRegion(h1a);
         CellRangeAddress h1b =new CellRangeAddress(1, 2, 1, 1);
        sheet.addMergedRegion(h1b);
         CellRangeAddress h1c =new CellRangeAddress(1, 1, 2, 9);
        sheet.addMergedRegion(h1c);
        if (dataMap == null || dataMap.size() <= 0) {
            return;
        }
        int rowIndex = 3;
        int rowMergeIndex = rowIndex;
        for (String key : dataMap.keySet()){
            List<BizPlatformDataOverviewDto> bizPlatformDataOverviewDtos = dataMap.get(key);
            for (BizPlatformDataOverviewDto o : bizPlatformDataOverviewDtos){
                HSSFRow sRow = sheet.createRow(rowIndex);
                HSSFCell cell0 = sRow.createCell(0);
                cell0.setCellValue(key);
                HSSFCell cell1 = sRow.createCell(1);
                cell1.setCellValue(o.getName());
                HSSFCell cell2 = sRow.createCell(2);
                cell2.setCellValue(o.getProcurement().toString());
                HSSFCell cell3 = sRow.createCell(3);
                cell3.setCellValue(o.getAccumulatedSalesMonth().toString());
                HSSFCell cell4 = sRow.createCell(4);
                cell4.setCellValue(o.getProcurementDay().toString());
                HSSFCell cell5 = sRow.createCell(5);
                cell5.setCellValue(o.getYieldRate());
                HSSFCell cell6 = sRow.createCell(6);
                cell6.setCellValue(o.getDifferenceTotalMonth().toString());
                HSSFCell cell7 = sRow.createCell(7);
                cell7.setCellValue(o.getRemainingDays());
                HSSFCell cell8 = sRow.createCell(8);
                cell8.setCellValue(o.getDayMinReturned().toString());
                HSSFCell cell9 = sRow.createCell(9);
                cell9.setCellValue(o.getStockAmount().toString());
                rowIndex ++;
            }

            CellRangeAddress cra =new CellRangeAddress(rowMergeIndex, rowIndex - 1, 0, 0);
            sheet.addMergedRegion(cra);
            rowMergeIndex = rowIndex;
        }

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
    @RequestMapping(value = {"user", ""})
    public String user(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
//        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        request.setAttribute("startDate", "2017-09-01"); // TODO 临时代码
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsUserPlatform";
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
    public String userData(HttpServletRequest request, String startDate, String endDate, String type, String centerType) {
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsPlatformService.userStatisticData(type, startDate, endDate, centerType);
        List<String> nameList = Lists.newArrayList();

        Date startDateDate = null;
        Date endDateDate = null;

        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();

        try {
            List<String> dayList = Lists.newArrayList();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
            startDateDate = simpleDateFormatDay.parse(startDate);
            endDateDate = simpleDateFormatDay.parse(endDate);
            Calendar c = Calendar.getInstance();
            c.setTime(startDateDate);
            while (c.getTime().getTime() <= endDateDate.getTime()) {
                if ("1".equals(type)) {
                    dayList.add(simpleDateFormatDay.format(c.getTime()));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    dayList.add(simpleDateFormat.format(c.getTime()));
                    c.add(Calendar.MONTH, 1);
                }
            }

            dayList.forEach(o -> {
                Object value = 0;
                for (BizUserStatisticsDto b : bizProductStatisticsDtos) {
                    if (b.getCreateDate().equals(o)) {
                        value = b.getCount();
                    }
                }
                seriesDataList.add(value);
                nameList.add(o);
            });

        } catch (ParseException e) {
            e.printStackTrace();
        }
        echartsSeriesDto.setName("用户量");
        echartsSeriesDto.setData(seriesDataList);
        echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

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
        return "modules/biz/statistics/bizStatisticsOrderPlatform";
    }

    /**
     * 订单相关统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderCategoryByCenter", ""})
    public String orderCategoryByCenter(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
//        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        request.setAttribute("startDate", "2017-09-01"); // TODO 临时代码
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsOrderPlatformCategoryByCenter";
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate 月份
     * @return 订单相关统计数据
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderData", ""})
    @ResponseBody
    public String orderData(HttpServletRequest request, String startDate, String endDate, String type, String centerType, String orderType) {
        return JSONObject.fromObject(getOrderData(startDate, endDate, type, centerType, orderType)).toString();
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate 月份
     * @return 订单相关统计数据
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderDataCategoryByCenter", ""})
    @ResponseBody
    public String orderDataCategoryByCenter(HttpServletRequest request, String startDate, String endDate, String type, String centerType, String orderType) {
        return JSONObject.fromObject(getOrderDataCategoryByCenter(startDate, endDate, type, centerType, orderType)).toString();
    }


    /**
     * 订单相关统计数据
     *
     * @param startDate 月份
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderDataCategoryByCenter(String startDate, String endDate, String type, String centerType, String orderType) {
        List<Office> bizPurchasingList = bizStatisticsService.getOfficeList("8");
        List<String> nameList = Lists.newArrayList();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isBlank(startDate)) {
            startDate = simpleDateFormat.format(new Date());
        }
        if (StringUtils.isNotBlank(startDate)) {
            startDate = startDate + "-01";
        }
        if (StringUtils.isNotBlank(endDate)) {
            endDate = endDate + "-01";
        }

        if (StringUtils.isBlank(endDate)) {
            endDate = simpleDateFormatDay.format(new Date());
        }


        Date startDateDate = null;
        Date endDateDate = null;
        List<String> dateStrList = Lists.newArrayList();

        try {
            startDateDate = simpleDateFormat.parse(startDate);
            endDateDate = simpleDateFormat.parse(endDate);
            Calendar c = Calendar.getInstance();
            c.setTime(startDateDate);
            if ("0".equals(type) && startDateDate.getTime() == endDateDate.getTime()) {
                c.add(Calendar.YEAR, -1);
            }
            while (c.getTime().getTime() <= endDateDate.getTime()) {
                if ("1".equals(type)) {
                    dateStrList.add(simpleDateFormatDay.format(c.getTime()));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                } else {
                    dateStrList.add(simpleDateFormat.format(c.getTime()));
                    c.add(Calendar.MONTH, 1);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String startDateFinal = startDate;
        String endDateFinal = endDate;
        List<EchartsSeriesDto> seriesList = Lists.newArrayList();
        List<EchartsSeriesDto> seriesCountList = Lists.newArrayList();
        bizPurchasingList.forEach(o -> {
            List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = bizStatisticsPlatformService.orderStatisticDataByOffice(startDateFinal, endDateFinal, type, centerType, orderType, o.getId());

            EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
            EchartsSeriesDto echartsSeriesCountDto = new EchartsSeriesDto();
            echartsSeriesDto.setName(o.getName());
            echartsSeriesCountDto.setName(o.getName());
            List<Object> seriesDataList = Lists.newArrayList();
            List<Object> seriesDataCountList = Lists.newArrayList();
            dateStrList.forEach(j -> {
                bizOrderStatisticsDtoList.forEach(b -> {
                    if (b.getCreateDate().equals(j)) {
                        seriesDataList.add(b.getTotalMoney());
                        seriesDataCountList.add(b.getOrderCount());
                    }
                });
            });
            echartsSeriesDto.setData(seriesDataList);
            echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

            echartsSeriesCountDto.setData(seriesDataCountList);
            echartsSeriesCountDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());
            seriesList.add(echartsSeriesDto);
            seriesCountList.add(echartsSeriesCountDto);
        });

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", seriesList);
        paramMap.put("seriesCountList", seriesCountList);
        paramMap.put("dateStrList", dateStrList);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesList));
        return paramMap;
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate 月份
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderData(String startDate, String endDate, String type, String centerType, String orderType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        String startDateMonth = startDate;
        if (StringUtils.isNotBlank(startDate)) {
            startDate = startDate + "-01";
        }
        if (StringUtils.isNotBlank(endDate)) {
            endDate = endDate + "-01";
        }

        if (StringUtils.isBlank(endDate)) {
            endDate = simpleDateFormatDay.format(new Date());
        }

        List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = bizStatisticsPlatformService.orderStatisticData(startDate, endDate, type, centerType, orderType);

        List<String> nameList = Lists.newArrayList();

        List<Object> seriesDataList = Lists.newArrayList();
        List<Object> seriesCountDataList = Lists.newArrayList();
        List<Object> seriesReceiveDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        EchartsSeriesDto echartsCountSeriesDto = new EchartsSeriesDto();
        EchartsSeriesDto echartsReceiveSeriesDto = new EchartsSeriesDto();
        if ("1".equals(type)) {
            try {
                List<String> dayList = Lists.newArrayList();

                Date parseDate = simpleDateFormat.parse(startDateMonth);
                Calendar c = Calendar.getInstance();
                c.setTime(parseDate);
                while (simpleDateFormat.format(c.getTime()).equals(startDateMonth)) {
                    dayList.add(simpleDateFormatDay.format(c.getTime()));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

                dayList.forEach(o -> {
                    Object value = 0;
                    Object count = 0;
                    Object receiveTotal = 0;
                    for (BizOrderStatisticsDto b : bizOrderStatisticsDtoList) {
                        if (b.getCreateDate().equals(o)) {
                            value = b.getTotalMoney();
                            count = b.getOrderCount();
                            receiveTotal = b.getReceiveTotal();
                        }
                    }
                    seriesDataList.add(value);
                    seriesCountDataList.add(count);
                    seriesReceiveDataList.add(receiveTotal);
                    nameList.add(o);
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            bizOrderStatisticsDtoList.forEach(o -> {
                seriesDataList.add(o.getTotalMoney());
                seriesCountDataList.add(o.getOrderCount());
                seriesReceiveDataList.add(o.getOrderCount());
                nameList.add(o.getCreateDate());
            });
        }
        echartsSeriesDto.setName("销售额");
        echartsSeriesDto.setData(seriesDataList);
        echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

        echartsCountSeriesDto.setName("订单量");
        echartsCountSeriesDto.setData(seriesCountDataList);
        echartsCountSeriesDto.setyAxisIndex(1);
        echartsCountSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

        echartsReceiveSeriesDto.setName("已收货款");
        echartsReceiveSeriesDto.setData(seriesReceiveDataList);
        echartsReceiveSeriesDto.setyAxisIndex(1);
        echartsReceiveSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", Lists.newArrayList(echartsSeriesDto, echartsCountSeriesDto));
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return paramMap;
    }

    /**
     * 采购中心订单表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTable")
    public String orderTable (HttpServletRequest request, String startDate, String endDate){
        request.setAttribute("adminPath", adminPath);  Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", StringUtils.isBlank(startDate) ? simpleDateFormat.format(cal.getTime()) : startDate);
        return "modules/biz/statistics/bizStatisticsOrderPlatformTable";
    }

    /**
     * 采购中心订单表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTableData")
    @ResponseBody
    public String orderTableData (HttpServletRequest request, String startDate, String endDate, String type, String centerType, String orderType){
        // 月份集合
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        String startDateMonth = startDate;
        if (StringUtils.isNotBlank(startDate)) {
            startDate = startDate + "-01";
        }
        if (StringUtils.isNotBlank(endDate)) {
            endDate = endDate + "-01";
        }

        if (StringUtils.isBlank(endDate)) {
            endDate = simpleDateFormatDay.format(new Date());
        }

        List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = bizStatisticsPlatformService.orderStatisticData(startDate, endDate, type, centerType, orderType);
        Map<String, Object> paramMap = Maps.newHashMap();

        paramMap.put("bizOrderStatisticsDtoList", bizOrderStatisticsDtoList);
        return JSONObject.fromObject(paramMap).toString();
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
        request.setAttribute("month", org.joda.time.LocalDateTime.now().toString(BizStatisticsService.PARAM_DATE_FORMAT));
        return "modules/biz/statistics/bizStatisticsProfitPlatform";
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
    public String profitData(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String centerType, String type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        String startDateMonth = startDate;
        if (StringUtils.isNotBlank(startDate)) {
            startDate = startDate + "-01";
        }
        if (StringUtils.isNotBlank(endDate)) {
            endDate = endDate + "-01";
        }

        if (StringUtils.isBlank(endDate)) {
            endDate = simpleDateFormatDay.format(new Date());
        }

        List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = bizStatisticsPlatformService.orderStatisticData(startDate, endDate, type, centerType, StringUtils.EMPTY);

        List<String> nameList = Lists.newArrayList();

        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        if ("1".equals(type)) {
            try {
                List<String> dayList = Lists.newArrayList();

                Date parseDate = simpleDateFormat.parse(startDateMonth);
                Calendar c = Calendar.getInstance();
                c.setTime(parseDate);
                while (simpleDateFormat.format(c.getTime()).equals(startDateMonth)) {
                    dayList.add(simpleDateFormatDay.format(c.getTime()));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

                dayList.forEach(o -> {
                    Object value = 0;
                    for (BizOrderStatisticsDto b : bizOrderStatisticsDtoList) {
                        if (b.getCreateDate().equals(o)) {
                            value = b.getProfitPrice();
                            System.out.println(b.getBuyPrice());
                        }
                    }
                    seriesDataList.add(value);
                    nameList.add(o);
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            bizOrderStatisticsDtoList.forEach(o -> {
                seriesDataList.add(o.getProfitPrice());
                nameList.add(o.getCreateDate());
            });
        }
        echartsSeriesDto.setName("利润");
        echartsSeriesDto.setData(seriesDataList);
        echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

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

        EchartsSeriesDto barSeriesList = null;
        try {
            barSeriesList = (EchartsSeriesDto)BeanUtils.cloneBean(echartsSeriesDto);
            barSeriesList.setType(EchartsSeriesDto.SeriesTypeEnum.BAR.getCode());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("barSeriesList", barSeriesList);
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return JSONObject.fromObject(paramMap).toString();
    }
}

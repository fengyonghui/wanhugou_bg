package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizProductStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizSkuInputOutputDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserSaleStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.EchartsSeriesDto;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsBetweenService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsDayService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderStatisticsDataTypeEnum;
import com.wanhutong.backend.modules.enums.UserRoleOfficeEnum;
import com.wanhutong.backend.modules.sys.dao.UserDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 统计相关Controller
 *
 * @author Ma.Qiang
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/statistics/between")
public class BizStatisticsBetweenController extends BaseController {

    protected Logger LOGGER = LoggerFactory.getLogger(BizStatisticsBetweenController.class);


    @Resource
    private BizStatisticsBetweenService bizStatisticsBetweenService;

    @Resource
    private BizStatisticsService bizStatisticsService;

    @Resource
    private UserDao userDao;

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
    @RequestMapping(value = {"userSale"})
    public String userSale(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsBetweenService.getBizPurchasingList(OfficeTypeEnum.PURCHASINGCENTER.getType()));

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
    @RequestMapping(value = {"userSaleData"})
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
    @RequestMapping(value = {"user"})
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
    @RequestMapping(value = {"userData"})
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
    @RequestMapping(value = {"product"})
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
    @RequestMapping(value = {"productData"})
    @ResponseBody
    public String product(HttpServletRequest request, String startDate, String endDate, Integer variId, Integer dataType, Integer purchasingId) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        List<BizProductStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.productStatisticData(startDate, endDate, variId, purchasingId);
        List<String> nameList = Lists.newArrayList();

        List<Object> seriesDataList = Lists.newArrayList();
        List<Object> allList = Lists.newArrayList();
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
            allList.add(o.getVendorName().concat("-").concat(o.getItemNo()).concat("-").concat(o.getCount()+"").concat("-")
                    .concat(o.getTotalMoney()+"").concat("-").concat(o.getClickCount()+""));
        });
        echartsSeriesDto.setName("商品销量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("AllList", allList);
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
    @RequestMapping(value = {"productDataDownload"})
    @ResponseBody
    public void productDataDownload(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String imgUrl,
                                    String startDate,
                                    String endDate,
                                    Integer variId,
                                    Integer purchasingId) throws IOException {
        List<BizProductStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.productStatisticData(startDate, endDate, variId, purchasingId);

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
            rowIndex++;
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
    @RequestMapping(value = {"order"})
    public String order(HttpServletRequest request, String startDate, String endDate) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", StringUtils.isBlank(startDate) ? simpleDateFormat.format(cal.getTime()) : startDate);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", StringUtils.isBlank(endDate) ? simpleDateFormat.format(cal.getTime()) : endDate);
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
    @RequestMapping(value = {"orderData"})
    @ResponseBody
    public String orderData(HttpServletRequest request, String startDate, String endDate, String lineChartType, String barChartType, String centerType) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        return JSONObject.fromObject(getOrderData(startDate, endDate, lineChartType, barChartType, centerType)).toString();
    }

    /**
     * 订单相关统计数据
     *
     * @param startDate     开始时间
     * @param lineChartType 线图数据类型
     * @param barChartType  柱图数据类型
     * @return 订单相关统计数据
     */
    private Map<String, Object> getOrderData(String startDate, String endDate, String lineChartType, String barChartType, String centerType) {
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
            logger.error("between get order data error", e);
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
                            o.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                            centerType
                    ));
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
                                    lastMonth.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                                    centerType
                            );
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
            case RECEIVE:
                officeNameSet.forEach(o -> {
                    EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
                    echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

                    List<Object> dataList = Lists.newArrayList();
                    for (int i = dataMap.size() - 1; i >= 0; i--) {
                        dataList.add(dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o) != null ? dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).get(o).getReceiveTotal() : 0);
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
    @RequestMapping(value = {"sku"})
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
    @RequestMapping(value = {"skuData"})
    @ResponseBody
    public String sku(HttpServletRequest request, String month, String type, Integer variId) throws ParseException {

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
        } else {
            List<BizProductStatisticsDto> statisticsDtoList = bizStatisticsBetweenService.skuAllStatisticData(variId);
            for (BizProductStatisticsDto bizProductStatisticsDto : statisticsDtoList) {
                seriesDataList.add(bizProductStatisticsDto.getUpSkuCount());
                nameList.add(bizProductStatisticsDto.getMonthDate());
            }
        }

        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        echartsSeriesDto.setName("商品新增数量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("nameList", nameList);
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        System.out.println(JSONObject.fromObject(paramMap).toString());
        return JSONObject.fromObject(paramMap).toString();
    }


    /**
     * 采购中心订单表格统计
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "orderTable")
    public String orderTable(HttpServletRequest request, String startDate, String endDate) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", StringUtils.isBlank(startDate) ? simpleDateFormat.format(cal.getTime()) : startDate);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        request.setAttribute("endDate", StringUtils.isBlank(endDate) ? simpleDateFormat.format(cal.getTime()) : endDate);
        return "modules/biz/statistics/bizStatisticsOrderBetweenTable";
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
    public Map<String, BizOrderStatisticsDto> centOrderTable(HttpServletRequest request, String startDate, String endDate, String centerType, Model model) throws ParseException {
        //当前月时间
        Map<String, BizOrderStatisticsDto> resultMap = bizStatisticsBetweenService.orderStatisticData(startDate, endDate, centerType);

        //合并
        HashMap<String, BizOrderStatisticsDto> map = new HashMap<>();

        for (Map.Entry<String, BizOrderStatisticsDto> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            BizOrderStatisticsDto value = entry.getValue();
            if (map.get(key) != null) {
                map.get(key).setOrderCount(value.getOrderCount());
                map.get(key).setTotalMoney(value.getTotalMoney() == null ? new BigDecimal(0) : value.getTotalMoney());
                map.get(key).setProfitPrice(value.getProfitPrice() == null ? new BigDecimal(0) : value.getProfitPrice());
                map.get(key).setBuyPrice(value.getBuyPrice() == null ? new BigDecimal(0) : value.getBuyPrice());
            } else {
                BizOrderStatisticsDto ordStaDto = new BizOrderStatisticsDto();
                ordStaDto.setOrderCount(value.getOrderCount());
                ordStaDto.setTotalMoney(value.getTotalMoney() == null ? new BigDecimal(0) : value.getTotalMoney());
                ordStaDto.setProfitPrice(value.getProfitPrice() == null ? new BigDecimal(0) : value.getProfitPrice());
                ordStaDto.setBuyPrice(value.getBuyPrice() == null ? new BigDecimal(0) : value.getBuyPrice());
                map.put(key, ordStaDto);
            }
        }

        model.addAttribute("map", resultMap);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        return map;
    }

    /**
     * 采购中心订单统计表格数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "centOrderTableDownload")
    public void centOrderTableDownload(
            HttpServletRequest request,
            HttpServletResponse response,
            String startDate, String endDate,
            Model model, String imgUrl, String centerType
    ) throws ParseException, IOException {
        Map<String, BizOrderStatisticsDto> resultMap = bizStatisticsBetweenService.orderStatisticData(startDate, endDate, centerType);

        String fileName = "订单统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell = header.createCell(0);
        hCell.setCellValue("采购中心");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue("销售额");
        HSSFCell hCell2 = header.createCell(2);
        hCell2.setCellValue("利润");
        HSSFCell hCell3 = header.createCell(3);
        hCell3.setCellValue("订单量");

        for (String key : resultMap.keySet()) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex++;
            BizOrderStatisticsDto bizOrderStatisticsDto = resultMap.get(key);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(bizOrderStatisticsDto.getOfficeName());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(bizOrderStatisticsDto.getTotalMoney().toString());
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(bizOrderStatisticsDto.getProfitPrice().toString());
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(bizOrderStatisticsDto.getOrderCount());
        }

        int index = 10;
        HSSFRow row = sheet.createRow(index + 3);
        HSSFCell headerCell = row.createCell(0);
        headerCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
        headerCell.setCellValue("图表");

        row = sheet.createRow(index + 6);
        HSSFCell cells = row.createCell(0);
        cells.setCellType(HSSFCell.CELL_TYPE_BLANK);
        // 将图片写入流中
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();

        try {
            String[] url = imgUrl.split(",");
            String u = url[1];
            //Base64解码
            byte[] buffer = new BASE64Decoder().decodeBuffer(u);
            //生成图片
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HSSFPatriarch patri = sheet.createDrawingPatriarch();
        HSSFClientAnchor anchor = new HSSFClientAnchor(5, 5, 5, 5,
                (short) 1, index + 6, (short) 20, 45);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));


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
    @RequestMapping(value = {"userSaleDataDownload"})
    public void userSaleDataDownload(HttpServletRequest request,
                                     HttpServletResponse response,
                                     String imgUrl,
                                     String imgUrl1,
                                     String startDate,
                                     String endDate,
                                     Integer purchasingId,
                                     String usName) throws IOException {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);


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
            rowIndex++;
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
    @RequestMapping(value = {"userDataDownload"})
    @ResponseBody
    public void userDataDownload(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String startDate,
                                 String endDate,
                                 String imgUrl
    ) throws IOException {
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userStatisticData(startDate, endDate);

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
            rowIndex++;
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
     * 利润统计数据下载
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profitDataDownload"})
    public void profitDataDownload(HttpServletRequest request,
                                   HttpServletResponse response,
                                   String startDate,
                                   String endDate,
                                   String imgUrl,
                                   String imgUrl1,
                                   String centerType
    ) throws IOException {
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
                            o.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                            centerType
                    ));
            monthList.add(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT));
        });
        Collections.reverse(monthList);

        Set<String> officeNameSet = Sets.newHashSet();
        monthDateList.forEach(o -> {
            officeNameSet.addAll(dataMap.get(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)) != null ? dataMap.get(o.toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)).keySet() : CollectionUtils.EMPTY_COLLECTION);
        });
        officeNameSet.removeAll(Collections.singleton(null));

        String fileName = "利润统计.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell0 = header.createCell(0);
        hCell0.setCellValue("采购中心");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue(startDate + "-" + endDate);

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
                (short) 5, 5, (short) 20, 20);
        patri.createPicture(anchor, wb.addPicture(
                outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));


        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename="
                + URLEncoder.encode(fileName, "UTF-8"));
        wb.write(response.getOutputStream());
    }


    /**
     * 利润统计
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profit"})
    public String profit(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        return "modules/biz/statistics/bizStatisticsProfitBetween";
    }

    /**
     * 利润统计数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"profitData"})
    @ResponseBody
    public String profitData(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String centerType) {
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
                            o.plusDays(finalBetweenDays.intValue()).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT),
                            centerType
                            ));
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
            seriesList.add(
                    bizStatisticsBetweenService.genEchartsSeriesDto(
                            officeNameSet,
                            dataMap.get(monthDateList.get(i).toString(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT)),
                            monthDateList.get(i),
                            String.valueOf(OrderStatisticsDataTypeEnum.PROFIT.getCode())));
        }

        seriesList.removeAll(Collections.singleton(null));

        paramMap.put("seriesList", seriesList);
        paramMap.put("dataMap", dataMap);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesList));

        return JSONObject.fromObject(paramMap).toString();
    }



    /**
     * 个人利润统计
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"singleUserProfit"})
    public String singleUserProfit(HttpServletRequest request, HttpServletResponse response) {
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
        cal.add(Calendar.DAY_OF_MONTH, 6);
        request.setAttribute("endDate", simpleDateFormat.format(cal.getTime()));
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        return "modules/biz/statistics/bizStatisticsSingleUserProfitBetween";
    }

    /**
     * 个人利润统计
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("biz:statistics:profit:view")
    @RequestMapping(value = {"singleUserProfitData"})
    @ResponseBody
    public String singleUserProfitData(HttpServletRequest request, HttpServletResponse response, String startDate, String endDate, String usName, Integer purchasingId) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);
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
    @RequestMapping(value = {"singleUserProfitDataDownload"})
    public void singleUserProfitDataDownload(HttpServletRequest request,
                                             HttpServletResponse response,
                                             String imgUrl,
                                             String imgUrl1,
                                             String startDate,
                                             String endDate,
                                             Integer purchasingId) throws IOException {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);

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
    @RequestMapping(value = {"singleUserProfitDataTable"})
    public String singleUserProfitDataTable(String startDate, String endDate, HttpServletResponse response, HttpServletRequest request, Integer purchasingId) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("startDate", startDate);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("endDate", endDate);
        return "modules/biz/statistics/bizStatisticsSingleUserProfitBetweenTable";
    }

    /**
     * 用户业绩相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:userSale:view")
    @RequestMapping(value = {"userSaleDataTable"})
    public String userSaleDataTable(HttpServletRequest request, String startDate, String endDate, Integer purchasingId, String usName) {
        List<BizUserSaleStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userSaleStatisticData(startDate, endDate, purchasingId);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("startDate", startDate);
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("endDate", endDate);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        return "modules/biz/statistics/bizStatisticsUserSaleBetweenTable";
    }

    /**
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"customTable"})
    public String customTable(HttpServletRequest request, String startDate, String endDate) {
        List<BizUserStatisticsDto> bizProductStatisticsDtos = bizStatisticsBetweenService.userStatisticData(startDate, endDate);
        request.setAttribute("dataList", bizProductStatisticsDtos);
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        return "modules/biz/statistics/bizStatisticsCustomBetweenTable";
    }

    /**
     * 产品统计（个人 表格数据）
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = "productAnalysisTables")
    public String productAnalysisTables (HttpServletRequest request, String startDate, String endDate){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("varietyList", bizStatisticsService.getBizVarietyInfoList());
        request.setAttribute("purchasingList", bizStatisticsService.getOfficeList("8"));
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        List<BizProductStatisticsDto> productStatisticsList = bizStatisticsService.productStatisticData(startDate, 1, null);
        request.setAttribute("productStatisticsList", productStatisticsList);
        return "modules/biz/statistics/bizStatisticsProductBetweenTables";
    }


    /**
     * 供应商供货额
     */
    @RequiresPermissions("biz:statistics:vendorProductPrice:view")
    @RequestMapping(value = "vendorProductPriceTables")
    public String vendorProductPrice (HttpServletResponse response, HttpServletRequest request,
                                      String startDate, String endDate, String vendName, String methodType){
        int pageSize = 20;
        long startTime = System.currentTimeMillis();

        if (StringUtils.isBlank(startDate)) {
            Calendar cal = Calendar.getInstance();
            //获取本周一的日期
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.add(Calendar.DAY_OF_MONTH, -7);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
            startDate = simpleDateFormat.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 6);
            endDate = simpleDateFormat.format(cal.getTime());
        }

        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("vendName", vendName);

        List<BizOrderStatisticsDto> result = bizStatisticsBetweenService.vendorProductPrice(startDate, endDate, vendName);

        result.sort((o1, o2) -> Integer.compare(o2.getOrderCount(), o1.getOrderCount()));

        Page<Office> page = new Page<>(request, response);
        page.setPageSize(pageSize);
        page.setCount(result.size());
        page.initialize();
        List<BizOrderStatisticsDto> pairs = result.subList((page.getPageNo() - 1) * pageSize, Math.min(page.getPageNo() * pageSize, result.size()));
        request.setAttribute("result", pairs);
        request.setAttribute("page", page);

        if ("download".equalsIgnoreCase(methodType)) {
            String fileName = "供应金额统计.xls";
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet = wb.createSheet();
            sheet.autoSizeColumn(1, true);

            int rowIndex = 0;
            HSSFRow header = sheet.createRow(rowIndex);
            rowIndex++;
            HSSFCell hCell = header.createCell(0);
            hCell.setCellValue("供应商ID");
            HSSFCell hCell1 = header.createCell(1);
            hCell1.setCellValue("供应商名称");
            HSSFCell hCell2 = header.createCell(2);
            hCell2.setCellValue("供应次数");
            HSSFCell hCell3 = header.createCell(3);
            hCell3.setCellValue("供应金额");


            for (BizOrderStatisticsDto o : result) {
                HSSFRow row = sheet.createRow(rowIndex);
                rowIndex++;
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(o.getOfficeId());
                HSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(o.getOfficeName());
                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(o.getOrderCount());
                HSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(o.getTotalMoney().toString());
            }

            try {
                response.setContentType("application/msexcel;charset=utf-8");
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                wb.write(response.getOutputStream());
            } catch (IOException e) {
                LOGGER.error("export skuInputOutputRecord error", e);
            }
            return null;
        }

        return "modules/biz/statistics/bizStatisticsVendorProductPriceBetweenTables";
    }

    /**
     * 供应商供货额 SKU
     */
    @RequiresPermissions("biz:statistics:vendorProductPrice:view")
    @RequestMapping(value = "vendorSkuPriceTables")
    public String vendorSkuPriceTables (HttpServletRequest request, String startDate, String endDate, Integer officeId){

        List<BizOrderStatisticsDto> result = bizStatisticsBetweenService.vendorSkuPrice(startDate, endDate, officeId);

        result.sort((o1, o2) -> Integer.compare(o2.getOrderCount(), o1.getOrderCount()));


        request.setAttribute("result", result);
        return "modules/biz/statistics/bizStatisticsVendorSkuPriceBetweenTables";
    }

    /**
     * 采购专员下采购商有效订单
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:customOrder:view")
    @RequestMapping(value = "customOrder")
    public String customOrder(HttpServletRequest request) {
        User user = new User();
        Role role = new Role();
        role.setId(new Integer(UserRoleOfficeEnum.PURCHASE.getType()));
        user.setRole(role);
        List<User> userList = userDao.findList(user);

        request.setAttribute("adminPath", adminPath);
        request.setAttribute("userList", userList);

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
        return "modules/biz/statistics/bizStatisticsCustomOrderBetween";
    }

    /**
     * 采购专员下采购商有效订单相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:customOrder:view")
    @RequestMapping(value = "customOrderData")
    @ResponseBody
    public String customOrder(HttpServletRequest request, String startDate, String endDate, Integer variId) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        List<BizCustomCenterConsultant> bizCustomCenterConsultantList = bizStatisticsBetweenService.customOrderList(startDate, endDate, variId);
        List<String> nameList = Lists.newArrayList();

        List<Object> allList = Lists.newArrayList();
        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        bizCustomCenterConsultantList.forEach(o -> {
            seriesDataList.add(o.getOrderCount());
            nameList.add(o.getCustoms().getName());

            allList.add(o.getCustoms().getName().concat("-").concat(o.getCenters().getName()).concat("-").concat(o.getConsultants().getName().split("-")[0]).concat("-").concat(String.valueOf(o.getOrderCount())));
        });
        echartsSeriesDto.setName("采购商订单量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        Boolean retFlag = CollectionUtils.isNotEmpty(seriesDataList);
        if (retFlag){
            paramMap.put("legendData", "有效订单数(" + bizCustomCenterConsultantList.get(0).getCenters().getName() + ")");
        }
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("AllList", allList);
        paramMap.put("ret", retFlag);
        return JSONObject.fromObject(paramMap).toString();
    }

    /**
     * 采购专员下采购商有效订单（表格数据）
     */
    @RequiresPermissions("biz:statistics:customOrder:view")
    @RequestMapping(value = "customOrderAnalysisTables")
    public String customOrderAnalysisTables (HttpServletRequest request, String startDate, String endDate, Integer consultantId){
        User user = new User();
        Role role = new Role();
        role.setId(new Integer(UserRoleOfficeEnum.PURCHASE.getType()));
        user.setRole(role);
        List<User> userList = userDao.findList(user);

        request.setAttribute("adminPath", adminPath);
        request.setAttribute("userList", userList);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        List<BizCustomCenterConsultant> bizCustomCenterConsultantList = bizStatisticsBetweenService.customOrderList(startDate, endDate, consultantId);
        request.setAttribute("bizCustomCenterConsultantList", bizCustomCenterConsultantList);
        return "modules/biz/statistics/bizStatisticsCustomOrderBetweenTables";
    }

    /**
     * 采购专员下采购商有效订单相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:customOrder:view")
    @RequestMapping(value = {"customOrderDataDownload"})
    @ResponseBody
    public void customOrderDataDownload(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String imgUrl,
                                    String startDate,
                                    String endDate,
                                    Integer consultantId) throws IOException {
        List<BizCustomCenterConsultant> bizCustomCenterConsultantList = bizStatisticsBetweenService.customOrderList(startDate, endDate, consultantId);

        String fileName = "采购商订单统计分析.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell = header.createCell(0);
        hCell.setCellValue("经销店名称");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue("采购中心");
        HSSFCell hCell2 = header.createCell(2);
        hCell2.setCellValue("采购专员");
        HSSFCell hCell3 = header.createCell(3);
        hCell3.setCellValue("订单数量");


        for (BizCustomCenterConsultant o : bizCustomCenterConsultantList) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex++;
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(o.getCustoms().getName());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(o.getCenters().getName());
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(o.getConsultants().getName());
            HSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(o.getOrderCount());
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
     * 采购专员关联下具有有效订单的采购商的数量统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:consultantOrder:view")
    @RequestMapping(value = {"consultantOrder"})
    public String consultantOrder(HttpServletRequest request) {
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
        return "modules/biz/statistics/bizStatisticsConsultantOrderBetween";
    }

    /**
     * 采购专员关联下具有有效订单的采购商的数量数据统计
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:consultantOrder:view")
    @RequestMapping(value = "consultantOrderData")
    @ResponseBody
    public String consultantOrderData(HttpServletRequest request, String startDate, String endDate, Integer purchasingId) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return JSONObject.fromObject(ImmutableMap.of("ret", false)).toString();
        }
        List<BizCustomCenterConsultant> resultList = bizStatisticsBetweenService.consultantOrderList(startDate, endDate, purchasingId);
        List<String> nameList = Lists.newArrayList();

        List<Object> allList = Lists.newArrayList();
        List<Object> seriesDataList = Lists.newArrayList();
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        resultList.forEach(o -> {
            seriesDataList.add(o.getOrderCount());
            nameList.add(o.getConsultants().getName());

            allList.add(o.getCenters().getName().concat("-").concat(o.getConsultants().getName().split("-")[0]).concat("-").concat(String.valueOf(o.getOrderCount())));
        });
        echartsSeriesDto.setName("采购商订单量");
        echartsSeriesDto.setData(seriesDataList);

        Map<String, Object> paramMap = Maps.newHashMap();
        Boolean retFlag = CollectionUtils.isNotEmpty(seriesDataList);
        if (retFlag){
            paramMap.put("legendData", "有效订单数(" + resultList.get(0).getCenters().getName() + ")");
        }
        paramMap.put("seriesList", echartsSeriesDto);
        paramMap.put("nameList", nameList);
        paramMap.put("AllList", allList);
        paramMap.put("ret", retFlag);
        return JSONObject.fromObject(paramMap).toString();
    }

    /**
     * 采购专员关联下具有有效订单的采购商的数量数据统计（表格数据）
     */
    @RequiresPermissions("biz:statistics:consultantOrder:view")
    @RequestMapping(value = "consultantOrderAnalysisTables")
    public String consultantOrderAnalysisTables (HttpServletRequest request, String startDate, String endDate, Integer purchasingId){
        request.setAttribute("adminPath", adminPath);
        request.setAttribute("purchasingList", bizStatisticsBetweenService.getBizPurchasingList("8"));
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        List<BizCustomCenterConsultant> resultList = bizStatisticsBetweenService.consultantOrderList(startDate, endDate, purchasingId);
        request.setAttribute("resultList", resultList);
        return "modules/biz/statistics/bizStatisticsConsultantOrderBetweenTables";
    }

    /**
     * 采购专员关联下具有有效订单的采购商的数量统计导出
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:customOrder:view")
    @RequestMapping(value = {"consultantOrderDataDownload"})
    @ResponseBody
    public void consultantOrderDataDownload(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String imgUrl,
                                        String startDate,
                                        String endDate,
                                        Integer purchasingId) throws IOException {
        List<BizCustomCenterConsultant> resultList = bizStatisticsBetweenService.consultantOrderList(startDate, endDate, purchasingId);

        String fileName = "具有有效订单的采购商的数量数据统计分析.xls";
        HSSFWorkbook wb = new HSSFWorkbook();

        HSSFSheet sheet = wb.createSheet();
        sheet.autoSizeColumn(1, true);

        int rowIndex = 0;
        HSSFRow header = sheet.createRow(rowIndex);
        rowIndex++;
        HSSFCell hCell = header.createCell(0);
        hCell.setCellValue("采购中心");
        HSSFCell hCell1 = header.createCell(1);
        hCell1.setCellValue("采购专员");
        HSSFCell hCell2 = header.createCell(2);
        hCell2.setCellValue("订单数量");


        for (BizCustomCenterConsultant o : resultList) {
            HSSFRow row = sheet.createRow(rowIndex);
            rowIndex++;
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(o.getCenters().getName());
            HSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(o.getConsultants().getName());
            HSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(o.getOrderCount());
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
     * 出入库记录
     */
    @RequiresPermissions("biz:statistics:skuInputOutputRecord:view")
    @RequestMapping(value = "skuInputOutputRecord")
    public String skuInputOutputRecord (HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value="dataType", required = false, defaultValue = "3") Integer dataType,
                                        String startDate, String endDate, String invName, String skuItemNo, String methodType){

        int pageSize = 20;
        long startTime = System.currentTimeMillis();

        List<BizSkuInputOutputDto> result = bizStatisticsBetweenService.skuInputOutputRecord(startDate, endDate, invName, skuItemNo);

        switch (dataType) {
            case 1:
                result.removeIf(o -> o.getDataType() != 1);
                break;
            case 0:
                result.removeIf(o -> o.getDataType() != 0);
                break;
            default:
                break;
        }

        result.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));

        Page<Office> page = new Page<>(request, response);
        page.setPageSize(pageSize);
        page.setCount(result.size());
        page.initialize();
        List<BizSkuInputOutputDto> pairs = result.subList((page.getPageNo() - 1) * pageSize, Math.min(page.getPageNo() * pageSize, result.size()));

        request.setAttribute("page", page);
        request.setAttribute("invName", invName);
        request.setAttribute("skuItemNo", skuItemNo);
        request.setAttribute("dataType", dataType);
        request.setAttribute("result", pairs);

        if ("download".equalsIgnoreCase(methodType)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fileName = "出入库记录.xls";
            HSSFWorkbook wb = new HSSFWorkbook();

            HSSFSheet sheet = wb.createSheet();
            sheet.autoSizeColumn(1, true);

            int rowIndex = 0;
            HSSFRow header = sheet.createRow(rowIndex);
            rowIndex++;
            HSSFCell hCell = header.createCell(0);
            hCell.setCellValue("仓库名称");
            HSSFCell hCell1 = header.createCell(1);
            hCell1.setCellValue("数量");
            HSSFCell hCell2 = header.createCell(2);
            hCell2.setCellValue("类型");
            HSSFCell hCell3 = header.createCell(3);
            hCell3.setCellValue("商品名称");
            HSSFCell hCell4 = header.createCell(4);
            hCell4.setCellValue("商品编号");
            HSSFCell hCell5 = header.createCell(5);
            hCell5.setCellValue("时间");


            for (BizSkuInputOutputDto o : result) {
                HSSFRow row = sheet.createRow(rowIndex);
                rowIndex++;
                HSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(o.getInvName());
                HSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(o.getCountNumber());
                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(o.getDataType() == 1 ? "入库" : "出库");
                HSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(o.getSkuName());
                HSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(o.getItemNo());
                HSSFCell cell5 = row.createCell(5);
                cell5.setCellValue(simpleDateFormat.format(o.getCreateTime()));
            }

            try {
                response.setContentType("application/msexcel;charset=utf-8");
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                wb.write(response.getOutputStream());
            } catch (IOException e) {
                LOGGER.error("export skuInputOutputRecord error", e);
            }
            return null;
        }

        return "modules/biz/statistics/bizStatisticsInputOutputBetweenTables";
    }
}

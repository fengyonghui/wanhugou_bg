package com.wanhutong.backend.modules.biz.web.statistics;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsDayService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsPlatformService;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import com.wanhutong.backend.modules.sys.entity.Office;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @RequestMapping(value = {"user", ""})
    public String user(HttpServletRequest request) {
        request.setAttribute("adminPath", adminPath);
        Calendar cal = Calendar.getInstance();
        //获取本周一的日期
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BizStatisticsDayService.DAY_PARAM_DATE_FORMAT);
        request.setAttribute("startDate", simpleDateFormat.format(cal.getTime()));
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
        EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
        EchartsSeriesDto echartsCountSeriesDto = new EchartsSeriesDto();
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
                    for (BizOrderStatisticsDto b : bizOrderStatisticsDtoList) {
                        if (b.getCreateDate().equals(o)) {
                            value = b.getTotalMoney();
                            count = b.getOrderCount();
                        }
                    }
                    seriesDataList.add(value);
                    seriesCountDataList.add(count);
                    nameList.add(o);
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            bizOrderStatisticsDtoList.forEach(o -> {
                seriesDataList.add(o.getTotalMoney());
                seriesCountDataList.add(o.getOrderCount());
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

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("seriesList", Lists.newArrayList(echartsSeriesDto, echartsCountSeriesDto));
        paramMap.put("nameList", nameList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesDataList));
        return paramMap;
    }

}

package com.wanhutong.backend.modules.biz.web.statistics;


import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.EchartsSeriesDto;
import com.wanhutong.backend.modules.biz.service.statistics.BizStatisticsService;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
     * 用户相关统计数据
     *
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"user", ""})
    public String user(HttpServletRequest request) {


        List<String> districtList = Lists.newArrayList("河南信阳", "山东济南", "山东临沂", "山东德州", "山东潍坊");
        List<Integer> salesVolumeList = Lists.newArrayList(10, 80, 30, 44, 55);
        List<Integer> yieldRateList = Lists.newArrayList(10, 98, 77, 44, 22);


        request.setAttribute("districtlist", JSONUtils.toJSONString(districtList));
        request.setAttribute("salesVolumeList", JSONUtils.toJSONString(salesVolumeList));
        request.setAttribute("yieldRateList", JSONUtils.toJSONString(yieldRateList));


        List<Integer> novSaleroomList = Lists.newArrayList(100000, 300300, 800000, 1200000, 900000);
        List<Integer> decSaleroomList = Lists.newArrayList(200000, 700300, 600000, 440000, 500000);
        List<Integer> janSaleroomList = Lists.newArrayList(900000, 200300, 505000, 100000, 700000);

        request.setAttribute("novSaleroomList", JSONUtils.toJSONString(novSaleroomList));
        request.setAttribute("decSaleroomList", JSONUtils.toJSONString(decSaleroomList));
        request.setAttribute("janSaleroomList", JSONUtils.toJSONString(janSaleroomList));
        return "modules/biz/statistics/bizStatisticsUser";
    }

    /**
     * 订单相关统计数据
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
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:order:view")
    @RequestMapping(value = {"orderData", ""})
    @ResponseBody
    public String orderData(HttpServletRequest request, String month) {
        LocalDateTime selectMonth = StringUtils.isBlank(month) ? LocalDateTime.now() : LocalDateTime.parse(month);
        LocalDateTime lastMonth = selectMonth.minusMonths(1);
        LocalDateTime beforeLastMonth = lastMonth.minusMonths(1);

        // 根据日期取当月订单数据
        Map<String, BizOrderStatisticsDto> selectDataMap = bizStatisticsService.orderStaticData(selectMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT));
        Map<String, BizOrderStatisticsDto> lastDataMap = bizStatisticsService.orderStaticData(lastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT));
        Map<String, BizOrderStatisticsDto> beforeLastDataMap = bizStatisticsService.orderStaticData(beforeLastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT));

        Set<String> officeNameSet = Sets.newHashSet();
        officeNameSet.addAll(selectDataMap.keySet());
        officeNameSet.addAll(lastDataMap.keySet());
        officeNameSet.addAll(beforeLastDataMap.keySet());


        Map<String, Object> paramMap = Maps.newHashMap();
        List<String> monthList = Lists.newArrayList(
                beforeLastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT),
                lastMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT),
                selectMonth.toString(BizStatisticsService.PARAM_DATE_FORMAT)
        );
        paramMap.put("officeNameSet", officeNameSet);
        paramMap.put("monthList", monthList);


        ArrayList<EchartsSeriesDto> seriesList = Lists.newArrayList(
                bizStatisticsService.genEchartsSeriesDto(officeNameSet, beforeLastDataMap, beforeLastMonth),
                bizStatisticsService.genEchartsSeriesDto(officeNameSet, lastDataMap, lastMonth),
                bizStatisticsService.genEchartsSeriesDto(officeNameSet, selectDataMap, selectMonth)
        );
        seriesList.removeAll(Collections.singleton(null));

        paramMap.put("seriesList", seriesList);

        ArrayList<EchartsSeriesDto> rateSeriesList = Lists.newArrayList();
        officeNameSet.forEach(o -> {
            EchartsSeriesDto echartsSeriesDto = new EchartsSeriesDto();
            echartsSeriesDto.setType(EchartsSeriesDto.SeriesTypeEnum.LINE.getCode());

            List<Object> dataList = Lists.newArrayList();

            dataList.add(beforeLastDataMap.get(o) != null ? beforeLastDataMap.get(o).getTotalMoney() : 0);
            dataList.add(lastDataMap.get(o) != null ? lastDataMap.get(o).getTotalMoney() : 0);
            dataList.add(selectDataMap.get(o) != null ? selectDataMap.get(o).getTotalMoney() : 0);

            echartsSeriesDto.setData(dataList);
            echartsSeriesDto.setName(o);
            rateSeriesList.add(echartsSeriesDto);
        });
        seriesList.removeAll(Collections.singleton(null));


        paramMap.put("rateSeriesList", rateSeriesList);
        paramMap.put("ret", CollectionUtils.isNotEmpty(seriesList));
        return JSONObject.fromObject(paramMap).toString();
    }


}

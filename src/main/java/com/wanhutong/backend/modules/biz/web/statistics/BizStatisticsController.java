package com.wanhutong.backend.modules.biz.web.statistics;


import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Lists;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 统计相关Controller
 *
 * @author Ma.Qiang
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/statistics")
public class BizStatisticsController {


    /**
     * 用户相关统计数据
     * @param request
     * @return
     */
    @RequiresPermissions("biz:statistics:user:view")
    @RequestMapping(value = {"user", ""})
    public String user(HttpServletRequest request) {

        List<String> districtList = Lists.newArrayList("河南信阳", "山东济南", "山东临沂", "山东德州", "山东潍坊");
        List<Integer> salesVolumeList = Lists.newArrayList(10,80,30,44,55);
        List<Integer> yieldRateList = Lists.newArrayList(10,98,77,44,22);


        request.setAttribute("districtlist", JSONUtils.toJSONString(districtList));
        request.setAttribute("salesVolumeList", JSONUtils.toJSONString(salesVolumeList));
        request.setAttribute("yieldRateList", JSONUtils.toJSONString(yieldRateList));


        List<Integer> novSaleroomList = Lists.newArrayList(100000,300300,800000,1200000,900000);
        List<Integer> decSaleroomList = Lists.newArrayList(200000,700300,600000,440000,500000);
        List<Integer> janSaleroomList = Lists.newArrayList(900000,200300,505000,100000,700000);

        request.setAttribute("novSaleroomList", JSONUtils.toJSONString(novSaleroomList));
        request.setAttribute("decSaleroomList", JSONUtils.toJSONString(decSaleroomList));
        request.setAttribute("janSaleroomList", JSONUtils.toJSONString(janSaleroomList));
        return "modules/biz/statistics/bizStatisticsUser";
    }


}

package com.wanhutong.backend.modules.biz.service.statistics;



import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    /**
     * 请求参数日期格式
     */
    public static final String PARAM_DATE_FORMAT = "yyyy-MM";


    /**
     * 用户相关统计数据
     * @return 用户统计数据
     */
    public String user() {



        return null;
    }


    /**
     * 根据月份取订单统计相关数据
     *
     * @param month 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public Map<String, BizOrderStatisticsDto> orderStaticData(String month) {
        List<BizOrderStatisticsDto> orderTotalAndCountByCreateTimeMonthStatus = bizOrderHeaderDao.getOrderTotalAndCountByCreateTimeMonthStatus(month, OrderHeaderBizStatusEnum.COMPLETE.getState(), OfficeTypeEnum.PURCHASINGCENTER.getType());
        Map<String, BizOrderStatisticsDto> resultMap = Maps.newHashMap();
        orderTotalAndCountByCreateTimeMonthStatus.forEach(o -> {
            resultMap.putIfAbsent(o.getOfficeName(), o);
        });
        return resultMap;
    }



}

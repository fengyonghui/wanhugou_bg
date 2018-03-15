package com.wanhutong.backend.modules.biz.service.statistics;


import com.google.common.collect.Lists;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.dto.*;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


/**
 * 统计相关Service
 *
 * @author Ma.Qiang
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class BizStatisticsPlatformService {

    @Resource
    private BizOrderHeaderDao bizOrderHeaderDao;
    
    @Resource
    private OfficeDao officeDao;

    /**
     * 请求参数日期格式
     */
    public static final String PARAM_DATE_FORMAT = "yyyy-MM";


    /**
     * 根据月份取订单统计相关数据
     *
     * @param startDate 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizOrderStatisticsDto> orderStatisticData(String startDate, String endDate, String type, String centerType, String orderType) {
        if (StringUtils.isBlank(centerType) || Integer.valueOf(centerType) == 0) {
            return bizOrderHeaderDao.getAllValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null);
        }
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, null);
    }

    /**
     * 根据月份取用户统计相关数据
     *
     * @param startDate 取数据的月份
     * @return 根据不同机构分类的统计数据
     */
    public List<BizUserStatisticsDto> userStatisticData(String type, String startDate, String endDate, String centerType) {
        if (StringUtils.isBlank(type) || Integer.valueOf(type) == 0) {
            return bizOrderHeaderDao.getUserStatisticDataPlatform(type, startDate, endDate + " 23:59:59", centerType);
        }
        return bizOrderHeaderDao.getAllUserStatisticDataPlatform(type, startDate, endDate + " 23:59:59", centerType);
    }


    public List<BizOrderStatisticsDto> orderStatisticDataByOffice(String startDate, String endDate, String type, String centerType, String orderType, Integer id) {
        return bizOrderHeaderDao.getValidOrderTotalAndCount(startDate, endDate, OrderHeaderBizStatusEnum.INVALID_STATUS, type, centerType, orderType, id);
    }


    /**
     * 获取平台业务数据
     */
    public List<BizPlatformDataOverviewDto> getPlatformData(String startDate, String endDate) {
        List<BizPlatformDataOverviewDto> list = Lists.newArrayList();
        List<Office> listByType = officeDao.findListByType("8");
        listByType.removeIf(o -> o.getName().contains("测试"));
        listByType.forEach(o -> {
            List<BizOrderStatisticsDto> bizOrderStatisticsDtoList = orderStatisticDataByOffice(startDate, endDate, null, null, null, o.getId());
            System.out.println(bizOrderStatisticsDtoList);


        });

        for(int i = 0; i < 10; i ++) {
            BizPlatformDataOverviewDto bizPlatformDataOverviewDto = new BizPlatformDataOverviewDto();

            bizPlatformDataOverviewDto.setProvince("山东");
            bizPlatformDataOverviewDto.setName("山东临沂");
            bizPlatformDataOverviewDto.setProcurement(new BigDecimal(1000));
            bizPlatformDataOverviewDto.setAccumulatedSalesMonth(new BigDecimal(600));
            bizPlatformDataOverviewDto.setProcurementDay(new BigDecimal(300));
            bizPlatformDataOverviewDto.setStockAmount(new BigDecimal(400));

            list.add(bizPlatformDataOverviewDto);
        }
        return list;
    }
}

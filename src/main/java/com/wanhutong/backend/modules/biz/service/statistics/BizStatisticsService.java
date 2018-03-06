package com.wanhutong.backend.modules.biz.service.statistics;



import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


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
    public List<BizOrderStatisticsDto> orderStaticData(String month) {
        return bizOrderHeaderDao.getOrderTotalAndCountByCreateTimeMonthStatus(month, OrderHeaderBizStatusEnum.COMPLETE.getState(), OfficeTypeEnum.PURCHASINGCENTER.getType());
    }

}

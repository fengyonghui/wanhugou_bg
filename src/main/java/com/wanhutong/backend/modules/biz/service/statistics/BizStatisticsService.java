package com.wanhutong.backend.modules.biz.service.statistics;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


/**
 * 统计相关Service
 *
 * @author Ma.Qiang
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class BizStatisticsService {


    /**
     * 用户相关统计数据
     * @return 用户统计数据
     */
    public String user() {



        return null;
    }


}

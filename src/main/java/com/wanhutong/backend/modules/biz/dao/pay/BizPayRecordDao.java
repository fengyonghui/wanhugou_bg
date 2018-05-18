/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.pay;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交易记录DAO接口
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@MyBatisDao
public interface BizPayRecordDao extends CrudDao<BizPayRecord> {

    BizPayRecord findBizPayRecord(String payNum);

    List<BizOrderStatisticsDto> getReceiveData(@Param("startDate") String startDate, @Param("endDate")String endDate, @Param("centerType")String centerType);

    List<BizOrderStatisticsDto> getSingleReceiveData(@Param("startDate") String startDate, @Param("endDate")String endDate, @Param("officeId")String officeId);
}
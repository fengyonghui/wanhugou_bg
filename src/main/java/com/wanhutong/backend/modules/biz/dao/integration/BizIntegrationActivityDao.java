/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.integration;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分活动DAO接口
 * @author LX
 * @version 2018-09-16
 */
@MyBatisDao
public interface BizIntegrationActivityDao extends CrudDao<BizIntegrationActivity> {
    @Select("SELECT id,activity_name as activityName,integration_num as integrationNum,status from biz_integration_activity where activity_code =#{code}")
    BizIntegrationActivity getIntegrationByCode(@Param("code") String code);
}
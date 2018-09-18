/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.integration;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.integration.BizIntegrationActivity;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分活动DAO接口
 * @author LX
 * @version 2018-09-16
 */
@MyBatisDao
public interface BizIntegrationActivityDao extends CrudDao<BizIntegrationActivity> {
    @Select("SELECT id,activity_name as activityName,integration_num as integrationNum,status from biz_integration_activity where activity_code =#{code}")
    BizIntegrationActivity getIntegrationByCode(@Param("code") String code);


    void insertMiddle(List<BizIntegrationActivity> list);

    @Update("update sys_user_activity set status = 0 where activity_id = #{id}")
    void updateMiddleStatusByActivityId(@Param("id") Integer id);

    @Select("select GROUP_CONCAT(user_id) from sys_user_activity where activity_id = #{id}")
    String selectOfficeIdsByActivityId(@Param("id") Integer id);

    /**
     * 全部采购商数量
     * */
    @Select("SELECT count(*) FROM sys_office WHERE type = 6 AND STATUS = 1")
    Integer selectAllOfficeTotal();

    /**
     *  已下单采购商数量统计
     *
     * */
    @Select("select count(*) as total from (SELECT a.id,b.cust_id FROM sys_office a LEFT JOIN biz_order_header b ON b.cust_id = a.id  AND b.STATUS = 1 where a.status = 1 and a.type = 6 and b.cust_id is not null GROUP BY a.id ) u")
    Integer selectOrderOfficeTotal();

    /**
     *  未下单采购商数量
     * */
    @Select("select count(*) as total from (SELECT a.id,b.cust_id FROM sys_office a LEFT JOIN biz_order_header b ON b.cust_id = a.id  AND b.STATUS = 1 where a.status = 1 and a.type = 6 and b.cust_id is null GROUP BY a.id ) u")
    Integer selectUnOrderOfficeTotal();
}
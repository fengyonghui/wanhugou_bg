/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import org.apache.ibatis.annotations.Param;


/**
 * 排产计划DAO接口
 * @author 王冰洋
 * @version 2018-07-17
 */
@MyBatisDao
public interface BizSchedulingPlanDao extends CrudDao<BizSchedulingPlan> {

    /**
     * 通过表名和表Id获取排产entity
     * @param objectId
     * @param objectName
     * @return
     */
    BizSchedulingPlan getByObjectIdAndObjectName(@Param("objectId") Integer objectId, @Param("objectName") String objectName);

}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.process.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 通用流程DAO接口
 * @author Ma.Qiang
 * @version 2018-04-28
 */
@MyBatisDao
public interface CommonProcessDao extends CrudDao<CommonProcessEntity> {

    void updateCurrentByObject(@Param("objectId") Integer objectId, @Param("objectName")String objectName, @Param("current")int current);
}
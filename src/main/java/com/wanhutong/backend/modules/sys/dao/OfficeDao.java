/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.TreeDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.sys.entity.Office;

import java.util.List;

/**
 * 机构DAO接口
 * @author ThinkGem
 * @version 2014-05-16
 */
@MyBatisDao
public interface OfficeDao extends TreeDao<Office> {

    List<Office> selectInfo(Office office);

    List<Office> queryList(Office office);

    List<Office> findOfficeByIdToParent(BizCustomCenterConsultant bizCustomCenterConsultant);

    List<Office> findOfficeCustByIdToParent(Office office);
}

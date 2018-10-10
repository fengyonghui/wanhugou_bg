/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.TreeDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizUserStatisticsDto;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 根据type取office
     * @param type 类型
     * @return
     */
    List<Office> findListByType(String type);

    /**
     * 取所有的供应商
     * @param parentIds
     * @return
     */
    List<Office> findVent(String parentIds);

    /**
     * 取多个类型的office
     * @param typeList
     * @return
     */
    List<Office> findListByTypeList(@Param("typeList") List<String> typeList);

    List<Office> findCustomByOfficeId(Integer officeId);

    List<String> getCustParentIdByVendorId(@Param("vendorId")Integer vendorId);

    List<String> getCustIdByVendorId(@Param("vendorId")Integer vendorId);

    /**
     * 取新用户
     * @param startDate
     * @param endDate
     * @param officeId
     * @return
     */
    List<BizUserStatisticsDto> singleUserRegisterData(@Param("startDate")String startDate, @Param("endDate")String endDate, @Param("officeId")String officeId);

    /**
     *
     * @param bizCustomCenterConsultant
     * @return
     */
    List<Office> findOfficeById4Mobile(BizCustomCenterConsultant bizCustomCenterConsultant);
}

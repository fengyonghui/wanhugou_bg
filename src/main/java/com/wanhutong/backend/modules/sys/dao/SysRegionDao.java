/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色区域表DAO接口
 * @author zx
 * @version 2017-12-11
 */
@MyBatisDao
public interface SysRegionDao extends CrudDao<SysRegion> {


    /**
     * 根据ID 取公司地址的省份
     * @param id
     * @return
     */
    SysRegion queryOfficeProvinceById(Integer id);

    /**
     * 查询省市区
     * @return
     */
    List<SysRegion> findRegion(@Param("level")String level, @Param("code")Integer code);
	
}
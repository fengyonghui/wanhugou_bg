/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizFreightConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务费设置DAO接口
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
@MyBatisDao
public interface BizFreightConfigDao extends CrudDao<BizFreightConfig> {

    /**
     * 出列表页外的List查询
     * @param bizFreightConfig
     * @return
     */
    List<BizFreightConfig> findFreightList(BizFreightConfig bizFreightConfig);

    /**
     * 根据采购中心和分类查询服务费
     * @param centerId
     * @param variId
     * @return
     */
    List<BizFreightConfig> findListByOfficeAndVari(@Param("centerId")Integer centerId, @Param("variId")Integer variId);
}
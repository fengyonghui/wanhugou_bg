/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 销售采购备货中间表DAO接口
 * @author 张腾飞
 * @version 2018-01-09
 */
@MyBatisDao
public interface BizPoOrderReqDao extends CrudDao<BizPoOrderReq> {

    /**
     * 通过po单子获取销售采购备货中间表
     * @param bphId
     * @return
     */
    List<BizPoOrderReq> getByPo(@Param("bphId")Integer bphId);
}
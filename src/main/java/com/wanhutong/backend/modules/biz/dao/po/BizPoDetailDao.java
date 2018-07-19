/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import org.apache.ibatis.annotations.Param;

/**
 * 采购订单信息信息DAO接口
 * @author liuying
 * @version 2017-12-30
 */
@MyBatisDao
public interface BizPoDetailDao extends CrudDao<BizPoDetail> {
    
    /**
     * 获取已排产总量
     * @param objectId
     * @return
     */
    BizPoDetail getsumSchedulingNum(@Param("objectId") Integer objectId);
}
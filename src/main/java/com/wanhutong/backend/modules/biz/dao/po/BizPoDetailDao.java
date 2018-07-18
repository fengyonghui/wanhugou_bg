/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.po;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;

/**
 * 采购订单信息信息DAO接口
 * @author liuying
 * @version 2017-12-30
 */
@MyBatisDao
public interface BizPoDetailDao extends CrudDao<BizPoDetail> {
    /**
     * 将bizPoDetail中数据插入排产计划表
     * @param bizPoDetail
     */
    void insertFromBizPoDetail(BizPoDetail bizPoDetail);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.vend;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 供应商拓展表DAO接口
 * @author liuying
 * @version 2018-02-24
 */
@MyBatisDao
public interface BizVendInfoDao extends CrudDao<BizVendInfo> {
	int recover(BizVendInfo bizVendInfo);

    /**
     * 供应商审核状态修改
     * @param id 供应商ID
     * @param status 审核状态
     * @return 操作结果
     */
    int auditSupplier(@Param("id") int id, @Param("status")int status);
}
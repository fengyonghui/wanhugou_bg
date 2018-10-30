/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.roleapply;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.roleapply.BizRoleApply;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 零售申请表DAO接口
 * @author wangby
 * @version 2018-10-30
 */
@MyBatisDao
public interface BizRoleApplyDao extends CrudDao<BizRoleApply> {

    /**
     * 申请人的officeId 获取零售申请实体类
     * @param officeId
     * @return
     */
    List<BizRoleApply> getByOfficeId(@Param("officeId") Integer officeId);
}
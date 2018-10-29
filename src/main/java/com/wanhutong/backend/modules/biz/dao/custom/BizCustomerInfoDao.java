/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.custom;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomerInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 机构信息entityDAO接口
 * @author wangby
 * @version 2018-10-22
 */
@MyBatisDao
public interface BizCustomerInfoDao extends CrudDao<BizCustomerInfo> {

    BizCustomerInfo getByOfficeId(@Param("officeId") Integer officeId);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.message;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageOfficeType;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 站内信发送用户类型表DAO接口
 * @author wangby
 * @version 2018-11-22
 */
@MyBatisDao
public interface BizMessageOfficeTypeDao extends CrudDao<BizMessageOfficeType> {
    int insertBatch(@Param("officeTypeList")List<Integer> officeTypeList, @Param("messageId") Integer messageId, @Param("userId") Integer userId);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.message;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageUser;
import com.wanhutong.backend.modules.sys.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站内信关系DAO接口
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@MyBatisDao
public interface BizMessageUserDao extends CrudDao<BizMessageUser> {

    int insertBatch(@Param("userList")List<User> userList, @Param("messageId") Integer messageId, @Param("bizStatus") Integer bizStatus);


}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao.wx;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.wx.SysWxPersonalUser;

/**
 * 注册用户DAO接口
 * @author Oy
 * @version 2018-04-10
 */
@MyBatisDao
public interface SysWxPersonalUserDao extends CrudDao<SysWxPersonalUser> {
	public void recovery(SysWxPersonalUser personalUser);
}
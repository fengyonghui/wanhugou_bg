/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao.office;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.office.SysOfficeAddress;

import java.util.List;

/**
 * 地址管理(公司+详细地址)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-23
 */
@MyBatisDao
public interface SysOfficeAddressDao extends CrudDao<SysOfficeAddress> {
	public List<SysOfficeAddress> orderHeaderfindList(SysOfficeAddress sysOfficeAddress);

	List<SysOfficeAddress> findListByTypes(SysOfficeAddress sysOfficeAddress);
}
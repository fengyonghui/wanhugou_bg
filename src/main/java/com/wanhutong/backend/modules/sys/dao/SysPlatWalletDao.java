/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.dao;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.sys.entity.SysPlatWallet;

/**
 * 平台总钱包DAO接口
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@MyBatisDao
public interface SysPlatWalletDao extends CrudDao<SysPlatWallet> {
	
}
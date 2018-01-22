/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.entity.SysPlatWallet;
import com.wanhutong.backend.modules.sys.dao.SysPlatWalletDao;

/**
 * 平台总钱包Service
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Service
@Transactional(readOnly = true)
public class SysPlatWalletService extends CrudService<SysPlatWalletDao, SysPlatWallet> {

	public SysPlatWallet get(Integer id) {
		return super.get(id);
	}
	
	public List<SysPlatWallet> findList(SysPlatWallet sysPlatWallet) {
		return super.findList(sysPlatWallet);
	}
	
	public Page<SysPlatWallet> findPage(Page<SysPlatWallet> page, SysPlatWallet sysPlatWallet) {
		return super.findPage(page, sysPlatWallet);
	}
	
	@Transactional(readOnly = false)
	public void save(SysPlatWallet sysPlatWallet) {
		super.save(sysPlatWallet);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysPlatWallet sysPlatWallet) {
		super.delete(sysPlatWallet);
	}
	
}
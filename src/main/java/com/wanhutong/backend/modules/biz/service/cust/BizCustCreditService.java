/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.cust;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.cust.BizCustCredit;
import com.wanhutong.backend.modules.biz.dao.cust.BizCustCreditDao;

/**
 * 用户钱包Service
 * @author Ouyang
 * @version 2018-03-09
 */
@Service
@Transactional(readOnly = true)
public class BizCustCreditService extends CrudService<BizCustCreditDao, BizCustCredit> {

	public BizCustCredit get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCustCredit> findList(BizCustCredit bizCustCredit) {
		return super.findList(bizCustCredit);
	}
	
	public Page<BizCustCredit> findPage(Page<BizCustCredit> page, BizCustCredit bizCustCredit) {
		if(bizCustCredit.getCustomer()!=null){
			bizCustCredit.getCustomer().getMoblieMoeny().setMobile(bizCustCredit.getCustomer().getMoblieMoeny().getMobile());
		}
		return super.findPage(page, bizCustCredit);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCustCredit bizCustCredit) {
		super.save(bizCustCredit);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCustCredit bizCustCredit) {
		super.delete(bizCustCredit);
	}
	
}
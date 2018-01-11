/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.invoice;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceInfoDao;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceInfo;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 记录客户发票信息(发票开户行,税号)Service
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceInfoService extends CrudService<BizInvoiceInfoDao, BizInvoiceInfo> {

	@Autowired
	private CommonLocationService commonLocationService;
	
	public BizInvoiceInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInvoiceInfo> findList(BizInvoiceInfo bizInvoiceInfo) {
		return super.findList(bizInvoiceInfo);
	}
	
	public Page<BizInvoiceInfo> findPage(Page<BizInvoiceInfo> page, BizInvoiceInfo bizInvoiceInfo) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizInvoiceInfo);
		}else {
			bizInvoiceInfo.getSqlMap().put("invoiceInfo", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizInvoiceInfo);
		}

	}
	
	@Transactional(readOnly = false)
	public void save(BizInvoiceInfo bizInvoiceInfo) {
		if(bizInvoiceInfo.getBizLocation() !=null){
			commonLocationService.updateCommonLocation(bizInvoiceInfo.getBizLocation());
		}
		super.save(bizInvoiceInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizInvoiceInfo bizInvoiceInfo) {
		super.delete(bizInvoiceInfo);
	}
	
}
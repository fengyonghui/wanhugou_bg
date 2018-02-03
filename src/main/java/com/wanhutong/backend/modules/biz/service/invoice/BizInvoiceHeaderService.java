/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.invoice;

import java.util.HashSet;
import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.invoice.BizInvoiceHeader;
import com.wanhutong.backend.modules.biz.dao.invoice.BizInvoiceHeaderDao;

/**
 * 发票抬头，发票内容，发票类型Service
 * @author OuyangXiutian
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceHeaderService extends CrudService<BizInvoiceHeaderDao, BizInvoiceHeader> {

	@Autowired
	private BizInvoiceDetailService bizInvoiceDetailService;

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;

	public BizInvoiceHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizInvoiceHeader> findList(BizInvoiceHeader bizInvoiceHeader) {
		return super.findList(bizInvoiceHeader);
	}
	
	public Page<BizInvoiceHeader> findPage(Page<BizInvoiceHeader> page, BizInvoiceHeader bizInvoiceHeader) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizInvoiceHeader);
		}else {
			bizInvoiceHeader.getSqlMap().put("invoiceHeader", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizInvoiceHeader);
		}

	}
	
	@Transactional(readOnly = false)
	public void save(BizInvoiceHeader bizInvoiceHeader) {
		Double sum=0.0;
		BizInvoiceDetail bizInvoiceDetail = new BizInvoiceDetail();
		bizInvoiceDetail.setInvoiceHeader(bizInvoiceHeader);
		List<BizInvoiceDetail> list = bizInvoiceDetailService.findList(bizInvoiceDetail);
		for (BizInvoiceDetail biz : list) {
			BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(biz.getOrderHead().getId());
			Double totalExp = bizOrderHeader.getTotalExp();//订单总费用
			Double freight = bizOrderHeader.getFreight();//运费
			Double totalDetail = bizOrderHeader.getTotalDetail();//订单详情总价
			bizInvoiceDetail.setOrderHead(bizOrderHeader);
			sum += totalExp + freight + totalDetail;
		}
		bizInvoiceHeader.setInvTotal(sum);//发票数额
		super.save(bizInvoiceHeader);
		bizInvoiceDetail.setInvAmt(bizInvoiceHeader.getInvTotal());
		bizInvoiceDetail.setInvoiceHeader(bizInvoiceHeader);
		bizInvoiceDetailService.save(bizInvoiceDetail);

	}
	
	@Transactional(readOnly = false)
	public void delete(BizInvoiceHeader bizInvoiceHeader) {
		super.delete(bizInvoiceHeader);
	}
	
}
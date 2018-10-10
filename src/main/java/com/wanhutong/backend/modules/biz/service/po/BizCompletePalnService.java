/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import com.wanhutong.backend.common.supcan.treelist.cols.Col;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import com.wanhutong.backend.modules.biz.dao.po.BizCompletePalnDao;

/**
 * 确认排产表Service
 * @author 王冰洋
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class BizCompletePalnService extends CrudService<BizCompletePalnDao, BizCompletePaln> {

	@Autowired
	private BizCompletePalnDao bizCompletePalnDao;
	@Autowired
	private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizPoHeaderService bizPoHeaderService;

	public BizCompletePaln get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCompletePaln> findList(BizCompletePaln bizCompletePaln) {
		return super.findList(bizCompletePaln);
	}
	
	public Page<BizCompletePaln> findPage(Page<BizCompletePaln> page, BizCompletePaln bizCompletePaln) {
		return super.findPage(page, bizCompletePaln);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCompletePaln bizCompletePaln) {
		super.save(bizCompletePaln);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCompletePaln bizCompletePaln) {
		super.delete(bizCompletePaln);
	}

	/**
	 * 确认排产后更改排产状态
	 * @param bizCompletePaln
	 */
	@Transactional(readOnly = false)
	public void updateCompleteStatus(BizCompletePaln bizCompletePaln) {
		bizCompletePalnDao.updateCompleteStatus(bizCompletePaln);
	}

	/**
	 * 批量确认排产后更改排产状态
	 * @param paramList
	 */
	@Transactional(readOnly = false)
	public void batchUpdateCompleteStatus(List<String> paramList, Integer poHeaderId) {
		BizRequestHeader bizRequestHeader = new BizRequestHeader();
		bizRequestHeader.setBizPoHeader(new BizPoHeader(poHeaderId));
		List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
		BizOrderHeader orderHeader = new BizOrderHeader();
		orderHeader.setBizPoHeader(new BizPoHeader(poHeaderId));
		List<BizOrderHeader> orderHeaderList = bizOrderHeaderService.findList(orderHeader);
		if (CollectionUtils.isNotEmpty(requestHeaderList)) {
			bizPoHeaderService.sendSmsForDeliver("",requestHeaderList.get(0).getReqNo());
		}
		if (CollectionUtils.isNotEmpty(orderHeaderList)) {
			bizPoHeaderService.sendSmsForDeliver(orderHeaderList.get(0).getOrderNum(),"");
		}
		bizCompletePalnDao.batchUpdateCompleteStatus(paramList);
	}
}
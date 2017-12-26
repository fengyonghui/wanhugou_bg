/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderDao;

import javax.annotation.Resource;

/**
 * 备货清单Service
 * @author liuying
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class BizRequestHeaderService extends CrudService<BizRequestHeaderDao, BizRequestHeader> {
	@Resource
	private BizRequestDetailService bizRequestDetailService;

	public BizRequestHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestHeader> findList(BizRequestHeader bizRequestHeader) {
		return super.findList(bizRequestHeader);
	}
	
	public Page<BizRequestHeader> findPage(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
		return super.findPage(page, bizRequestHeader);
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestHeader bizRequestHeader) {
		String reqNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.RE,bizRequestHeader.getFromOffice().getId());
		bizRequestHeader.setReqNo(reqNo);
		List<BizRequestDetail> requestDetailList=bizRequestHeader.getRequestDetailList();

		super.save(bizRequestHeader);
		int i=0;
		boolean flag=false;
		for(BizRequestDetail requestDetail:requestDetailList){
			if(requestDetail.getId()!=null){
				flag=true;
			}
			if(flag){
				i=requestDetailList.size();
			}

				requestDetail.setLineNo(++i);


			requestDetail.setRequestHeader(bizRequestHeader);
			bizRequestDetailService.save(requestDetail);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestHeader bizRequestHeader) {
		super.delete(bizRequestHeader);
	}
	
}
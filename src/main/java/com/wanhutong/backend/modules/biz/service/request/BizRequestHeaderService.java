/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.xml.internal.bind.v2.TODO;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
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
	@Resource
	private DefaultPropService defaultPropService;
	@Resource
	private OfficeService officeService;


	public BizRequestHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestHeader> findList(BizRequestHeader bizRequestHeader) {
		return super.findList(bizRequestHeader);
	}
	
	public Page<BizRequestHeader> findPage(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
		Date today = bizRequestHeader.getRecvEta();
		if(today!=null){
			Format f = new SimpleDateFormat("yyyy-MM-dd");
			Calendar addCal = Calendar.getInstance();
			addCal.setTime(today);
			addCal.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
			Date tomorrow = addCal.getTime();
			bizRequestHeader.setEndDate("'"+f.format(tomorrow)+"'");
			Calendar subCal = Calendar.getInstance();
			subCal.setTime(today);
			subCal.add(Calendar.DAY_OF_MONTH, -1);// 今天+1天
			Date yesterday = subCal.getTime();
			bizRequestHeader.setStartDate("'"+f.format(yesterday)+"'");
		}
		User user = UserUtils.getUser();
		if (user.isAdmin()) {
			return super.findPage(page, bizRequestHeader);
		} else {
			bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so", "su"));
			return super.findPage(page, bizRequestHeader);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestHeader bizRequestHeader) {
		String reqNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.RE,bizRequestHeader.getFromOffice().getId());
		bizRequestHeader.setReqNo(reqNo);
		DefaultProp defaultProp=new DefaultProp();
		defaultProp.setPropKey("vend_center");
		List<DefaultProp> defaultPropList=defaultPropService.findList(defaultProp);
		if(defaultPropList!=null){
			DefaultProp prop=defaultPropList.get(0);
			Integer vendId=Integer.parseInt(prop.getPropValue());
			Office office=officeService.get(vendId);
			bizRequestHeader.setToOffice(office);
		}
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
			if(requestDetail.getId()==null){
				requestDetail.setLineNo(++i);
			}
			requestDetail.setRequestHeader(bizRequestHeader);
			bizRequestDetailService.save(requestDetail);
		}
	}

	@Transactional(readOnly = false)
	public void saveRequestHeader(BizRequestHeader bizRequestHeader) {
		super.save(bizRequestHeader);
	}

	@Transactional(readOnly = false)
	public void delete(BizRequestHeader bizRequestHeader) {
		super.delete(bizRequestHeader);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sun.xml.internal.bind.v2.TODO;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
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
	@Resource
	private BizPlatformInfoService bizPlatformInfoService;
	@Resource
	private BizPoHeaderService bizPoHeaderService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private BizPoDetailService bizPoDetailService;

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
			System.out.println("获取是:" + f.format(today));
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
		User user= UserUtils.getUser();
		DefaultProp defaultProp=new DefaultProp();
		defaultProp.setPropKey("vendCenter");
		Integer vendId=0;
		List<DefaultProp> defaultPropList=defaultPropService.findList(defaultProp);
		if(defaultPropList!=null){
			DefaultProp prop=defaultPropList.get(0);
			 vendId=Integer.parseInt(prop.getPropValue());
		}
		 	if(user.getCompany().getId().equals(vendId)){
				bizRequestHeader.setBizStatus(((Integer) ReqHeaderStatusEnum.APPROVE.ordinal()).byteValue());
			return  super.findPage(page, bizRequestHeader);
		}
		 	logger.info("用户机构----"+user.getCompany().getId());
			return super.findPage(page, bizRequestHeader);
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestHeader bizRequestHeader) {
		String reqNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.RE,bizRequestHeader.getFromOffice().getId());
		bizRequestHeader.setReqNo(reqNo);
		DefaultProp defaultProp=new DefaultProp();
		defaultProp.setPropKey("vendCenter");
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
		if(bizRequestHeader.getPoDetailList()!=null){
			Map<Integer,List<BizPoDetail>> map=new HashMap<Integer,List<BizPoDetail>>();
				for(BizPoDetail bizPoDetail:bizRequestHeader.getPoDetailList()){
					Integer key=bizPoDetail.getPoHeader().getVendOffice().getId();
					if(map.containsKey(key)){
						List<BizPoDetail> poDetails = map.get(key);
						map.remove(key);
						poDetails.add(bizPoDetail);
						map.put(key,poDetails);
					}
					else {
						List<BizPoDetail> poDetailList=new ArrayList<BizPoDetail>();
						poDetailList.add(bizPoDetail);
						map.put(key,poDetailList);
					}
				}
			BizPoHeader poHeader=new BizPoHeader();
			for (Map.Entry<Integer, List<BizPoDetail>> entry : map.entrySet()) {
				System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
				poHeader.setId(null);
				String poNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO,entry.getKey());
				poHeader.setOrderNum(poNo);
				poHeader.setPlateformInfo(bizPlatformInfoService.get(1));
				poHeader.setVendOffice(officeService.get(entry.getKey()));
				bizPoHeaderService.save(poHeader);
				int a=0;
				Double totalDetail=0.0;
					for (BizPoDetail poDetail:entry.getValue()){
						poDetail.setLineNo(++a);
						poDetail.setPoHeader(poHeader);
						BizSkuInfo bizSkuInfo=bizSkuInfoService.get(poDetail.getSkuInfo().getId());
						poDetail.setSkuName(bizSkuInfo.getName());
						poDetail.setPartNo(bizSkuInfo.getPartNo());
						bizPoDetailService.save(poDetail);
						totalDetail+=poDetail.getOrdQty()*poDetail.getUnitPrice();
					}

				poHeader.setTotalDetail(totalDetail);
				bizPoHeaderService.save(poHeader);
				}

			bizRequestHeader.setBizStatus(((Integer)ReqHeaderStatusEnum.STOCKING.ordinal()).byteValue());
			super.save(bizRequestHeader);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestHeader bizRequestHeader) {
		super.delete(bizRequestHeader);
	}
	
}
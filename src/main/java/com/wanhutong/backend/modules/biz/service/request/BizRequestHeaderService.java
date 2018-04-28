/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;

import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
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
	private BizSkuInfoService bizSkuInfoService;


	public BizRequestHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestHeader> findList(BizRequestHeader bizRequestHeader) {
		User user = UserUtils.getUser();
		boolean oflag = false;
		boolean flag=false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
					flag=true;
				}
			}
		}
		if (UserUtils.getOfficeList() != null){
			for (Office office:UserUtils.getOfficeList()){
				if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
					oflag = true;
				}
			}
		}
		if (user.isAdmin()) {
			return super.findList(bizRequestHeader);
		} else {
			if(oflag){

			}else {
				bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
			}
			return super.findList(bizRequestHeader);
		}
	}

	public Page<BizRequestHeader> findPageForSendGoods(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
		User user = UserUtils.getUser();
		boolean oflag = false;
		boolean flag=false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
					flag=true;
				}
			}
		}
		if (UserUtils.getOfficeList() != null){
			for (Office office:UserUtils.getOfficeList()){
				if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
					oflag = true;
				}
			}
		}
		if (user.isAdmin()) {
			return super.findPage(page,bizRequestHeader);
		} else {
			if(oflag){

			}else {
				bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
			}
			return super.findPage(page,bizRequestHeader);
		}
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
        Office office = officeService.get(user.getCompany().getId());
        if (user.isAdmin()) {
			bizRequestHeader.setDataStatus("filter");
			return super.findPage(page, bizRequestHeader);
		} else {
        	bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so", "su"));
			return super.findPage(page, bizRequestHeader);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestHeader bizRequestHeader) {

		DefaultProp defaultProp=new DefaultProp();
		defaultProp.setPropKey("vend_center");
		List<DefaultProp> defaultPropList=defaultPropService.findList(defaultProp);
		if(defaultPropList!=null){
			DefaultProp prop=defaultPropList.get(0);
			Integer vendId=Integer.parseInt(prop.getPropValue());
			Office office=officeService.get(vendId);
			bizRequestHeader.setToOffice(office);
		}
		User user=UserUtils.getUser();
		boolean flag=false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
					flag=true;
					break;
				}
			}
		}
		if(bizRequestHeader.getId()==null){
			BizRequestHeader requestHeader=new BizRequestHeader();
			requestHeader.setFromOffice(bizRequestHeader.getFromOffice());
			List<BizRequestHeader> requestHeaderList=findList(requestHeader);
			int s=0;
			if (requestHeaderList!=null && requestHeaderList.size()>0){
				s=requestHeaderList.size();
			}
			String reqNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.RE,bizRequestHeader.getFromOffice().getId(),bizRequestHeader.getToOffice().getId(),s+1);
			bizRequestHeader.setReqNo(reqNo);
		}
		super.save(bizRequestHeader);
		BizRequestDetail bizRequestDetail=new BizRequestDetail();
		if(bizRequestHeader.getSkuInfoIds()!=null && bizRequestHeader.getReqQtys()!=null){
			String [] skuInfoIdArr=StringUtils.split(bizRequestHeader.getSkuInfoIds(),",");
			String [] reqArr=StringUtils.split(bizRequestHeader.getReqQtys(),",");
			String [] lineNoArr=StringUtils.split(bizRequestHeader.getLineNos(),",");
			int t=0;
			int p=0;
			for(int i=0;i<skuInfoIdArr.length;i++){
				if(reqArr[i].equals("0")){
					continue;
				}
				bizRequestDetail.setSkuInfo(bizSkuInfoService.get(Integer.parseInt(skuInfoIdArr[i].trim())));
				bizRequestDetail.setReqQty(Integer.parseInt(reqArr[i].trim()));

				if(bizRequestHeader.getReqDetailIds()!=null){
					String [] detailIdArr=StringUtils.split(bizRequestHeader.getReqDetailIds(),",");
					if (detailIdArr.length > i){
						bizRequestDetail.setId(Integer.parseInt(detailIdArr[i].trim()));
						if(p<Integer.parseInt(lineNoArr[i])){
							t=Integer.parseInt(lineNoArr[i]);
						}else {
							t=p;
						}
					}else {
						bizRequestDetail.setId(null);
						bizRequestDetail.setLineNo(++t);
					}

				}
				if(bizRequestHeader.getReqDetailIds()==null) {
					bizRequestDetail.setLineNo(++t);
				}
				bizRequestDetail.setRequestHeader(bizRequestHeader);
				bizRequestDetailService.save(bizRequestDetail);
			}
		}
	}

	@Transactional(readOnly = false)
	public void saveInfo(BizRequestHeader bizRequestHeader) {
		super.save(bizRequestHeader);
	}
	@Transactional(readOnly = false)
	public void saveRequestHeader(BizRequestHeader bizRequestHeader) {
		super.save(bizRequestHeader);
	}

	@Transactional(readOnly = false)
	public void delete(BizRequestHeader bizRequestHeader) {
		super.delete(bizRequestHeader);
	}

	/**
	 * 用于备货清单导出
	 * */
	public List<BizRequestHeader> findListExport(BizRequestHeader bizRequestHeader) {
		List<BizRequestHeader> list = super.findList(bizRequestHeader);
		list.forEach(header->{
			BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
			bizRequestDetail1.setRequestHeader(header);
			BizSkuInfo bizSkuInfo=new BizSkuInfo();
			bizSkuInfo.setItemNo(bizRequestHeader.getItemNo());
			bizSkuInfo.setVendorName(bizRequestHeader.getName());
			bizRequestDetail1.setSkuInfo(bizSkuInfo);
			List<BizRequestDetail> detilDetailList = bizRequestDetailService.findList(bizRequestDetail1);
			Integer reqQtys = 0;
			Integer recvQtys = 0;
			Double money=0.0;
			for (BizRequestDetail requestDetail:detilDetailList) {
				money+=(requestDetail.getReqQty()==null?0:requestDetail.getReqQty())*requestDetail.getSkuInfo().getBuyPrice();
				reqQtys += requestDetail.getReqQty();
				recvQtys += requestDetail.getRecvQty();
			}
			header.setTotalMoney(money);
			header.setReqQtys(String.valueOf(reqQtys));
			header.setRecvQtys(String.valueOf(recvQtys));
		});
		return list;
	}

	/**
	 * 备货清单分页查询
	 * */
	public Page<BizRequestHeader> pageFindList(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
		User user = UserUtils.getUser();
		boolean oflag = false;
		boolean flag=false;
		if(user.getRoleList()!=null){
			for(Role role:user.getRoleList()){
				if(RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())){
					flag=true;
				}
			}
		}
		if (UserUtils.getOfficeList() != null){
			for (Office office:UserUtils.getOfficeList()){
				if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
					oflag = true;
				}
			}
		}
		if (user.isAdmin()) {
			return super.findPage(page,bizRequestHeader);
		} else {
			if(oflag){

			}else {
				bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
			}
			return super.findPage(page,bizRequestHeader);
		}
	}

}
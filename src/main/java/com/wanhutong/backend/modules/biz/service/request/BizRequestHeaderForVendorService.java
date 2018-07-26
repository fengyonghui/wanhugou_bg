/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import com.google.common.collect.ImmutableMap;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderForVendorDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.VendorRequestOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqFromTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.DefaultProp;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 备货清单Service
 * @author liuying
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class BizRequestHeaderForVendorService extends CrudService<BizRequestHeaderForVendorDao, BizRequestHeader> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizRequestHeaderForVendorService.class);
	/**
	 * 默认表名
	 */
	public static final String DATABASE_TABLE_NAME = "biz_request_header";
	@Resource
	private BizRequestDetailService bizRequestDetailService;
	@Resource
	private DefaultPropService defaultPropService;
	@Resource
	private OfficeService officeService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private CommonProcessService commonProcessService;
	@Resource
	private BizRequestHeaderForVendorDao bizRequestHeaderForVendorDao;
	@Resource
	private SystemService systemService;
	@Resource
	private BizOrderHeaderDao bizOrderHeaderDao;
	@Resource
    private BizPoPaymentOrderService bizPoPaymentOrderService;

	@Resource
	private BizOrderStatusService bizOrderStatusService;

	@Override
	public BizRequestHeader get(Integer id) {
		return super.get(id);
	}

	@Override
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

	@Override
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
			return super.findPage(page, bizRequestHeader);
		} else {
        	bizRequestHeader.setDataStatus("filter");
			bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so", "su"));
			return super.findPage(page, bizRequestHeader);
		}
	}

	@Override
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
		if(bizRequestHeader.getId()==null){
			int s=findContByFromOffice(bizRequestHeader.getFromOffice().getId());
			String reqNo= GenerateOrderUtils.getOrderNum(OrderTypeEnum.RE,bizRequestHeader.getFromOffice().getId(),bizRequestHeader.getToOffice().getId(),s+1);
			bizRequestHeader.setReqNo(reqNo);
		}
		Integer id = bizRequestHeader.getId();
		super.save(bizRequestHeader);
		if (id == null){
			bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
		}
		Integer processId = 0;
		if (ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
			processId = saveCommonProcess(bizRequestHeader);
		}
		if (ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
			processId = saveVendCommonProcess(bizRequestHeader);
		}

		this.updateProcessId(bizRequestHeader.getId(),processId);

		BizRequestDetail bizRequestDetail=new BizRequestDetail();
		if(bizRequestHeader.getSkuInfoIds()!=null && bizRequestHeader.getReqQtys()!=null){
			String [] skuInfoIdArr=StringUtils.split(bizRequestHeader.getSkuInfoIds(),",");
			String [] reqArr=StringUtils.split(bizRequestHeader.getReqQtys(),",");
			String [] lineNoArr=StringUtils.split(bizRequestHeader.getLineNos(),",");
			int t=0;
			int p=0;
			Double totalDetail=0.0;
			for(int i=0;i<skuInfoIdArr.length;i++){
				if(reqArr[i].equals("0")){
					continue;
				}
				BizSkuInfo bizSkuInfo=bizSkuInfoService.get(Integer.parseInt(skuInfoIdArr[i].trim()));
				bizRequestDetail.setSkuInfo(bizSkuInfo);
				bizRequestDetail.setReqQty(Integer.parseInt(reqArr[i]
						.trim()));

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
					bizRequestDetail.setId(null);
					bizRequestDetail.setLineNo(++t);
				}
				bizRequestDetail.setRequestHeader(bizRequestHeader);
				bizRequestDetailService.save(bizRequestDetail);
				BizRequestDetail requestDetail=bizRequestDetailService.get(bizRequestDetail);
				totalDetail+=(requestDetail.getUnitPrice()==null?bizSkuInfo.getBuyPrice():requestDetail.getUnitPrice())*requestDetail.getReqQty();

			}
			bizRequestHeader.setTotalDetail(totalDetail);
			super.save(bizRequestHeader);
		}
	}


	public Integer  saveCommonProcess(BizRequestHeader bizRequestHeader){

		RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();
		RequestOrderProcessConfig.RequestOrderProcess purchaseOrderProcess = requestOrderProcessConfig.processMap.get(requestOrderProcessConfig.getDefaultProcessId());
		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		commonProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
		commonProcessService.save(commonProcessEntity);

		/*String desc = purchaseOrderProcess.getName();
		Integer bizStatus = ReqHeaderStatusEnum.getEnum(desc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(bizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/
		StringBuilder phone = new StringBuilder();
		User user=UserUtils.getUser();
		User sendUser=new User(systemService.getRoleByEnname(purchaseOrderProcess.getRoleEnNameEnum()==null?"":purchaseOrderProcess.getRoleEnNameEnum().toLowerCase()));
		sendUser.setCent(user.getCompany());
			List<User> userList = systemService.findUser(sendUser);
			if (CollectionUtils.isNotEmpty(userList)) {
				for (User u : userList) {
					phone.append(u.getMobile()).append(",");
				}
			}

		if (StringUtils.isNotBlank(phone.toString())) {
			AliyunSmsClient.getInstance().sendSMS(
					SmsTemplateCode.PENDING_AUDIT_1.getCode(),
					phone.toString(),
					ImmutableMap.of("order","备货清单", "orderNum", bizRequestHeader.getReqNo()));
		}


		return commonProcessEntity.getId();
	}

	public Integer  saveVendCommonProcess(BizRequestHeader bizRequestHeader){

		VendorRequestOrderProcessConfig vendorRequestOrderProcessConfig = ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get();
		VendorRequestOrderProcessConfig.RequestOrderProcess purchaseOrderProcess = vendorRequestOrderProcessConfig.processMap.get(vendorRequestOrderProcessConfig.getDefaultProcessId());
		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		commonProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
		commonProcessService.save(commonProcessEntity);

		/*String desc = purchaseOrderProcess.getName();
		Integer bizStatus = ReqHeaderStatusEnum.getEnum(desc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(bizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/
		StringBuilder phone = new StringBuilder();
		User user=UserUtils.getUser();
		if (CollectionUtils.isNotEmpty(purchaseOrderProcess.getRoleEnNameEnum())) {
			for (String roleEnNameEnum:purchaseOrderProcess.getRoleEnNameEnum()) {
				User sendUser=new User(systemService.getRoleByEnname(roleEnNameEnum.toLowerCase()));
				sendUser.setCent(user.getCompany());
				List<User> userList = systemService.findUser(sendUser);
				if (CollectionUtils.isNotEmpty(userList)) {
					for (User u : userList) {
						phone.append(u.getMobile()).append(",");
					}
				}
			}
		}

		if (StringUtils.isNotBlank(phone.toString())) {
			AliyunSmsClient.getInstance().sendSMS(
					SmsTemplateCode.PENDING_AUDIT_1.getCode(),
					phone.toString(),
					ImmutableMap.of("order","备货清单", "orderNum", bizRequestHeader.getReqNo()));
		}


		return commonProcessEntity.getId();
	}
	@Transactional(readOnly = false)
	public void saveInfo(BizRequestHeader bizRequestHeader) {
		super.save(bizRequestHeader);
	}
	@Transactional(readOnly = false)
	public void saveRequestHeader(BizRequestHeader bizRequestHeader) {
		super.save(bizRequestHeader);
	}

	@Override
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
				Double buyPrice =0.0;
				if(requestDetail.getSkuInfo()!=null && requestDetail.getSkuInfo().getBuyPrice()!=null){
					buyPrice=requestDetail.getSkuInfo().getBuyPrice();
				}
				money+=(requestDetail.getReqQty()==null?0:requestDetail.getReqQty())*buyPrice;
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


	/**
	 * 备货清单分页查询
	 * */
	public Page<BizRequestHeader> pageFindListV2(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
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
			bizRequestHeader.setPage(page);
			page.setList(bizRequestHeaderForVendorDao.findListForPoHeader(bizRequestHeader));
			return page;
		} else {
			if(oflag){

			}else {
				bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
			}
			bizRequestHeader.setPage(page);
			page.setList(bizRequestHeaderForVendorDao.findListForPoHeader(bizRequestHeader));
			return page;
		}
	}


	/**
	 *属于采购中心备货
	 * @param reqHeaderId
	 * @param currentType
	 * @param auditType
	 * @param description
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String audit(Integer reqHeaderId, String currentType, int auditType, String description) {
		BizRequestHeader bizRequestHeader = this.get(reqHeaderId);
		CommonProcessEntity cureentProcessEntity  = bizRequestHeader.getCommonProcess();

		if (cureentProcessEntity == null) {
			return "操作失败,当前订单无审核状态!";
		}
		cureentProcessEntity = commonProcessService.get(bizRequestHeader.getCommonProcess().getId());
		if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
			logger.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", reqHeaderId, currentType);
			return "操作失败,当前审核状态异常!";
		}
		RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();

		// 当前流程
		RequestOrderProcessConfig.RequestOrderProcess currentProcess = requestOrderProcessConfig.processMap.get(Integer.valueOf(currentType));
		// 下一流程
		RequestOrderProcessConfig.RequestOrderProcess nextProcess = requestOrderProcessConfig.processMap.get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
		if (nextProcess == null) {
			return "操作失败,当前流程已经结束!";
		}
		User user = UserUtils.getUser();
		RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(currentProcess.getRoleEnNameEnum());
		Role role = new Role();
		role.setEnname(roleEnNameEnum.getState());
		if (!user.isAdmin() && !user.getRoleList().contains(role)) {
			return "操作失败,该用户没有权限!";
		}

		if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
			return "请输入驳回理由!";
		}

		cureentProcessEntity.setBizStatus(auditType);
		cureentProcessEntity.setProcessor(user.getId().toString());
		cureentProcessEntity.setDescription(description);
		commonProcessService.save(cureentProcessEntity);

		/*String currentDesc = currentProcess.getName();
		Integer currentBizStatus = ReqHeaderStatusEnum.getEnum(currentDesc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(currentBizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/


		CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
		nextProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		nextProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());

		if(nextProcessEntity.getType().equals(requestOrderProcessConfig.getAutProcessId().toString())){
			Integer bizStatus = bizRequestHeader.getBizStatus();
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.APPROVE.getState());
			saveRequestHeader(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
		}
		commonProcessService.save(nextProcessEntity);

		/*String nextDesc = currentProcess.getName();
		Integer nextBizStatus = ReqHeaderStatusEnum.getEnum(currentDesc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(nextBizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/

		this.updateProcessId(reqHeaderId, nextProcessEntity.getId());

		StringBuilder phone = new StringBuilder();

		User sendUser=new User(systemService.getRoleByEnname(nextProcess.getRoleEnNameEnum()==null?"":nextProcess.getRoleEnNameEnum().toLowerCase()));
		//	sendUser.setCent(UserUtils.getUser().getCompany());
		List<User> userList = systemService.findUser(sendUser);
		if (CollectionUtils.isNotEmpty(userList)) {
			for (User u : userList) {
				phone.append(u.getMobile()).append(",");
			}
		}
		if (StringUtils.isNotBlank(phone.toString())) {
			AliyunSmsClient.getInstance().sendSMS(
					SmsTemplateCode.PENDING_AUDIT_1.getCode(),
					phone.toString(),
					ImmutableMap.of("order","备货清单", "orderNum", bizRequestHeader.getReqNo()));
		}


		return "ok";
	}

	/**
	 *属于供应商备货
	 * @param reqHeaderId
	 * @param currentType
	 * @param auditType
	 * @param description
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public String vendAudit(Integer reqHeaderId, String currentType, int auditType, String description) {
		BizRequestHeader bizRequestHeader = this.get(reqHeaderId);
		CommonProcessEntity cureentProcessEntity  = bizRequestHeader.getCommonProcess();

		if (cureentProcessEntity == null) {
			return "操作失败,当前订单无审核状态!";
		}
		cureentProcessEntity = commonProcessService.get(bizRequestHeader.getCommonProcess().getId());
		if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
			logger.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", reqHeaderId, currentType);
			return "操作失败,当前审核状态异常!";
		}
		VendorRequestOrderProcessConfig vendorRequestOrderProcessConfig = ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get();
		// 当前流程
		VendorRequestOrderProcessConfig.RequestOrderProcess vendCurrentProcess = vendorRequestOrderProcessConfig.processMap.get(Integer.valueOf(currentType));
		// 下一流程
		VendorRequestOrderProcessConfig.RequestOrderProcess vendNextProcess = vendorRequestOrderProcessConfig.processMap.get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? vendCurrentProcess.getPassCode() : vendCurrentProcess.getRejectCode());
		if (vendNextProcess == null) {
			return "操作失败,当前流程已经结束!";
		}
		User user = UserUtils.getUser();
		boolean hasRole = false;
		for (String s:vendCurrentProcess.getRoleEnNameEnum()) {
			RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
			Role role = new Role();
			role.setEnname(roleEnNameEnum.getState());
			if (user.getRoleList().contains(role)) {
				hasRole = true;
				break;
			}
			if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
				return "请输入驳回理由!";
			}
		}
		if (!user.isAdmin() && !hasRole) {
			return "操作失败,该用户没有权限!";
		}
		cureentProcessEntity.setBizStatus(auditType);
		cureentProcessEntity.setProcessor(user.getId().toString());
		cureentProcessEntity.setDescription(description);
		commonProcessService.save(cureentProcessEntity);

		CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
		nextProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		nextProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		nextProcessEntity.setType(String.valueOf(vendNextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());

		if(nextProcessEntity.getType().equals(vendorRequestOrderProcessConfig.getAutProcessId().toString())){
			Integer bizStatus = bizRequestHeader.getBizStatus();
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.APPROVE.getState());
			saveRequestHeader(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
		}
		commonProcessService.save(nextProcessEntity);

		/*String nextDesc = vendCurrentProcess.getName();
		Integer nextBizStatus = ReqHeaderStatusEnum.getEnum(currentDesc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(nextBizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/

		this.updateProcessId(reqHeaderId, nextProcessEntity.getId());

		StringBuilder phone = new StringBuilder();

		for (String s : vendNextProcess.getRoleEnNameEnum()) {
			if (!"".equals(s)) {
				List<User> userList = systemService.findUser(new User(systemService.getRoleByEnname(s.toLowerCase())));
				if (CollectionUtils.isNotEmpty(userList)) {
					for (User u : userList) {
						phone.append(u.getMobile()).append(",");
					}
				}
			}
		}
		if (StringUtils.isNotBlank(phone.toString())) {
			AliyunSmsClient.getInstance().sendSMS(
					SmsTemplateCode.PENDING_AUDIT_1.getCode(),
					phone.toString(),
					ImmutableMap.of("order","备货清单", "orderNum", bizRequestHeader.getReqNo()));
		}


		return "ok";
	}
	/**
	 * 更新流程ID
	 * @param headerId
	 * @param processId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateProcessId(Integer headerId, Integer processId) {
		return dao.updateProcessId(headerId, processId);
	}

	public  int findContByFromOffice(Integer fromOfficeId ){
		return  bizRequestHeaderForVendorDao.findContByFromOffice(fromOfficeId);
	}

	/**
	 * 备货单收货、备货单发货 导出
	 * */
	public List<BizRequestHeader> findListAllExport(BizRequestHeader bizRequestHeader) {
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

	/**
	 * 备货单商品的销售单数量
	 * @param centId
	 * @param skuId
	 * @return
	 */
	public Integer findSellCount(Integer centId, Integer skuId) {
		return bizRequestHeaderForVendorDao.findSellCount(centId, skuId);
	}

	/**
	 * 备货单商品的销售单
	 * @param skuIdList
	 * @param centId
	 * @return
	 */
	public List<BizOrderHeader> findOrderForVendReq(List<Integer> skuIdList, Integer centId) {
		return bizOrderHeaderDao.findOrderForVendReq(skuIdList, centId);
	}

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int incrPayTotal(int id, BigDecimal payTotal) {
        return dao.incrPayTotal(id, payTotal);
    }

    /**
     * 生成支付申请单
     *
     * @param bizRequestHeader
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> genPaymentOrder(BizRequestHeader bizRequestHeader) {
        if (bizRequestHeader.getBizPoPaymentOrder() != null && bizRequestHeader.getBizPoPaymentOrder().getId() != null && bizRequestHeader.getBizPoPaymentOrder().getId() != 0) {
            return Pair.of(Boolean.FALSE, "操作失败,该订单已经有正在申请的支付单!");
        }
        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
        if (paymentOrderProcessConfig.getDefaultBaseMoney().compareTo(bizRequestHeader.getPlanPay()) > 0) {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getPayProcessId());
        } else {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
        }

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizRequestHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoPaymentOrderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setPoHeaderId(bizRequestHeader.getId());
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
        bizPoPaymentOrder.setTotal(bizRequestHeader.getPlanPay());
        bizPoPaymentOrder.setDeadline(bizRequestHeader.getPayDeadline());
        bizPoPaymentOrder.setProcessId(commonProcessEntity.getId());
        bizPoPaymentOrder.setType(PoPayMentOrderTypeEnum.REQ_TYPE.getType());
        bizPoPaymentOrderService.save(bizPoPaymentOrder);

        bizRequestHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
        this.updatePaymentOrderId(bizRequestHeader.getId(), bizPoPaymentOrder.getId());
        return Pair.of(Boolean.TRUE, "操作成功!");
    }

    /**
     * 开始审核流程
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> startAudit(int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, int auditType, String desc) {
        VendorRequestOrderProcessConfig vendorRequestOrderProcessConfig = ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get();
        BizRequestHeader bizRequestHeader = this.get(id);
        if (bizRequestHeader == null) {
            LOGGER.error("start audit error [{}]", id);
            return Pair.of(Boolean.FALSE,   "操作失败!参数错误[id]");
        }

        if (prew) {
            bizRequestHeader.setPayDeadline(prewPayDeadline);
            bizRequestHeader.setPlanPay(prewPayTotal);
            this.genPaymentOrder(bizRequestHeader);
        }
        Byte bizStatus = bizRequestHeader.getBizStatus().byteValue();
        this.updateBizStatus(bizRequestHeader.getId(), ReqHeaderStatusEnum.PROCESS.getState());
        if (!bizStatus.equals(bizRequestHeader.getBizStatus().byteValue())) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
        }
        this.updateProcessToInit(bizRequestHeader);
        auditRe(id, String.valueOf(vendorRequestOrderProcessConfig.getDefaultProcessId()), auditType, desc);
        return Pair.of(Boolean.TRUE,   "操作成功!");
    }

    /**
     * 审批RE
     *
     * @param id
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> auditRe(int id, String currentType, int auditType, String description) {
//        BizRequestHeader bizRequestHeader = this.get(id);
//        CommonProcessEntity cureentProcessEntity = bizRequestHeader.getCommonProcess();
		String s = vendAudit(id, currentType, auditType, description);
		if ("ok".equals(s)) {
			return Pair.of(Boolean.TRUE, "操作成功!");
		} else if ("操作失败,该用户没有权限!".equals(s)){
			return Pair.of(Boolean.FALSE, "操作失败,该用户没有权限!");
		} else if ("请输入驳回理由!".equals(s)) {
			return Pair.of(Boolean.FALSE, "请输入驳回理由!");
		} else if ("操作失败,当前审核状态异常!".equals(s)){
			return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常!");
		} else {
			return Pair.of(Boolean.FALSE, "操作失败,当前流程已经结束!");
		}
    }

    private void updateProcessToInit(BizRequestHeader bizRequestHeader) {
        VendorRequestOrderProcessConfig vendorRequestOrderProcessConfig = ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get();
		VendorRequestOrderProcessConfig.RequestOrderProcess currentProcess = vendorRequestOrderProcessConfig.processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
		VendorRequestOrderProcessConfig.RequestOrderProcess requestOrderProcess = vendorRequestOrderProcessConfig.processMap.get(currentProcess.getPassCode());

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizRequestHeader.getId().toString());
        commonProcessEntity.setObjectName(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc());
        commonProcessEntity.setType(String.valueOf(requestOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        this.updateProcessId(bizRequestHeader.getId(), commonProcessEntity.getId());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updatePaymentOrderId(Integer id, Integer paymentId) {
        return dao.updatePaymentOrderId(id, paymentId);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateBizStatus(Integer id, Integer status) {
        return dao.updateBizStatus(id, status);
    }

}
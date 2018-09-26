/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestExpandDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderForVendorDao;
import com.wanhutong.backend.modules.biz.entity.logistic.AddressVoEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestExpand;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
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
import org.apache.ibatis.annotations.Param;
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
import java.util.Map;

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
	private BizRequestExpandDao bizRequestExpandDao;
	@Resource
	private BizRequestExpandService bizRequestExpandService;

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
		List<String> roleList = Lists.newArrayList();
		for (Role role : user.getRoleList()) {
			roleList.add(role.getEnname());
		}
		if (roleList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
			flag = true;
		}
		if (UserUtils.getOfficeList() != null){
			for (Office office:UserUtils.getOfficeList()){
				if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
					oflag = true;
				}
			}
		}
		if (!user.isAdmin() && !oflag && !flag) {
            bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
		} else if (!user.isAdmin() && flag) {
			bizRequestHeader.setVendorId(user.getCompany().getId());
			bizRequestHeader.getSqlMap().put("request",BaseService.dataScopeFilter(user,"sv","su"));
		}
		return super.findList(bizRequestHeader);
	}

	public Page<BizRequestHeader> findPageForSendGoods(Page<BizRequestHeader> page, BizRequestHeader bizRequestHeader) {
		User user = UserUtils.getUser();
		boolean oflag = false;
		boolean flag=false;
		List<Role> roleList = user.getRoleList();
		List<String> enNameList = Lists.newArrayList();
		for (Role role : roleList) {
			enNameList.add(role.getEnname());
		}
		if (UserUtils.getOfficeList() != null){
			for (Office office:UserUtils.getOfficeList()){
				if (OfficeTypeEnum.SUPPLYCENTER.getType().equals(office.getType())){
					oflag = true;
				}
			}
		}
		if (!user.isAdmin() && !oflag && !enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
			bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so","su"));
		} else if (!user.isAdmin() && enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
			bizRequestHeader.setVendorId(user.getCompany().getId());
			bizRequestHeader.getSqlMap().put("request",BaseService.dataScopeFilter(user,"sv","su"));
		}
		return super.findPage(page,bizRequestHeader);
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
		boolean sflag = false;
		for (Role role : user.getRoleList()) {
			if (RoleEnNameEnum.SUPPLY_CHAIN.getState().equals(role.getEnname())) {
				sflag = true;
			}
		}
		if (!user.isAdmin()) {
			bizRequestHeader.setDataStatus("filter");
			if (sflag) {
				bizRequestHeader.setVendorId(user.getCompany().getId());
				bizRequestHeader.getSqlMap().put("request",BaseService.dataScopeFilter(user,"sv","su"));
			} else {
				bizRequestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "so", "su"));
			}
		}
		return super.findPage(page, bizRequestHeader);
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
//		Integer processId = 0;
		saveCommonProcess(bizRequestHeader);
//		this.updateProcessId(bizRequestHeader.getId(),processId);

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
			Integer reqExpandId = bizRequestExpandDao.getIdByRequestHeaderId(bizRequestHeader.getId());
			if (reqExpandId == null) {
				BizRequestExpand bizRequestExpand = new BizRequestExpand();
				bizRequestExpand.setRequestHeader(bizRequestHeader);
				bizRequestExpand.setBizVendInfo(bizRequestHeader.getBizVendInfo());
				bizRequestExpandService.save(bizRequestExpand);
			} else {
				BizRequestExpand bizRequestExpand = bizRequestExpandService.get(reqExpandId);
				bizRequestExpand.setRequestHeader(bizRequestHeader);
				bizRequestExpand.setBizVendInfo(bizRequestHeader.getBizVendInfo());
				bizRequestExpandService.save(bizRequestExpand);
			}
		}
	}


	private void saveCommonProcess(BizRequestHeader bizRequestHeader){
		CommonProcessEntity commonProcess = new CommonProcessEntity();
		commonProcess.setObjectId(bizRequestHeader.getId().toString());
		commonProcess.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		List<CommonProcessEntity> processList = commonProcessService.findList(commonProcess);
		if (CollectionUtils.isNotEmpty(processList)) {
			commonProcessService.updateCurrentByObject(bizRequestHeader.getId(), BizRequestHeaderForVendorService.DATABASE_TABLE_NAME, CommonProcessEntity.NOT_CURRENT);
		}

		RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();
		RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess = requestOrderProcessConfig.processMap.get(requestOrderProcessConfig.getDefaultProcessId());
		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		commonProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		commonProcessEntity.setType(String.valueOf(requestOrderProcess.getCode()));
		commonProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
		commonProcessService.save(commonProcessEntity);

		StringBuilder phone = new StringBuilder();
		User user=UserUtils.getUser();
		User sendUser=new User(systemService.getRoleByEnname(requestOrderProcess.getRoleEnNameEnum()==null?"":requestOrderProcess.getRoleEnNameEnum().toLowerCase()));
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
		cureentProcessEntity.setCurrent(CommonProcessEntity.NOT_CURRENT);
		commonProcessService.save(cureentProcessEntity);

		/*String currentDesc = currentProcess.getName();
		Integer currentBizStatus = ReqHeaderStatusEnum.getEnum(currentDesc).getState();
		bizOrderStatusService.insertAfterBizStatusChangedNew(currentBizStatus, BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());*/


		CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
		nextProcessEntity.setObjectId(bizRequestHeader.getId().toString());
		nextProcessEntity.setObjectName(BizRequestHeaderForVendorService.DATABASE_TABLE_NAME);
		nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());
		nextProcessEntity.setCurrent(CommonProcessEntity.CURRENT);


		if (cureentProcessEntity.getType().equals(requestOrderProcessConfig.getDefaultProcessId().toString())) {
			Integer bizStatus =  bizRequestHeader.getBizStatus();
			this.updateBizStatus(reqHeaderId,ReqHeaderStatusEnum.IN_REVIEW.getState());
			if (bizStatus == null || !bizStatus.equals(ReqHeaderStatusEnum.IN_REVIEW.getState())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
		}

		if(nextProcessEntity.getType().equals(requestOrderProcessConfig.getAutProcessId().toString())){
			Integer bizStatus = bizRequestHeader.getBizStatus();
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.APPROVE.getState());
			saveRequestHeader(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(ReqHeaderStatusEnum.APPROVE.getState())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
			saveRequestHeader(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
		}
		commonProcessService.save(nextProcessEntity);

//		this.updateProcessId(reqHeaderId, nextProcessEntity.getId());

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
		if (skuIdList != null && skuIdList.size() == 0) {
			skuIdList = null;
		}
		return bizOrderHeaderDao.findOrderForVendReq(skuIdList, centId);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int incrPayTotal(int id, BigDecimal payTotal) {
		return bizRequestExpandDao.incrPayTotal(id, payTotal);
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
		bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.REQ_TYPE.getType());
		bizPoPaymentOrderService.save(bizPoPaymentOrder);

		bizRequestHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
		this.updatePaymentOrderId(bizRequestExpandDao.getIdByRequestHeaderId(bizRequestHeader.getId()), bizPoPaymentOrder.getId());
		return Pair.of(Boolean.TRUE, "操作成功!");
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updatePaymentOrderId(Integer id, Integer paymentId) {
		return bizRequestExpandDao.updatePaymentOrderId(id, paymentId);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int updateBizStatus(Integer id, Integer status) {
		return dao.updateBizStatus(id, status,UserUtils.getUser(),new Date());
	}

	/**
	 * 该备货单下所有商品的总采购数量，总排产数量（分为按订单排产的总排产量和按商品排产的总排产量）
	 * @param id
	 * @return
	 */
	public BizRequestHeader getTotalQtyAndSchedulingNum(Integer id){
		return bizRequestHeaderForVendorDao.getTotalQtyAndSchedulingNum(id);
	}

	/**
	 * 供应商确认排产后，更新排产表中排产状态
	 * @param requestHeader
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateSchedulingType(BizRequestHeader requestHeader) {
		bizRequestHeaderForVendorDao.updateSchedulingType(requestHeader);
	}

	public List<AddressVoEntity> findOfficeRegion(Integer officeId) {
		return bizRequestHeaderForVendorDao.findOfficeRegion(officeId);
	}

	public void findOrderLogistics(AddressVoEntity addressVoEntity) {
		Map<String, Object> params = Maps.newHashMap();
		//设置起始站点
		params.put("transitStartPointCode", null);
		if (StringUtils.isNotBlank(addressVoEntity.getRegionCode())) {
			params.put("transitStopPointCode", addressVoEntity.getRegionCode());
			params.put("level", 3);
		} else if (StringUtils.isNotBlank(addressVoEntity.getCityCode())) {
			params.put("transitStopPointCode", addressVoEntity.getCityCode());
			params.put("level", 2);
		} else {
			//return ResultVo.createByErrorEnum("找不到相应的采购中心地址信息");
		}
		//如果收货地址为广东 则把起始站点设置为2
		if (StringUtils.isNotBlank(addressVoEntity.getProvCode()) && addressVoEntity.getProvCode().equals(1)) {
			params.put("transitStartPointCode", 2);
		}
		//设置网点
		params.put("branchesCode", 1);
		//设置线路类别（1=发货线路，2=到货线路） 默认发货路线
		params.put("category", 1);
		params.put("varietyInfoIdSParams", addressVoEntity.getVarietyInfoIdSParams());


	}

}
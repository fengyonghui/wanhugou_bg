/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.BizHeaderSchedulingDto;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.category.BizVarietyInfoService;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventorySkuService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.po.BizCompletePalnService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;
import com.wanhutong.backend.modules.biz.service.po.BizSchedulingPlanService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.RequestOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.VendorRequestOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqFromTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 备货清单Controller
 * @author liuying
 * @version 2017-12-23
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestHeaderForVendor")
public class BizRequestHeaderForVendorController extends BaseController {

	@Autowired
	private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
	@Autowired
	private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizSkuInfoV2Service bizSkuInfoService;
	@Autowired
	private BizPoOrderReqService bizPoOrderReqService;
	@Autowired
	private DictService dictService;
	@Autowired
	private BizVarietyInfoService bizVarietyInfoService;
	@Autowired
	private BizInventorySkuService bizInventorySkuService;
	@Autowired
	private BizOrderStatusService bizOrderStatusService;
	@Autowired
	private CommonImgService commonImgService;
	@Autowired
	private BizVendInfoService bizVendInfoService;
	@Autowired
	private BizSchedulingPlanService bizSchedulingPlanService;
	@Autowired
	private BizCompletePalnService bizCompletePalnService;
	@Autowired
	private BizPoHeaderService bizPoHeaderService;
	@Autowired
	private BizPoPaymentOrderService bizPoPaymentOrderService;
	@Autowired
	private OfficeService officeService;

	public static final String REQUEST_HEADER_TABLE_NAME = "biz_request_header";
	public static final String REQUEST_DETAIL_TABLE_NAME = "biz_request_detail";
	public static final Integer SCHEDULING_FOR_HEADER = 0;
	public static final Integer SCHEDULING_FOR_DETAIL = 1;
	public static final String MARKETING_MANAGER = "marketing_manager";

	@ModelAttribute
	public BizRequestHeader get(@RequestParam(required=false) Integer id) {
		if (id == null){
		    return new BizRequestHeader();
        }
        BizRequestHeader entity = bizRequestHeaderForVendorService.get(id);
        if (entity.getCommonProcess() != null && entity.getCommonProcess().getId() != null) {
            List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
            bizPoHeaderService.getCommonProcessListFromDB(entity.getCommonProcess().getId(), commonProcessList);
            Collections.reverse(commonProcessList);
            entity.setCommonProcessList(commonProcessList);
        }
        BizPoHeader bizPoHeader = new BizPoHeader();
        bizPoHeader.setBizRequestHeader(entity);
        List<BizPoHeader> bizPoHeaderList = bizPoHeaderService.findList(bizPoHeader);
        if (CollectionUtils.isNotEmpty(bizPoHeaderList)) {
            BizPoHeader poHeader = bizPoHeaderList.get(0);
            if (poHeader.getCommonProcess() != null && poHeader.getCommonProcess().getId() != null) {
                List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
                bizPoHeaderService.getCommonProcessListFromDB(poHeader.getCommonProcess().getId(), commonProcessList);
                Collections.reverse(commonProcessList);
                poHeader.setCommonProcessList(commonProcessList);
                entity.setBizPoHeader(poHeader);
            }
        }

        BizRequestDetail bizRequestDetail = new BizRequestDetail();
        bizRequestDetail.setRequestHeader(entity);
        List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
        List<BizRequestDetail> requestDetails = Lists.newArrayList();
        for (BizRequestDetail requestDetail : requestDetailList) {

            BizSchedulingPlan bizSchedulingPlan = new BizSchedulingPlan();
            bizSchedulingPlan.setBizRequestDetail(requestDetail);
//				List<BizSchedulingPlan> schedulingPlanList = bizSchedulingPlanService.findAllList(bizSchedulingPlan);
//				//requestDetail.setSchedulingPlanList(schedulingPlanList);

            BizRequestDetail requestDetailTemp = bizRequestDetailService.getsumSchedulingNum(requestDetail.getId());
            if (requestDetailTemp != null) {
                requestDetail.setSumSchedulingNum(requestDetailTemp.getSumSchedulingNum());
                requestDetail.setSumCompleteNum(requestDetailTemp.getSumCompleteNum());
                requestDetail.setSumCompleteDetailNum(requestDetailTemp.getSumCompleteDetailNum());
            }
            requestDetails.add(requestDetail);
        }
        entity.setRequestDetailList(requestDetails);
		return entity;
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = {"list4Mobile"})
	@ResponseBody
	public String list4Mobile(BizRequestHeader bizRequestHeader,
							  @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
							  HttpServletRequest request, HttpServletResponse response) {
		String dataFrom = "biz_request_bizRequestHeader";
		bizRequestHeader.setDataFrom(dataFrom);

		Page<BizRequestHeader> bizPoHeaderPage = new Page<>(request, response);
		bizPoHeaderPage.setPageNo(pageNo);
		Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(bizPoHeaderPage, bizRequestHeader);
		Map<String, Object> resultMap = Maps.newHashMap();
		resultMap.put("page", page);
		//品类名称
		List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());
		resultMap.put("varietyInfoList", varietyInfoList);
		resultMap.put("auditStatus", ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());
		resultMap.put("vendAuditStatus",ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());

		return JsonUtil.generateData(resultMap, null);
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = {"list", ""})
	public String list(BizRequestHeader bizRequestHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		List<String> enNameList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(user.getRoleList())) {
            for (Role role : user.getRoleList()) {
                enNameList.add(role.getEnname());
            }
        }

		Map<Integer, com.wanhutong.backend.modules.config.parse.Process> purMap = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap();
		Integer currentCode = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getDefaultNewProcessId();
		Integer lastCode = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId();
		Map<String,Integer> poMap = new LinkedHashMap<>();
		Set<String> processSet = new HashSet<>();
		while (true) {
			com.wanhutong.backend.modules.config.parse.Process current = purMap.get(currentCode);
			com.wanhutong.backend.modules.config.parse.Process next = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPassProcess(current);
			poMap.put(current.getName(),currentCode);
			processSet.add(current.getName());
			if (lastCode.equals(currentCode)) {
				break;
			}
			currentCode = next.getCode();
		}
		Map<Integer, RequestOrderProcessConfig.RequestOrderProcess> reqMap = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap;
		Map<String,Integer> requestMap = new LinkedHashMap<>();


		for (Map.Entry<Integer,RequestOrderProcessConfig.RequestOrderProcess> map : reqMap.entrySet()) {
			requestMap.put(map.getValue().getName(),map.getKey());
			processSet.add(map.getValue().getName());
		}
		requestMap.remove("审核完成");
		requestMap.remove("驳回");
		processSet.remove("审核完成");
		poMap.remove("驳回");
		if (StringUtils.isNotBlank(bizRequestHeader.getProcess()) && requestMap.get(bizRequestHeader.getProcess()) != null) {
			bizRequestHeader.setReqCode(requestMap.get(bizRequestHeader.getProcess()));
		} else if (StringUtils.isNotBlank(bizRequestHeader.getProcess()) && poMap.get(bizRequestHeader.getProcess()) != null){
			bizRequestHeader.setPoCode(poMap.get(bizRequestHeader.getProcess()));
		}
		String dataFrom = "biz_request_bizRequestHeader";
		bizRequestHeader.setDataFrom(dataFrom);
		Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(new Page<BizRequestHeader>(request, response), bizRequestHeader);
        model.addAttribute("page", page);
        //品类名称
		List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());
		List<Role> roleList = user.getRoleList();
		Set<String> roleSet = Sets.newHashSet();
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role r : roleList) {
				RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
				if (parse != null) {
					roleSet.add(parse.name());
				}
			}
		}

		model.addAttribute("processSet",processSet);
		model.addAttribute("roleSet",roleSet);
		model.addAttribute("varietyInfoList", varietyInfoList);
		model.addAttribute("auditStatus", ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());
		model.addAttribute("vendAuditStatus",ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());
		return "modules/biz/request/bizRequestHeaderForVendorList";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = {"list4MobileNew"})
	@ResponseBody
	public String list4MobileNew(BizRequestHeader bizRequestHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> resultMap = Maps.newHashMap();
		User user = UserUtils.getUser();
		List<String> enNameList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(user.getRoleList())) {
			for (Role role : user.getRoleList()) {
				enNameList.add(role.getEnname());
			}
		}

		Map<Integer, com.wanhutong.backend.modules.config.parse.Process> purMap = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap();
		Integer currentCode = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getDefaultNewProcessId();
		Integer lastCode = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId();
		Map<String,Integer> poMap = new LinkedHashMap<>();
		Set<String> processSet = new HashSet<>();
		while (true) {
			com.wanhutong.backend.modules.config.parse.Process current = purMap.get(currentCode);
			com.wanhutong.backend.modules.config.parse.Process next = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPassProcess(current);
			poMap.put(current.getName(),currentCode);
			processSet.add(current.getName());
			if (lastCode.equals(currentCode)) {
				break;
			}
			currentCode = next.getCode();
		}
		Map<Integer, RequestOrderProcessConfig.RequestOrderProcess> reqMap = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap;
		Map<String,Integer> requestMap = new LinkedHashMap<>();


		for (Map.Entry<Integer,RequestOrderProcessConfig.RequestOrderProcess> map : reqMap.entrySet()) {
			requestMap.put(map.getValue().getName(),map.getKey());
			processSet.add(map.getValue().getName());
		}
		requestMap.remove("审核完成");
		requestMap.remove("驳回");
		processSet.remove("审核完成");
		poMap.remove("驳回");
		if (StringUtils.isNotBlank(bizRequestHeader.getProcess()) && requestMap.get(bizRequestHeader.getProcess()) != null) {
			bizRequestHeader.setReqCode(requestMap.get(bizRequestHeader.getProcess()));
		} else if (StringUtils.isNotBlank(bizRequestHeader.getProcess()) && poMap.get(bizRequestHeader.getProcess()) != null){
			bizRequestHeader.setPoCode(poMap.get(bizRequestHeader.getProcess()));
		}
		String dataFrom = "biz_request_bizRequestHeader";
		bizRequestHeader.setDataFrom(dataFrom);
		Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(new Page<BizRequestHeader>(request, response), bizRequestHeader);
		model.addAttribute("page", page);
		resultMap.put("page",page);
		//品类名称
		List<BizVarietyInfo> varietyInfoList = bizVarietyInfoService.findList(new BizVarietyInfo());
		List<Role> roleList = user.getRoleList();
		Set<String> roleSet = Sets.newHashSet();
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role r : roleList) {
				RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
				if (parse != null) {
					roleSet.add(parse.name());
				}
			}
		}

		model.addAttribute("processSet",processSet);
		model.addAttribute("roleSet",roleSet);
		model.addAttribute("varietyInfoList", varietyInfoList);
		model.addAttribute("auditStatus", ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());
		model.addAttribute("vendAuditStatus",ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());

		resultMap.put("processSet",processSet);
		resultMap.put("roleSet",roleSet);
		resultMap.put("varietyInfoList",varietyInfoList);
		resultMap.put("auditStatus",ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());
		resultMap.put("vendAuditStatus",ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().getAutProcessId());

		//常量值添加到json结果集
		resultMap.put("approveState",ReqHeaderStatusEnum.APPROVE.getState());
		resultMap.put("closeState",ReqHeaderStatusEnum.CLOSE.getState());
		resultMap.put("inReviewState",ReqHeaderStatusEnum.IN_REVIEW.getState());
		resultMap.put("vendAllPayState",ReqHeaderStatusEnum.VEND_ALL_PAY.getState());

		resultMap.put("poType",PoPayMentOrderTypeEnum.PO_TYPE.getType());

		//return "modules/biz/request/bizRequestHeaderForVendorList";
		return JsonUtil.generateData(resultMap, null);
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form")
	public String form(BizRequestHeader bizRequestHeader, Model model) {
		List<BizRequestDetail> reqDetailList = Lists.newArrayList();
		if (bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getId() == null) {
			List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
			String str = bizRequestHeader.getStr();
			if (CollectionUtils.isNotEmpty(requestHeaderList)) {
				bizRequestHeader = requestHeaderList.get(0);
				bizRequestHeader.setStr(str);
			}
			BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
			bizPoPaymentOrder.setPoHeaderId(bizRequestHeader.getBizPoHeader().getId());
			bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
			bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
			List<BizPoPaymentOrder> payList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
			if (CollectionUtils.isNotEmpty(payList)) {
				bizPoPaymentOrder = payList.get(0);
			}
			bizRequestHeader.setBizPoPaymentOrder(bizPoPaymentOrder);

		}
		if (bizRequestHeader.getId() != null) {
			BizRequestDetail bizRequestDetail = new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			if (!ReqHeaderStatusEnum.CLOSE.getState().equals(bizRequestHeader.getBizStatus())
					&& bizRequestHeader.getBizStatus() >= ReqHeaderStatusEnum.PURCHASING.getState()
					&& ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
				/* 查询已生成的采购单 标识*/
				bizRequestDetail.setPoheaderSource("poHeader");
			}
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
			BizInventorySku bizInventorySku = new BizInventorySku();
			List<BizInventorySku> inventorySkuList =Lists.newArrayList();
			List<Integer> skuIdList = new ArrayList<>();
//			List<String> typeList = Lists.newLinkedList();
//			typeList.add(OfficeTypeEnum.PURCHASINGCENTER.getType());
//			List<Office> centList = officeService.findListByTypeList(typeList);
//			model.addAttribute("centList",centList);
			for (BizRequestDetail requestDetail : requestDetailList) {
				//skuIdList.add(requestDetail.getSkuInfo().getId());
				bizInventorySku.setSkuInfo(requestDetail.getSkuInfo());
				List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
				inventorySkuList.addAll(list);
				if (requestDetail.getBizPoHeader() == null) {
					bizRequestHeader.setPoSource("poHeaderSource");
				}
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				//requestDetail.setSellCount(findSellCount(requestDetail));
				//Map<String, Integer> stockQtyMap = selectCentInvSku(centList, skuInfo, bizRequestHeader.getFromType());
				//requestDetail.setInvSkuMap(stockQtyMap);
				reqDetailList.add(requestDetail);
			}
			model.addAttribute("inventorySkuList",inventorySkuList);
			List<BizOrderHeader> orderHeaderList = bizRequestHeaderForVendorService.findOrderForVendReq(skuIdList, bizRequestHeader.getFromOffice().getId());
			model.addAttribute("orderHeaderList",orderHeaderList);
			if (requestDetailList.size() == 0) {
				bizRequestHeader.setPoSource("poHeaderSource");
			}
			RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();
			model.addAttribute("defaultProcessId",requestOrderProcessConfig.getDefaultProcessId().toString());
		}

        if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() == null) {
            RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
            model.addAttribute("requestOrderProcess", requestOrderProcess);
        }
        if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getBizPoHeader().getCommonProcess() != null) {
			com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizRequestHeader.getBizPoHeader().getCommonProcess().getType()));
			model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
		}
		if (bizRequestHeader.getBizPoHeader() != null) {
			model.addAttribute("poSchType", bizRequestHeader.getBizPoHeader().getPoSchType());
		}

//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}
//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			VendorRequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}

		if (bizRequestHeader.getId() != null && bizRequestHeader.getId() != 0) {
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizRequestHeader.getId());
			bizOrderStatus.setOrderHeader(bizOrderHeader);
			bizOrderStatus.setOrderType(BizOrderStatus.OrderType.REQUEST.getType());
			List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

			Map<Integer, ReqHeaderStatusEnum> statusMap = ReqHeaderStatusEnum.getStatusMap();
			List<BizPoPaymentOrder> paymentOrderList = getPayMentOrderByReqId(bizRequestHeader.getId());
			model.addAttribute("paymentOrderList",paymentOrderList);
			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);
		}

		User userAdmin = UserUtils.getUser();
		//渠道部角色
		List<Role> roleList = userAdmin.getRoleList();
		String roleName = null;
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role role : roleList) {
				if (role.getEnname().equals(RoleEnNameEnum.CHANNEL_MANAGER.getState()) || userAdmin.isAdmin() ) {
					roleName = "channeOk";
				}
			}
		}
		model.addAttribute("roleChanne", roleName);

		model.addAttribute("entity", bizRequestHeader);
		model.addAttribute("reqDetailList", reqDetailList);
		model.addAttribute("bizSkuInfo", new BizSkuInfo());

		return "modules/biz/request/bizRequestHeaderForVendorForm";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form4MobileNew")
	@ResponseBody
	public String form4MobileNew(BizRequestHeader bizRequestHeader, Model model) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<BizRequestDetail> reqDetailList = Lists.newArrayList();
		if (bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getId() == null) {
			List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
			String str = bizRequestHeader.getStr();
			if (CollectionUtils.isNotEmpty(requestHeaderList)) {
				bizRequestHeader = requestHeaderList.get(0);
				bizRequestHeader.setStr(str);
			}
			BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
			bizPoPaymentOrder.setPoHeaderId(bizRequestHeader.getBizPoHeader().getId());
			bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
			bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
			List<BizPoPaymentOrder> payList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
			if (CollectionUtils.isNotEmpty(payList)) {
				bizPoPaymentOrder = payList.get(0);
			}
			bizRequestHeader.setBizPoPaymentOrder(bizPoPaymentOrder);

		}
		if (bizRequestHeader.getId() != null) {
			BizRequestDetail bizRequestDetail = new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			if (!ReqHeaderStatusEnum.CLOSE.getState().equals(bizRequestHeader.getBizStatus())
					&& bizRequestHeader.getBizStatus() >= ReqHeaderStatusEnum.PURCHASING.getState()
					&& ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
				/* 查询已生成的采购单 标识*/
				bizRequestDetail.setPoheaderSource("poHeader");
			}
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
			BizInventorySku bizInventorySku = new BizInventorySku();
			List<BizInventorySku> inventorySkuList =Lists.newArrayList();
			List<Integer> skuIdList = new ArrayList<>();
//			List<String> typeList = Lists.newLinkedList();
//			typeList.add(OfficeTypeEnum.PURCHASINGCENTER.getType());
//			List<Office> centList = officeService.findListByTypeList(typeList);
//			model.addAttribute("centList",centList);
			for (BizRequestDetail requestDetail : requestDetailList) {
				//skuIdList.add(requestDetail.getSkuInfo().getId());
				bizInventorySku.setSkuInfo(requestDetail.getSkuInfo());
				List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
				inventorySkuList.addAll(list);
				if (requestDetail.getBizPoHeader() == null) {
					bizRequestHeader.setPoSource("poHeaderSource");
				}
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				//requestDetail.setSellCount(findSellCount(requestDetail));
				//Map<String, Integer> stockQtyMap = selectCentInvSku(centList, skuInfo, bizRequestHeader.getFromType());
				//requestDetail.setInvSkuMap(stockQtyMap);
				reqDetailList.add(requestDetail);
			}
			model.addAttribute("inventorySkuList",inventorySkuList);
			resultMap.put("inventorySkuList", inventorySkuList);
//			List<BizOrderHeader> orderHeaderList = bizRequestHeaderForVendorService.findOrderForVendReq(skuIdList, bizRequestHeader.getFromOffice().getId());
//			model.addAttribute("orderHeaderList",orderHeaderList);
//			resultMap.put("orderHeaderList", orderHeaderList);
			if (requestDetailList.size() == 0) {
				bizRequestHeader.setPoSource("poHeaderSource");
			}
			RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();
			model.addAttribute("defaultProcessId",requestOrderProcessConfig.getDefaultProcessId().toString());
			resultMap.put("defaultProcessId", requestOrderProcessConfig.getDefaultProcessId().toString());
		}

		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() == null) {
			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
			model.addAttribute("requestOrderProcess", requestOrderProcess);
			resultMap.put("requestOrderProcess", requestOrderProcess);
		}
		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getBizPoHeader().getCommonProcess() != null) {
			com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizRequestHeader.getBizPoHeader().getCommonProcess().getType()));
			model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
			resultMap.put("purchaseOrderProcess", purchaseOrderProcess);
		}
		if (bizRequestHeader.getBizPoHeader() != null) {
			model.addAttribute("poSchType", bizRequestHeader.getBizPoHeader().getPoSchType());
			resultMap.put("poSchType", bizRequestHeader.getBizPoHeader().getPoSchType());
		}

//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}
//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			VendorRequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}

		if (bizRequestHeader.getId() != null && bizRequestHeader.getId() != 0) {
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizRequestHeader.getId());
			bizOrderStatus.setOrderHeader(bizOrderHeader);
			bizOrderStatus.setOrderType(BizOrderStatus.OrderType.REQUEST.getType());
			List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

			Map<Integer, ReqHeaderStatusEnum> statusMap = ReqHeaderStatusEnum.getStatusMap();
			List<BizPoPaymentOrder> paymentOrderList = getPayMentOrderByReqId(bizRequestHeader.getId());
			model.addAttribute("paymentOrderList",paymentOrderList);
			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);

			Map<Integer, String> stateDescMap = ReqHeaderStatusEnum.getStateDescMap();
			resultMap.put("stateDescMap", stateDescMap);
			resultMap.put("paymentOrderList", paymentOrderList);
			resultMap.put("statusList", statusList);
			resultMap.put("statusMap", statusMap);
		}

		User userAdmin = UserUtils.getUser();
		//渠道部角色
		List<Role> roleList = userAdmin.getRoleList();
		String roleName = null;
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role role : roleList) {
				if (role.getEnname().equals(RoleEnNameEnum.CHANNEL_MANAGER.getState()) || userAdmin.isAdmin() ) {
					roleName = "channeOk";
				}
			}
		}
		model.addAttribute("roleChanne", roleName);

		model.addAttribute("entity", bizRequestHeader);
		model.addAttribute("reqDetailList", reqDetailList);
		model.addAttribute("bizSkuInfo", new BizSkuInfo());

		resultMap.put("roleChanne", roleName);
		resultMap.put("entity", bizRequestHeader);
		resultMap.put("reqDetailList", reqDetailList);
		resultMap.put("bizSkuInfo", new BizSkuInfo());

		resultMap.put("STOCKREADYCOMMISSIONER", RoleEnNameEnum.STOCKREADYCOMMISSIONER.getState());
		resultMap.put("MARKETINGMANAGER", RoleEnNameEnum.MARKETINGMANAGER.getState());
		resultMap.put("IN_REVIEW", ReqHeaderStatusEnum.IN_REVIEW.getState());
		resultMap.put("APPROVE", ReqHeaderStatusEnum.APPROVE.getState());
		resultMap.put("UNREVIEWED", ReqHeaderStatusEnum.UNREVIEWED.getState());
		resultMap.put("PURCHASING", ReqHeaderStatusEnum.PURCHASING.getState());
		resultMap.put("VENDOR_TYPE", ReqFromTypeEnum.VENDOR_TYPE.getType());

		//return "modules/biz/request/bizRequestHeaderForVendorForm";
		return JsonUtil.generateData(resultMap, null);
	}

	private Map<String,Integer> selectCentInvSku(List<Office> centList, BizSkuInfo skuInfo, Integer skuType) {
		Map<String, Integer> map = new LinkedHashMap<>();
		if (CollectionUtils.isNotEmpty(centList)) {
			for (Office office : centList) {
				Integer stockQty = bizInventorySkuService.getStockQtyBySkuIdCentIdSkuType(skuInfo.getId(), office.getId(), skuType);
				map.put(office.getName(),stockQty == null ? 0 : stockQty);
			}
		}
		return map;
	}

	private List<BizPoPaymentOrder> getPayMentOrderByReqId(Integer reqId) {
		return bizPoPaymentOrderService.getPayMentOrderByReqId(reqId);
	}

	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "form4Mobile")
	public String form4Mobile(BizRequestHeader bizRequestHeader, Model model) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<BizRequestDetail> reqDetailList = Lists.newArrayList();
		if (bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getId() == null) {
			List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
			String str = bizRequestHeader.getStr();
			if (CollectionUtils.isNotEmpty(requestHeaderList)) {
				bizRequestHeader = requestHeaderList.get(0);
				bizRequestHeader.setStr(str);
			}
			BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
			bizPoPaymentOrder.setPoHeaderId(bizRequestHeader.getBizPoHeader().getId());
			bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
			bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
			List<BizPoPaymentOrder> payList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
			if (CollectionUtils.isNotEmpty(payList)) {
				bizPoPaymentOrder = payList.get(0);
			}
			bizRequestHeader.setBizPoPaymentOrder(bizPoPaymentOrder);

		}
		if (bizRequestHeader.getId() != null) {
			BizRequestDetail bizRequestDetail = new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			if (!ReqHeaderStatusEnum.CLOSE.getState().equals(bizRequestHeader.getBizStatus())
					&& bizRequestHeader.getBizStatus() >= ReqHeaderStatusEnum.PURCHASING.getState()
					&& ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
				/* 查询已生成的采购单 标识*/
				bizRequestDetail.setPoheaderSource("poHeader");
			}
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
			BizInventorySku bizInventorySku = new BizInventorySku();
			List<BizInventorySku> inventorySkuList =Lists.newArrayList();
			List<Integer> skuIdList = new ArrayList<>();
//			List<String> typeList = Lists.newLinkedList();
//			typeList.add(OfficeTypeEnum.PURCHASINGCENTER.getType());
//			List<Office> centList = officeService.findListByTypeList(typeList);
//			model.addAttribute("centList",centList);
			for (BizRequestDetail requestDetail : requestDetailList) {
				//skuIdList.add(requestDetail.getSkuInfo().getId());
				bizInventorySku.setSkuInfo(requestDetail.getSkuInfo());
				List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
				inventorySkuList.addAll(list);
				if (requestDetail.getBizPoHeader() == null) {
					bizRequestHeader.setPoSource("poHeaderSource");
				}
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				//requestDetail.setSellCount(findSellCount(requestDetail));
				//Map<String, Integer> stockQtyMap = selectCentInvSku(centList, skuInfo, bizRequestHeader.getFromType());
				//requestDetail.setInvSkuMap(stockQtyMap);
				reqDetailList.add(requestDetail);
			}
			model.addAttribute("inventorySkuList",inventorySkuList);
			resultMap.put("inventorySkuList", inventorySkuList);
			List<BizOrderHeader> orderHeaderList = bizRequestHeaderForVendorService.findOrderForVendReq(skuIdList, bizRequestHeader.getFromOffice().getId());
			model.addAttribute("orderHeaderList",orderHeaderList);
			resultMap.put("orderHeaderList", orderHeaderList);
			if (requestDetailList.size() == 0) {
				bizRequestHeader.setPoSource("poHeaderSource");
			}
			RequestOrderProcessConfig requestOrderProcessConfig = ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get();
			model.addAttribute("defaultProcessId",requestOrderProcessConfig.getDefaultProcessId().toString());
			resultMap.put("defaultProcessId", requestOrderProcessConfig.getDefaultProcessId().toString());
		}

		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() == null) {
			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
			model.addAttribute("requestOrderProcess", requestOrderProcess);
			resultMap.put("requestOrderProcess", requestOrderProcess);
		}
		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && bizRequestHeader.getBizPoHeader() != null && bizRequestHeader.getBizPoHeader().getCommonProcess() != null) {
			com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizRequestHeader.getBizPoHeader().getCommonProcess().getType()));
			model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
			resultMap.put("purchaseOrderProcess", purchaseOrderProcess);
		}
		if (bizRequestHeader.getBizPoHeader() != null) {
			model.addAttribute("poSchType", bizRequestHeader.getBizPoHeader().getPoSchType());
			resultMap.put("poSchType", bizRequestHeader.getBizPoHeader().getPoSchType());
		}

//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}
//		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr()) && ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//			VendorRequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
//					ConfigGeneral.VENDOR_REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
//			model.addAttribute("requestOrderProcess", requestOrderProcess);
//		}

		if (bizRequestHeader.getId() != null && bizRequestHeader.getId() != 0) {
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizRequestHeader.getId());
			bizOrderStatus.setOrderHeader(bizOrderHeader);
			bizOrderStatus.setOrderType(BizOrderStatus.OrderType.REQUEST.getType());
			List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

			Map<Integer, ReqHeaderStatusEnum> statusMap = ReqHeaderStatusEnum.getStatusMap();
			List<BizPoPaymentOrder> paymentOrderList = getPayMentOrderByReqId(bizRequestHeader.getId());
			model.addAttribute("paymentOrderList",paymentOrderList);
			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);

			resultMap.put("paymentOrderList", paymentOrderList);
			resultMap.put("statusList", statusList);
			resultMap.put("statusMap", statusMap);
		}

		User userAdmin = UserUtils.getUser();
		//渠道部角色
		List<Role> roleList = userAdmin.getRoleList();
		String roleName = null;
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role role : roleList) {
				if (role.getEnname().equals(RoleEnNameEnum.CHANNEL_MANAGER.getState()) || userAdmin.isAdmin() ) {
					roleName = "channeOk";
				}
			}
		}
		model.addAttribute("roleChanne", roleName);

		model.addAttribute("entity", bizRequestHeader);
		model.addAttribute("reqDetailList", reqDetailList);
		model.addAttribute("bizSkuInfo", new BizSkuInfo());

		resultMap.put("roleChanne", roleName);
		resultMap.put("entity", bizRequestHeader);
		resultMap.put("reqDetailList", reqDetailList);
		resultMap.put("bizSkuInfo", new BizSkuInfo());

		return JsonUtil.generateData(resultMap, null);
	}

	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "findByRequest")
	public List<BizRequestHeader> findByRequest(BizRequestHeader bizRequestHeader) {
		bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState().byteValue());
		bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
		List<BizRequestHeader> list= bizRequestHeaderForVendorService.findList(bizRequestHeader);
		List<BizRequestHeader> bizRequestHeaderList=Lists.newArrayList();
		BizPoOrderReq bizPoOrderReq=new BizPoOrderReq();
		for (BizRequestHeader bizRequestHeader1:list) {
			BizRequestDetail bizRequestDetail1 = new BizRequestDetail();
			bizRequestDetail1.setRequestHeader(bizRequestHeader1);
			BizSkuInfo bizSkuInfo =new BizSkuInfo();
			bizSkuInfo.setItemNo(bizRequestHeader.getItemNo());
			bizSkuInfo.setPartNo(bizRequestHeader.getPartNo());
			bizSkuInfo.setVendorName(bizRequestHeader.getName());
			bizRequestDetail1.setSkuInfo(bizSkuInfo);

			bizPoOrderReq.setRequestHeader(bizRequestHeader1);

			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail1);
			List<BizRequestDetail> reqDetailList =Lists.newArrayList();
			for (BizRequestDetail requestDetail:requestDetailList){
				bizPoOrderReq.setSoLineNo(requestDetail.getLineNo());
				List<BizPoOrderReq> poOrderReqList= bizPoOrderReqService.findList(bizPoOrderReq);
				PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
				Iterator<BizPoOrderReq> poOrderReqIterator = poOrderReqList.iterator();
//				if (poOrderReqIterator.hasNext()) {
//					BizPoHeader bizPoHeader = bizPoHeaderService.get(poOrderReqIterator.next().getPoHeader());
//					if (bizPoHeader.getProcessId() != purchaseOrderProcessConfig.getPayProcessId()) {
//						poOrderReqList.remove(poOrderReqIterator.next());
//					}
//				}
				if(CollectionUtils.isNotEmpty(poOrderReqList)){
					BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
					skuInfo.setVendorName(requestDetail.getSkuInfo().getVendorName());
					requestDetail.setSkuInfo(skuInfo);
					reqDetailList.add(requestDetail);
				}
			}
			bizRequestHeader1.setRequestDetailList(reqDetailList);
			bizRequestHeaderList.add(bizRequestHeader1);
		}
		return bizRequestHeaderList;
	}


	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "save")
	public String save(BizRequestHeader bizRequestHeader, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, bizRequestHeader)){
			return form(bizRequestHeader, model);
		}
		if (bizRequestHeader.getId() != null) {
			BizPoHeader bizPoHeader = new BizPoHeader();
			bizPoHeader.setBizRequestHeader(bizRequestHeader);
			List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);
			if (CollectionUtils.isNotEmpty(poList)) {
				bizPoHeaderService.updateProcessToInitAudit(poList.get(0), StringUtils.EMPTY);
			}
		}
		bizRequestHeaderForVendorService.save(bizRequestHeader);
		addMessage(redirectAttributes, "保存备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeaderForVendor/?repage";
	}
	@ResponseBody
	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "saveInfo")
	public boolean saveInfo(BizRequestHeader bizRequestHeader, String checkStatus) {
		Integer bizStatus = bizRequestHeader.getBizStatus();
		bizRequestHeader.setBizStatus(Integer.parseInt(checkStatus));
		boolean boo=false;
		try {
			if(bizRequestHeader.getRemarkReject()!=null && !bizRequestHeader.getRemarkReject().equals("adopt")){
				if(bizRequestHeader.getRemark()!=null && bizRequestHeader.getRemark().contains(":驳回原因：")){
					bizRequestHeader.setRemark(bizRequestHeader.getRemark()+bizRequestHeader.getRemarkReject());
				}else{
					bizRequestHeader.setRemark(bizRequestHeader.getRemark()+"\n"+":驳回原因："+bizRequestHeader.getRemarkReject());
				}
			}else{
				if(bizRequestHeader.getRemark()!=null && bizRequestHeader.getRemark().contains(":驳回原因：")){
					String b="";
					String[] split = bizRequestHeader.getRemark().split("\n:");
					for (int i = 0; i < split.length; i++) {
						if(i==0){
							b= split[i];
							break;
						}
					}
					bizRequestHeader.setRemark(String.valueOf(b));
				}
			}
			bizRequestHeaderForVendorService.save(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
			}
			boo=true;
		}catch (Exception e){
			boo=false;
			logger.error(e.getMessage());
		}
			return boo;

	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "delete")
	public String delete(BizRequestHeader bizRequestHeader, RedirectAttributes redirectAttributes) {
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_DELETE);
		bizRequestHeaderForVendorService.delete(bizRequestHeader);
		addMessage(redirectAttributes, "删除备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeaderForVendor/?repage";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "delete4Mobile")
	@ResponseBody
	public String delete4Mobile(int id) {
		BizRequestHeader bizRequestHeader = new BizRequestHeader();
		bizRequestHeader.setId(id);
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_DELETE);
		bizRequestHeaderForVendorService.delete(bizRequestHeader);
		return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "recovery")
	public String recovery(BizRequestHeader bizRequestHeader, RedirectAttributes redirectAttributes) {
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_NORMAL);
		bizRequestHeaderForVendorService.delete(bizRequestHeader);
		addMessage(redirectAttributes, "删除备货清单成功");
		return "redirect:"+Global.getAdminPath()+"/biz/request/bizRequestHeaderForVendor/?repage";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:edit")
	@RequestMapping(value = "recovery4Mobile")
	public String recovery4Mobile(int id) {
		BizRequestHeader bizRequestHeader = new BizRequestHeader();
		bizRequestHeader.setId(id);
		bizRequestHeader.setDelFlag(BizRequestHeader.DEL_FLAG_NORMAL);
		bizRequestHeaderForVendorService.delete(bizRequestHeader);
		return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
	}

	@RequiresPermissions("biz:request:bizRequestHeader:audit")
	@RequestMapping(value = "startAudit")
	@ResponseBody
	public String startAudit(HttpServletRequest request, Integer id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, Integer auditType, String desc) {
		Pair<Boolean, String> result = bizRequestHeaderForVendorService.startAudit(id, prew, prewPayTotal, prewPayDeadline, auditType, desc);
		if (result.getLeft()) {
			return JsonUtil.generateData(result, request.getParameter("callback"));
		}
		return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
	}

	@RequiresPermissions("biz:request:bizRequestHeader:createPayOrder")
	@RequestMapping(value = "saveRequest")
	public String saveRequest(BizRequestHeader bizRequestHeader, Model model, RedirectAttributes redirectAttributes, String type) {
		if ("createPay".equalsIgnoreCase(type)) {
			String msg = bizRequestHeaderForVendorService.genPaymentOrder(bizRequestHeader).getRight();
			addMessage(redirectAttributes, msg);
			return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor/?repage";
		}
		if (!beanValidator(model, bizRequestHeader)) {
			return form(bizRequestHeader, model);
		}
		addMessage(redirectAttributes, "保存备货单成功");
		return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor/?repage";
	}

	/**
	 * 备货清单管理 导出
	 * */
	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "requestHeaderExport")
	public String requestHeaderExport(BizRequestHeader bizRequestHeader,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String fileName = "备货清单" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
			bizRequestHeader.setDataStatus("detailed");
			List<BizRequestHeader> list = bizRequestHeaderForVendorService.findListExport(bizRequestHeader);
			//1备货清单
			List<List<String>> data = new ArrayList<List<String>>();
			//2备货清单,商品
			List<List<String>> skuData = new ArrayList<List<String>>();
			if(list.size()!=0){
				for(BizRequestHeader header:list){
					List<String> headerListData = new ArrayList();
					List<BizRequestDetail> reqDetailList=Lists.newArrayList();
					BizRequestDetail bizRequestDetail=new BizRequestDetail();
					bizRequestDetail.setRequestHeader(header);
					List<BizRequestDetail> requestDetailList=bizRequestDetailService.findList(bizRequestDetail);
					if(requestDetailList.size()!=0){
						for(BizRequestDetail requestDetail:requestDetailList){
							BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
							requestDetail.setSkuInfo(skuInfo);
							requestDetail.setRequestHeader(header);
							reqDetailList.add(requestDetail);
						}
					}
					//备货单遍历
					headerListData.add(header.getReqNo());
					if(header.getFromOffice()!=null && header.getFromOffice().getName()!=null){
						//采购中心
						headerListData.add(String.valueOf(header.getFromOffice().getName()));
					}else{
						headerListData.add("");
					}
					//期望收货时间
					headerListData.add(String.valueOf(sdf.format(header.getRecvEta())));
					//	备货商品数量
					headerListData.add(String.valueOf(header.getReqQtys()==null?"":header.getReqQtys()));
					//备货商品总价
					headerListData.add(String.valueOf(header.getTotalMoney()==null?"":header.getTotalMoney()));
					//已收保证金
					headerListData.add(String.valueOf(header.getRecvTotal()==null?"":header.getRecvTotal()));
					headerListData.add(String.valueOf(header.getRecvQtys()==null?"":header.getRecvQtys()));
					headerListData.add(String.valueOf(header.getRemark()==null?"":header.getRemark()));
					Dict dict = new Dict();
					dict.setDescription("备货单业务状态");
					dict.setType("biz_req_status");
					List<Dict> dictList = dictService.findList(dict);
					for (Dict bizDict : dictList) {
						if(bizDict.getValue().equals(String.valueOf(header.getBizStatus()))){
							//业务状态
							headerListData.add(String.valueOf(bizDict.getLabel()));
							break;
						}
					}
					headerListData.add(String.valueOf(sdf.format(header.getCreateDate())));
					if(header.getCreateBy()!=null && header.getCreateBy().getName()!=null){
						headerListData.add(String.valueOf(header.getCreateBy().getName()));
					}else{
						headerListData.add("");
					}
					data.add(headerListData);
					if(reqDetailList.size()!=0){
						reqDetailList.forEach(detail->{
							List<String> detailListData = new ArrayList();
							//商品遍历
							detailListData.add(String.valueOf(detail.getRequestHeader().getReqNo()));
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getProductInfo()!=null){
								//产品名称，品牌名称
								detailListData.add(String.valueOf(detail.getSkuInfo().getProductInfo().getName()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getProductInfo().getBrandName()));
							}else{
								detailListData.add("");
								detailListData.add("");
							}
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getName()!=null || detail.getSkuInfo().getPartNo()!=null || detail.getSkuInfo().getItemNo()!=null){
								//商品名称，商品编号，商品货号
								detailListData.add(String.valueOf(detail.getSkuInfo().getName()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getPartNo()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getItemNo()));
							}else{
								detailListData.add("");
								detailListData.add("");
								detailListData.add("");
							}
							if(detail.getSkuInfo()!=null && detail.getSkuInfo().getSkuPropertyInfos()!=null || detail.getSkuInfo().getBuyPrice()!=null){
								//商品属性，结算价
								detailListData.add(String.valueOf(detail.getSkuInfo().getSkuPropertyInfos()));
								detailListData.add(String.valueOf(detail.getSkuInfo().getBuyPrice()));
							}else{
								detailListData.add("");
								detailListData.add("");
							}
							detailListData.add(String.valueOf(detail.getReqQty()==null?"":detail.getReqQty()));
							if(detail.getRequestHeader()!=null && detail.getRequestHeader().getRecvEta()!=null){
								detailListData.add(String.valueOf(sdf.format(detail.getRequestHeader().getRecvEta())));
							}else{
								detailListData.add("");
							}
							skuData.add(detailListData);
						});
					}
				}
			}
			String[] headers = {"备货单号", "采购中心","期望收货时间", "备货商品数量", "备货商品总价","已收保证金","已到货数量", "备注", "业务状态","下单时间","申请人"};
			String[] details = {"备货单号", "产品名称", "品牌名称", "商品名称","商品编码", "商品货号", "商品属性", "结算价", "申报数量","期望收货时间"};
			ExportExcelUtils eeu = new ExportExcelUtils();
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			eeu.exportExcel(workbook, 0, "备货单数据", headers, data, fileName);
			eeu.exportExcel(workbook, 1, "商品数据", details, skuData, fileName);
			response.reset();
			response.setContentType("application/octet-stream; charset=utf-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
			workbook.write(response.getOutputStream());
			workbook.dispose();
			return null;
		}catch (Exception e){
			e.printStackTrace();
			addMessage(redirectAttributes, "导出备货清单数据失败！失败信息：" + e.getMessage());
		}
		return "redirect:" + adminPath + "/biz/request/bizRequestHeaderForVendor/list";
	}

	@RequiresPermissions("biz:request:bizRequestHeader:audit")
	@RequestMapping(value = "audit")
	@ResponseBody
	public String audit(int id, String currentType, int auditType, String description) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(id);
		String result = bizRequestHeaderForVendorService.audit(id, currentType, auditType, description);
//		if (ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
//            result = bizRequestHeaderForVendorService.vendAudit(id, currentType, auditType, description);
//		}else {
//            result =bizRequestHeaderForVendorService.audit(id, currentType, auditType, description);
//		}
		return result;
	}

    @RequiresPermissions("biz:request:bizRequestHeader:audit")
    @RequestMapping(value = "getCurrentBizStatus")
    @ResponseBody
    public String getCurrentBizStatus(int id) {
        BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(id);
        Integer bizStatus = bizRequestHeader.getBizStatus();
        return String.valueOf(bizStatus);
    }

	/**
	 * 查询供应商备货的供应商拓展信息
	 * @param vendorId
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("biz:vend:bizVendInfo:view")
	@RequestMapping(value = "selectVendInfo")
	public BizVendInfo selectVendInfo(Integer vendorId) {

		BizVendInfo bizVendInfo = bizVendInfoService.get(vendorId);
		if (bizVendInfo == null) {
			return null;
		}
		CommonImg compactImg = new CommonImg();
		compactImg.setImgType(ImgEnum.VEND_COMPACT.getCode());
		compactImg.setObjectId(vendorId);
		compactImg.setObjectName(ImgEnum.VEND_COMPACT.getTableName());
		List<CommonImg> compactImgList = commonImgService.findList(compactImg);

		CommonImg identityCardImg = new CommonImg();
		identityCardImg.setImgType(ImgEnum.VEND_IDENTITY_CARD.getCode());
		identityCardImg.setObjectId(vendorId);
		identityCardImg.setObjectName(ImgEnum.VEND_IDENTITY_CARD.getTableName());
		List<CommonImg> identityCardImgList = commonImgService.findList(identityCardImg);

		bizVendInfo.setCompactImgList(compactImgList);
		bizVendInfo.setIdentityCardImgList(identityCardImgList);
		return bizVendInfo;
	}

	public Integer findSellCount(BizRequestDetail requestDetail) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(requestDetail.getRequestHeader().getId());
		Integer centId = bizRequestHeader.getFromOffice().getId();
		Integer skuId = requestDetail.getSkuInfo().getId();
		return bizRequestHeaderForVendorService.findSellCount(centId,skuId);
	}

	@RequiresPermissions("biz:request:bizRequestHeader:view")
	@RequestMapping(value = "scheduling")
	public String scheduling(HttpServletRequest request, BizRequestHeader bizRequestHeader, Model model) {
		List<BizRequestDetail> reqDetailList = Lists.newArrayList();
		List<Integer> reqDetailIdList = Lists.newArrayList();
		if (bizRequestHeader.getId() != null) {
			BizRequestDetail bizRequestDetail = new BizRequestDetail();
			bizRequestDetail.setRequestHeader(bizRequestHeader);
			if (!ReqHeaderStatusEnum.CLOSE.getState().equals(bizRequestHeader.getBizStatus())
					&& bizRequestHeader.getBizStatus() >= ReqHeaderStatusEnum.PURCHASING.getState()
					&& ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
				/* 查询已生成的采购单 标识*/
				bizRequestDetail.setPoheaderSource("poHeader");
			}
			List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
			BizInventorySku bizInventorySku = new BizInventorySku();
			List<Integer> skuIdList = new ArrayList<>();

			//按订单排产时，获取排产记录
			Integer schedulingType = bizRequestHeader.getSchedulingType();

			Boolean detailHeaderFlg = false;
			List<BizCompletePaln> bizCompletePalns = new ArrayList<>();
			if (SCHEDULING_FOR_HEADER.equals(schedulingType)) {
				BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(bizRequestHeader.getId(), REQUEST_HEADER_TABLE_NAME);
				if (bizSchedulingPlan != null) {
					bizCompletePalns = bizSchedulingPlan.getCompletePalnList();
				}
				if (bizSchedulingPlan != null) {
					detailHeaderFlg = true;
				}
			}
			model.addAttribute("bizCompletePalns", bizCompletePalns);

			Boolean detailSchedulingFlg = false;
			for (BizRequestDetail requestDetail : requestDetailList) {
				skuIdList.add(requestDetail.getSkuInfo().getId());
				bizInventorySku.setSkuInfo(requestDetail.getSkuInfo());
				List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
				if (CollectionUtils.isNotEmpty(list)) {
					//已有的库存数量
					bizRequestHeader.setInvenSource("inventorySku");
					requestDetail.setInvenSkuOrd(list.size());
				}
				if (requestDetail.getBizPoHeader() == null) {
					bizRequestHeader.setPoSource("poHeaderSource");
				}
				BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
				requestDetail.setSkuInfo(skuInfo);
				requestDetail.setSellCount(findSellCount(requestDetail));

				//排产类型为按商品排产时，获取排产记录
				if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
					BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(requestDetail.getId(), REQUEST_DETAIL_TABLE_NAME);
					if (bizSchedulingPlan != null) {
						detailSchedulingFlg = true;
					}
					requestDetail.setBizSchedulingPlan(bizSchedulingPlan);
				}

				BizRequestDetail requestDetailTemp = bizRequestDetailService.getsumSchedulingNum(requestDetail.getId());
				if (requestDetailTemp != null) {
					requestDetail.setSumSchedulingNum(requestDetailTemp.getSumSchedulingNum());
					requestDetail.setSumCompleteNum(requestDetailTemp.getSumCompleteNum());
					requestDetail.setSumCompleteDetailNum(requestDetailTemp.getSumCompleteDetailNum());
				}
				reqDetailIdList.add(requestDetail.getId());
				reqDetailList.add(requestDetail);
			}
			List<BizOrderHeader> orderHeaderList = bizRequestHeaderForVendorService.findOrderForVendReq(skuIdList, bizRequestHeader.getFromOffice().getId());
			model.addAttribute("orderHeaderList", orderHeaderList);
			if (requestDetailList.size() == 0) {
				bizRequestHeader.setPoSource("poHeaderSource");
			}

			model.addAttribute("detailHeaderFlg", detailHeaderFlg);
			model.addAttribute("detailSchedulingFlg", detailSchedulingFlg);
		}

		if ("audit".equalsIgnoreCase(bizRequestHeader.getStr())) {
			RequestOrderProcessConfig.RequestOrderProcess requestOrderProcess =
					ConfigGeneral.REQUEST_ORDER_PROCESS_CONFIG.get().processMap.get(Integer.valueOf(bizRequestHeader.getCommonProcess().getType()));
			model.addAttribute("requestOrderProcess", requestOrderProcess);
		}

		if (bizRequestHeader.getId() != null && bizRequestHeader.getId() != 0) {
			BizOrderStatus bizOrderStatus = new BizOrderStatus();
			BizOrderHeader bizOrderHeader = new BizOrderHeader();
			bizOrderHeader.setId(bizRequestHeader.getId());
			bizOrderStatus.setOrderHeader(bizOrderHeader);
			bizOrderStatus.setOrderType(BizOrderStatus.OrderType.REQUEST.getType());
			List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
			statusList.sort((o1, o2) -> o1.getId().compareTo(o2.getId()));

			Map<Integer, ReqHeaderStatusEnum> statusMap = ReqHeaderStatusEnum.getStatusMap();

			model.addAttribute("statusList", statusList);
			model.addAttribute("statusMap", statusMap);
		}

		User userAdmin = UserUtils.getUser();
		//渠道部角色
		Boolean roleFlag = false;
		List<Role> roleList = userAdmin.getRoleList();
		//判断当前用户是否为供应商
		if (roleList != null) {
			for (Role role : roleList) {
				String roleName = role.getName();
				if (RoleEnNameEnum.SUPPLY_CHAIN.getDesc().equals(roleName)) {
					roleFlag = true;
				}
			}
		}
		model.addAttribute("roleFlag", roleFlag);

		String roleName = null;
		if (CollectionUtils.isNotEmpty(roleList)) {
			for (Role role : roleList) {
				if (role.getEnname().equals(RoleEnNameEnum.CHANNEL_MANAGER.getState()) || userAdmin.isAdmin()) {
					roleName = "channeOk";
				}
			}
		}
		model.addAttribute("roleChanne", roleName);

		model.addAttribute("entity", bizRequestHeader);
		model.addAttribute("reqDetailList", reqDetailList);

		JSONArray reqDetailIdListJson = JSONArray.fromObject(reqDetailIdList);
		model.addAttribute("reqDetailIdListJson", reqDetailIdListJson);
		model.addAttribute("bizSkuInfo", new BizSkuInfo());

		String forward = request.getParameter("forward");
		String forwardPage = "";
		if ("confirmScheduling".equals(forward)) {
			forwardPage = "modules/biz/request/bizRequestHeaderForVendorCompleteScheduling";
		} else {
			forwardPage = "modules/biz/request/bizRequestHeaderForVendorScheduling";
		}
		model.addAttribute("roleFlag", roleFlag);
		return forwardPage;
	}

	@RequestMapping(value = "saveSchedulingPlan")
	@ResponseBody
	public boolean saveSchedulingPlan(HttpServletRequest request, @RequestBody String params) throws ParseException {
		List<BizHeaderSchedulingDto> dtoList = JsonUtil.parseArray(params, new TypeReference<List<BizHeaderSchedulingDto>>() {
		});
		boolean boo = false;
		if (dtoList != null && dtoList.size() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//通过排产类型获取排产表中objectName的值
			Integer schedulingType = Integer.parseInt(dtoList.get(0).getSchedulingType());
			String objectName = REQUEST_HEADER_TABLE_NAME;
			if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
				objectName = REQUEST_DETAIL_TABLE_NAME;
				for (int i = 0; i < dtoList.size(); i++) {
					Integer detailId = dtoList.get(i).getObjectId();
					BizHeaderSchedulingDto dto = dtoList.get(i);
					BizSchedulingPlan schedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(detailId, objectName);
					if (schedulingPlan == null) {
						schedulingPlan = new BizSchedulingPlan();
						schedulingPlan.setObjectId(dto.getObjectId());
						schedulingPlan.setObjectName(objectName);
						schedulingPlan.setOriginalNum(dto.getOriginalNum());
						bizSchedulingPlanService.save(schedulingPlan);
					}

					BizCompletePaln bizCompletePaln = new BizCompletePaln();
					bizCompletePaln.setSchedulingPlan(schedulingPlan);
					bizCompletePaln.setCompleteNum(dto.getSchedulingNum());
					bizCompletePaln.setPlanDate(sdf.parse(dto.getPlanDate()));
					try {
						//防止catch后死循环
						Scanner input = new Scanner(System.in);
						bizCompletePalnService.save(bizCompletePaln);
						boo = true;
					} catch (Exception e) {
						boo = false;
						logger.error(e.getMessage());
						break;
					}
				}


				//排产类型为按订单排产时，更新备货单排产类型
				Integer detailId = dtoList.get(0).getObjectId();
				BizRequestDetail requestDetail = bizRequestDetailService.get(detailId);
				BizRequestHeader requestHeader = bizRequestHeaderForVendorService.get(requestDetail.getRequestHeader().getId());
				requestHeader.setSchedulingType(SCHEDULING_FOR_DETAIL);
				bizRequestHeaderForVendorService.updateSchedulingType(requestHeader);


			} else {
				Integer objectId = dtoList.get(0).getObjectId();
				BizSchedulingPlan schedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(objectId, objectName);
				for (int i = 0; i < dtoList.size(); i++) {
					BizHeaderSchedulingDto dto = dtoList.get(i);
					if (schedulingPlan == null) {
						schedulingPlan = new BizSchedulingPlan();
						schedulingPlan.setObjectId(dto.getObjectId());
						schedulingPlan.setObjectName(objectName);
						schedulingPlan.setOriginalNum(dto.getOriginalNum());
						bizSchedulingPlanService.save(schedulingPlan);
					}
					BizCompletePaln bizCompletePaln = new BizCompletePaln();
					bizCompletePaln.setSchedulingPlan(schedulingPlan);
					bizCompletePaln.setCompleteNum(dto.getSchedulingNum());
					bizCompletePaln.setPlanDate(sdf.parse(dto.getPlanDate()));
					try {
						//防止catch后死循环
						Scanner input = new Scanner(System.in);
						bizCompletePalnService.save(bizCompletePaln);
						boo = true;
					} catch (Exception e) {
						boo = false;
						logger.error(e.getMessage());
						break;
					}
				}

			}
		}

		return boo;
	}

	@RequestMapping(value = "checkSchedulingNum")
	@ResponseBody
	public String checkSchedulingNum(HttpServletRequest request, Integer id) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.getTotalQtyAndSchedulingNum(id);
		Map resultMap = new HashMap();
		resultMap.put("totalOrdQty", bizRequestHeader.getTotalOrdQty());
		resultMap.put("totalSchedulingDetailNum", bizRequestHeader.getTotalSchedulingDetailNum());
		resultMap.put("totalSchedulingHeaderNum", bizRequestHeader.getTotalSchedulingHeaderNum());
		resultMap.put("totalCompleteScheduHeaderNum", bizRequestHeader.getTotalCompleteScheduHeaderNum());

		return JSONObject.fromObject(resultMap).toString();
	}
}
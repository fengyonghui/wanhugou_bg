/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.dto.BizHeaderSchedulingDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizSchedulingPlan;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.po.BizCompletePalnService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizSchedulingPlanService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.BizOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ImgEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购订单表Controller
 *
 * @author liuying
 * @version 2017-12-30
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoHeader")
public class BizPoHeaderController extends BaseController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BizPoHeaderController.class);

    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizPoDetailService bizPoDetailService;
    @Autowired
    private BizPlatformInfoService bizPlatformInfoService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private DictService dictService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizSchedulingPlanService bizSchedulingPlanService;
    @Autowired
    private BizCompletePalnService bizCompletePalnService;
    @Autowired
    private BizInvoiceService bizInvoiceService;

    public static final String VEND_IMG_TABLE_NAME = "biz_vend_info";
    public static final String PO_HEADER_TABLE_NAME = "biz_po_header";
    public static final String PO_DETAIL_TABLE_NAME = "biz_po_detail";
    public static final Integer SCHEDULING_FOR_HEADER = 0;
    public static final Integer SCHEDULING_FOR_DETAIL = 1;
    public static final String MARKETING_MANAGER = "marketing_manager";


    @ModelAttribute
    public BizPoHeader get(@RequestParam(required = false) Integer id) {
        BizPoHeader entity = null;
        if (id != null) {
            entity = bizPoHeaderService.get(id);

            if (entity.getCommonProcess() != null && entity.getCommonProcess().getId() != null) {
                List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
                bizPoHeaderService.getCommonProcessListFromDB(entity.getCommonProcess().getId(), commonProcessList);
                Collections.reverse(commonProcessList);
                entity.setCommonProcessList(commonProcessList);
            }

            BizPoDetail bizPoDetail = new BizPoDetail();
            bizPoDetail.setPoHeader(entity);
            List<BizPoDetail> poDetailList = bizPoDetailService.findList(bizPoDetail);
            List<BizPoDetail> poDetails = Lists.newArrayList();
            for (BizPoDetail poDetail : poDetailList) {
                BizSkuInfo bizSkuInfo = poDetail.getSkuInfo();
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(bizSkuInfo.getId()));
                poDetail.setSkuInfo(skuInfo);

//                BizSchedulingPlan bizSchedulingPlan = new BizSchedulingPlan();
//                bizSchedulingPlan.setBizPoDetail(poDetail);
//                List<BizSchedulingPlan> schedulingPlanList = bizSchedulingPlanService.findAllList(bizSchedulingPlan);
//                poDetail.setSchedulingPlanList(schedulingPlanList);
                BizPoDetail poDetailTemp = bizPoDetailService.getsumSchedulingNum(poDetail.getId());
                if (poDetailTemp != null){
                    poDetail.setSumSchedulingNum(poDetailTemp.getSumSchedulingNum());
                    poDetail.setSumCompleteNum(poDetailTemp.getSumCompleteNum());
                    poDetail.setSumCompleteDetailNum(poDetailTemp.getSumCompleteDetailNum());
                }

                poDetails.add(poDetail);
            }
            entity.setPoDetailList(poDetails);
            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
            bizPoOrderReq.setPoHeader(entity);
            List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            BizRequestDetail bizRequestDetail = new BizRequestDetail();
            Map<Integer, ArrayList<BizPoOrderReq>> map = new HashMap<>();
            Map<String, Integer> mapSource = new HashMap<>();
            for (BizPoOrderReq poOrderReq : poOrderReqList) {
                if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())) {
                    BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(poOrderReq.getSoId());
                    String numKey = bizOrderHeader.getOrderNum();
                    if (mapSource.containsKey(numKey)) {
                        int count = mapSource.get(numKey);
                        mapSource.remove(numKey);
                        mapSource.put(numKey, count + 1);
                    } else {
                        mapSource.put(numKey, 1);
                    }
                    poOrderReq.setOrderHeader(bizOrderHeader);
                    bizOrderDetail.setOrderHeader(bizOrderHeader);
                    bizOrderDetail.setLineNo(poOrderReq.getSoLineNo());
                    List<BizOrderDetail> bizOrderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                    if (bizOrderDetailList != null && bizOrderDetailList.size() != 0) {
                        BizOrderDetail orderDetail = bizOrderDetailList.get(0);
                        Integer key = orderDetail.getSkuInfo().getId();
                        if (map.containsKey(key)) {
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = map.get(key);

                            map.remove(key);

                            String orderNumStr = bizOrderHeader.getOrderNum();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(orderDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        } else {
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = Lists.newArrayList();
                            String orderNumStr = bizOrderHeader.getOrderNum();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(orderDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        }

                    }
                } else if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())) {
                    BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(poOrderReq.getSoId());
                    String reqKey = bizRequestHeader.getReqNo();
                    if (mapSource.containsKey(reqKey)) {
                        int count = mapSource.get(reqKey);
                        mapSource.remove(reqKey);
                        mapSource.put(reqKey, count + 1);
                    } else {
                        mapSource.put(reqKey, 1);
                    }
                    poOrderReq.setRequestHeader(bizRequestHeader);
                    bizRequestDetail.setRequestHeader(bizRequestHeader);
                    bizRequestDetail.setLineNo(poOrderReq.getSoLineNo());
                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                    if (requestDetailList != null && requestDetailList.size() != 0) {
                        BizRequestDetail requestDetail = requestDetailList.get(0);
                        Integer key = requestDetail.getSkuInfo().getId();
                        if (map.containsKey(key)) {

                            ArrayList<BizPoOrderReq> bizPoOrderReqList = map.get(key);
                            map.remove(key);
                            poOrderReq.setOrderNumStr(bizRequestHeader.getReqNo());
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(requestDetail.getSkuInfo().getId(), bizPoOrderReqList);

                        } else {
                            String orderNumStr = bizRequestHeader.getReqNo();
                            poOrderReq.setOrderNumStr(orderNumStr);
                            ArrayList<BizPoOrderReq> bizPoOrderReqList = Lists.newArrayList();
                            bizPoOrderReqList.add(poOrderReq);
                            map.put(requestDetail.getSkuInfo().getId(), bizPoOrderReqList);
                        }
                    }
                }

                //	poOrderReqs.add(mapSource);
            }
            entity.setOrderSourceMap(mapSource);


            entity.setOrderNumMap(map);
        }
        if (entity == null) {
            entity = new BizPoHeader();
        }
        return entity;
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            bizPoHeader.setVendOffice(user.getCompany());
        }
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String filteringDate = ConfigGeneral.SYSTEM_CONFIG.get().getFilteringDate();
            Date date = df.parse(filteringDate);
            bizPoHeader.setFilteringDate(date);
        } catch (ParseException e) {
            LOGGER.error("日期解析失败",e);
        }
        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader);
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }

        List<com.wanhutong.backend.modules.config.parse.Process> processList = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessList();

        model.addAttribute("roleSet", roleSet);
        model.addAttribute("processList", processList);
        model.addAttribute("roleEnNameSet", roleEnNameSet);
        model.addAttribute("page", page);
        model.addAttribute("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());
        return "modules/biz/po/bizPoHeaderList";
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = {"listV2"})
    public String listV2(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            bizPoHeader.setVendOffice(user.getCompany());
        }
//        try {
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String filteringDate = ConfigGeneral.SYSTEM_CONFIG.get().getFilteringDate();
//            Date date = df.parse(filteringDate);
//            bizPoHeader.setFilteringDate(date);
//        } catch (ParseException e) {
//            LOGGER.error("日期解析失败",e);
//        }
//
//        String filteringDate = ConfigGeneral.SYSTEM_CONFIG.get().getFilteringDate();
//        try {
//            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filteringDate);
//            bizPoHeader.setFilteringDate(parse);
//        } catch (ParseException e) {
//            LOGGER.error("po list parse data error", e);
//        }

        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();

        if (StringUtils.isNotBlank(bizPoHeader.getProcessTypeStr())) {
            List<Process> processList = purchaseOrderProcessConfig.getNameProcessMap().get(bizPoHeader.getProcessTypeStr());
            List<String> transform = processList.stream().map(process -> String.valueOf(process.getCode())).collect(Collectors.toList());
            bizPoHeader.setProcessTypeList(transform);
        }

        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader);
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }

        List<com.wanhutong.backend.modules.config.parse.Process> processList = purchaseOrderProcessConfig.getShowFilterProcessList();

        Set<String> processSet = Sets.newHashSet();
        for (com.wanhutong.backend.modules.config.parse.Process process : processList) {
            processSet.add(process.getName());
        }

        model.addAttribute("roleSet", roleSet);
        model.addAttribute("processList", processSet);
        model.addAttribute("roleEnNameSet", roleEnNameSet);
        model.addAttribute("page", page);
        model.addAttribute("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());
        return "modules/biz/po/bizPoHeaderListV2";
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = {"listV2Data4Mobile"})
    @ResponseBody
    public String listV2Data4Mobile(BizPoHeader bizPoHeader, HttpServletRequest request, HttpServletResponse response) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            bizPoHeader.setVendOffice(user.getCompany());
        }

        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();

        if (StringUtils.isNotBlank(bizPoHeader.getProcessTypeStr())) {
            List<Process> processList = purchaseOrderProcessConfig.getNameProcessMap().get(bizPoHeader.getProcessTypeStr());
            List<String> transform = processList.stream().map(process -> String.valueOf(process.getCode())).collect(Collectors.toList());
            bizPoHeader.setProcessTypeList(transform);
        }

        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<BizPoHeader>(request, response), bizPoHeader);
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }

        List<com.wanhutong.backend.modules.config.parse.Process> processList = purchaseOrderProcessConfig.getShowFilterProcessList();

        Set<String> processSet = Sets.newHashSet();
        for (com.wanhutong.backend.modules.config.parse.Process process : processList) {
            processSet.add(process.getName());
        }

        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("roleSet", roleSet);
        resultMap.put("processList", processList);
        resultMap.put("roleEnNameSet", roleEnNameSet);
        resultMap.put("page", page);
        resultMap.put("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());

        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = {"listData4Mobile"})
    @ResponseBody
    public String listData4Mobile(BizPoHeader bizPoHeader, @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo, HttpServletRequest request, HttpServletResponse response) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.SUPPLY_CHAIN.getState());
        if (roleList.contains(role)) {
            bizPoHeader.setVendOffice(user.getCompany());
        }
        Page<BizPoHeader> bizPoHeaderPage = new Page<>(request, response);
        bizPoHeaderPage.setPageNo(pageNo);
        Page<BizPoHeader> page = bizPoHeaderService.findPage(bizPoHeaderPage, bizPoHeader);
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }

        List<com.wanhutong.backend.modules.config.parse.Process> processList = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessList();

        List<Map<String, Object>> resultList = Lists.newArrayList();
        List<BizPoHeader> list = page.getList();
        page.setList(null);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(o -> {
                Map<String, Object> data = Maps.newHashMap();
                data.put("id", o.getId());
                data.put("orderNum", o.getOrderNum() == null ? StringUtils.EMPTY : o.getOrderNum());
                data.put("vendOffice", o.getVendOffice().getName() == null ? StringUtils.EMPTY : o.getVendOffice().getName());
                data.put("process",  o.getCommonProcess() == null ? StringUtils.EMPTY : o.getCommonProcess());
                data.put("currentPaymentId",  o.getCurrentPaymentId() == null ? StringUtils.EMPTY : o.getCurrentPaymentId());
                data.put("bizStatus",  DictUtils.getDictLabel(String.valueOf(o.getBizStatus()), "biz_po_status", "未知类型"));
                data.put("payTotal",  o.getPayTotal());
                data.put("totalDetail",  o.getTotalDetail());
                data.put("totalExp",  o.getTotalDetail());
                resultList.add(data);
            });
        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("roleSet", roleSet);
        resultMap.put("processList", processList);
        resultMap.put("roleEnNameSet", roleEnNameSet);
        resultMap.put("page", page);
        resultMap.put("resultList", resultList);
        resultMap.put("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = "form")
    public String form(BizPoHeader bizPoHeader, Model model, String prewStatus, String type) {
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
            Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
            if ("8".equals(office.getType())) {
                bizPoHeader.setDeliveryStatus(0);
            } else {
                bizPoHeader.setDeliveryStatus(1);
            }
        }

        if ("audit".equalsIgnoreCase(type) && bizPoHeader.getCommonProcess() != null) {
            com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizPoHeader.getCommonProcess().getType()));
            model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
        }

        if (bizPoHeader.getVendOffice() != null && bizPoHeader.getVendOffice().getBizVendInfo() != null) {
            CommonImg compactImg = new CommonImg();
            compactImg.setImgType(ImgEnum.VEND_COMPACT.getCode());
            compactImg.setObjectId(bizPoHeader.getVendOffice().getId());
            compactImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> compactImgList = commonImgService.findList(compactImg);
            model.addAttribute("compactImgList", compactImgList);

            CommonImg identityCardImg = new CommonImg();
            identityCardImg.setImgType(ImgEnum.VEND_IDENTITY_CARD.getCode());
            identityCardImg.setObjectId(bizPoHeader.getVendOffice().getId());
            identityCardImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> identityCardImgList = commonImgService.findList(identityCardImg);
            model.addAttribute("identityCardImgList", identityCardImgList);

        }

        List<Role> roleList = UserUtils.getUser().getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }

        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        bizPoOrderReq.setPoHeader(bizPoHeader);
        List<BizPoOrderReq> bizPoOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
        if (CollectionUtils.isNotEmpty(bizPoOrderReqs)) {
            bizPoOrderReq = bizPoOrderReqs.get(0);
        }
        BizOrderHeader bizOrderHeader = null;
        if (bizPoOrderReq != null) {
            bizOrderHeader = bizOrderHeaderService.get(bizPoOrderReq.getSoId());
        }

        if (bizOrderHeader != null && 6 == bizOrderHeader.getOrderType()) {
            CommonImg commonImg = new CommonImg();
            commonImg.setObjectId(bizOrderHeader.getId());
            commonImg.setObjectName(ImgEnum.ORDER_SKU_PHOTO.getTableName());
            commonImg.setImgType(ImgEnum.ORDER_SKU_PHOTO.getCode());
            List<CommonImg> photoOrderImgList = commonImgService.findList(commonImg);
            model.addAttribute("photoOrderImgList", photoOrderImgList);
        }

        model.addAttribute("roleSet", roleSet);
        model.addAttribute("bizPoHeader", bizPoHeader);
        model.addAttribute("bizOrderHeader", bizOrderHeader);
        model.addAttribute("type", type);
        model.addAttribute("prewStatus", prewStatus);
        return "modules/biz/po/bizPoHeaderForm";
    }

    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = "form4Mobile")
    @ResponseBody
    public String form4Mobile(HttpServletRequest request, BizPoHeader bizPoHeader, Model model, String prewStatus, String type) {
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
            Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
            if ("8".equals(office.getType())) {
                bizPoHeader.setDeliveryStatus(0);
            } else {
                bizPoHeader.setDeliveryStatus(1);
            }
        }

        if ("audit".equalsIgnoreCase(type) && bizPoHeader.getCommonProcess() != null) {
            com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizPoHeader.getCommonProcess().getType()));
            model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
        }

        if (bizPoHeader.getVendOffice() != null && bizPoHeader.getVendOffice().getBizVendInfo() != null) {
            CommonImg compactImg = new CommonImg();
            compactImg.setImgType(ImgEnum.VEND_COMPACT.getCode());
            compactImg.setObjectId(bizPoHeader.getVendOffice().getId());
            compactImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> compactImgList = commonImgService.findList(compactImg);
            model.addAttribute("compactImgList", compactImgList);

            CommonImg identityCardImg = new CommonImg();
            identityCardImg.setImgType(ImgEnum.VEND_IDENTITY_CARD.getCode());
            identityCardImg.setObjectId(bizPoHeader.getVendOffice().getId());
            identityCardImg.setObjectName(VEND_IMG_TABLE_NAME);
            List<CommonImg> identityCardImgList = commonImgService.findList(identityCardImg);
            model.addAttribute("identityCardImgList", identityCardImgList);

        }

        List<Role> roleList = UserUtils.getUser().getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }

        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        bizPoOrderReq.setPoHeader(bizPoHeader);
        List<BizPoOrderReq> bizPoOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
        if (CollectionUtils.isNotEmpty(bizPoOrderReqs)) {
            bizPoOrderReq = bizPoOrderReqs.get(0);
        }
        BizOrderHeader bizOrderHeader = null;
        if (bizPoOrderReq != null) {
            bizOrderHeader = bizOrderHeaderService.get(bizPoOrderReq.getSoId());
        }

        if (bizOrderHeader != null && 6 == bizOrderHeader.getOrderType()) {
            CommonImg commonImg = new CommonImg();
            commonImg.setObjectId(bizOrderHeader.getId());
            commonImg.setObjectName(ImgEnum.ORDER_SKU_PHOTO.getTableName());
            commonImg.setImgType(ImgEnum.ORDER_SKU_PHOTO.getCode());
            List<CommonImg> photoOrderImgList = commonImgService.findList(commonImg);
            model.addAttribute("photoOrderImgList", photoOrderImgList);
        }


        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("roleSet", roleSet);

        Map<String, Object> bizPoHeaderMap = Maps.newHashMap();
        bizPoHeaderMap.put("id", bizPoHeader.getId());
        bizPoHeaderMap.put("orderNumber", bizPoHeader.getOrderNum());
        bizPoHeaderMap.put("totalDetail", bizPoHeader.getTotalDetail());
        bizPoHeaderMap.put("total", bizPoHeader.getTotalDetail() + bizPoHeader.getTotalExp() + bizPoHeader.getFreight());
        bizPoHeaderMap.put("lastPayDate", bizPoHeader.getLastPayDate());
        bizPoHeaderMap.put("deliveryStatus", bizPoHeader.getDeliveryStatus());
        bizPoHeaderMap.put("deliveryOffice", bizPoHeader.getDeliveryOffice());
        bizPoHeaderMap.put("remark", bizPoHeader.getRemark());
        bizPoHeaderMap.put("bizStatus", DictUtils.getDictLabel(String.valueOf(bizPoHeader.getBizStatus()), "biz_po_status", "未知类型"));
        bizPoHeaderMap.put("vendOffice", bizPoHeader.getVendOffice());
        bizPoHeaderMap.put("process", bizPoHeader.getCommonProcess());
        bizPoHeaderMap.put("commonProcessList", bizPoHeader.getCommonProcessList());

        Map<String, Object> bizOrderHeaderMap = Maps.newHashMap();
        bizOrderHeaderMap.put("id",bizOrderHeader == null ? StringUtils.EMPTY : bizOrderHeader.getId());
        bizOrderHeaderMap.put("orderNumber", bizOrderHeader == null ? StringUtils.EMPTY :bizOrderHeader.getOrderNum());

        resultMap.put("bizPoHeader", bizPoHeaderMap);
        resultMap.put("bizOrderHeader", bizOrderHeaderMap);
        resultMap.put("type", type);
        resultMap.put("prewStatus", prewStatus);
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "audit")
    @ResponseBody
    public String audit(HttpServletRequest request, int id, String currentType, int auditType, String description) {
        Pair<Boolean, String> result = bizPoHeaderService.auditPo(id, currentType, auditType, description);
        if (result.getLeft()) {
            return JsonUtil.generateData(result, request.getParameter("callback"));
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizpopaymentorder:bizPoPaymentOrder:audit")
    @RequestMapping(value = "auditPay")
    @ResponseBody
    public String auditPay(HttpServletRequest request, int id, String currentType, int auditType, String description, BigDecimal money) {
        Pair<Boolean, String> result = bizPoHeaderService.auditPay(id, currentType, auditType, description, money);
        if (result.getLeft()) {
            return JsonUtil.generateData(result, request.getParameter("callback"));
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
    }


    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "save")
    public String save(HttpServletResponse response, HttpServletRequest request,
                       BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes,
                       String prewStatus, String type, String version) {

        if ("audit".equalsIgnoreCase(type)) {
            String msg = bizPoHeaderService.genPaymentOrder(bizPoHeader).getRight();
            addMessage(redirectAttributes, msg);
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
        }

        if (!beanValidator(model, bizPoHeader)) {
            return form(bizPoHeader, model, prewStatus, null);
        }

        Set<Integer> poIdSet = bizPoHeaderService.findPrewPoHeader(bizPoHeader);
        if (poIdSet.size() == 1) {
            addMessage(redirectAttributes, "prew".equals(prewStatus) ? "采购订单预览信息" : "保存采购订单成功");
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/form/?id=" + poIdSet.iterator().next() + "&prewStatus=" + prewStatus;
        }
        int deOfifceId = 0;
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null) {
            deOfifceId = bizPoHeader.getDeliveryOffice().getId();
        }
        String poNo = "0";
        bizPoHeader.setOrderNum(poNo);
        bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
        bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
        Integer id = bizPoHeader.getId();
        bizPoHeaderService.save(bizPoHeader);
        if (id == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }
        if (bizPoHeader.getOrderNum() == null || "0".equals(bizPoHeader.getOrderNum())) {
            poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, deOfifceId, bizPoHeader.getVendOffice().getId(), bizPoHeader.getId());
            bizPoHeader.setOrderNum(poNo);
            bizPoHeaderService.savePoHeader(bizPoHeader);
        }

        addMessage(redirectAttributes, "prew".equals(prewStatus) ? "采购订单预览信息" : "保存采购订单成功");

        if ("mobile".equalsIgnoreCase(version)) {
            return renderString(response, JsonUtil.generateData("操作成功", request.getParameter("callback")), "application/json");
        }

        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/form/?id=" + bizPoHeader.getId() + "&prewStatus=" + prewStatus;
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "saveForPhotoOrder")
    @ResponseBody
    public String saveForPhotoOrder(HttpServletRequest request,
                                    Integer orderHeaderId,
                                    int deliveryStatus,
                                    Date lastPayDate,
                                    Model model, RedirectAttributes redirectAttributes, String type) {

        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderHeaderId);
        BizOrderAddress bizOrderAddress = bizOrderAddressService.getOrderAddrByOrderId(bizOrderHeader.getId());

        Office vendor = officeService.get(bizOrderHeader.getSellersId());
        Office customer = officeService.get(bizOrderHeader.getCustomer().getId());

        BizPoHeader bizPoHeader = new BizPoHeader();
        String poNo = "0";
        bizPoHeader.setOrderNum(poNo);
        bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
        bizPoHeader.setVendOffice(new Office());
        bizPoHeader.setIsPrew(0);
        bizPoHeader.setLastPayDate(lastPayDate);
        bizPoHeader.setVendOffice(vendor);

        bizPoHeader.setTotalDetail(bizOrderHeader.getTotalDetail());

        Integer id = bizPoHeader.getId();
        bizPoHeaderService.save(bizPoHeader);
        if (id == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }

        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        bizPoOrderReq.setId(null);
        bizPoOrderReq.setPoHeader(bizPoHeader);
        bizPoOrderReq.setPoLineNo(0);
        bizPoOrderReq.setOrderHeader(bizOrderHeader);
        bizPoOrderReq.setRequestHeader(null);
        bizPoOrderReq.setSoLineNo(0);
        bizPoOrderReq.setSoQty(0);
        bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
        bizPoOrderReqService.save(bizPoOrderReq);

        if (bizPoHeader.getOrderNum() == null || "0".equals(bizPoHeader.getOrderNum())) {
            poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, deliveryStatus == 0 ? customer.getId() : vendor.getId(), bizPoHeader.getVendOffice().getId(), bizPoHeader.getId());
            bizPoHeader.setOrderNum(poNo);
            bizPoHeaderService.save(bizPoHeader);
        }

        bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
        bizOrderHeaderService.save(bizOrderHeader);
        bizOrderStatusService.saveOrderStatus(bizOrderHeader);

        bizPoHeaderService.sendSmsForDeliver(bizOrderHeader.getOrderNum(),"");
        bizPoHeaderService.sendMailForDeliver(bizOrderHeader.getOrderNum(),"");

        return "操作成功";
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "savePoHeader")
    public String savePoHeader(BizPoHeader bizPoHeader, Model model, RedirectAttributes redirectAttributes, String prewStatus, String type) {
        if ("createPay".equalsIgnoreCase(type)) {
            String msg = bizPoHeaderService.genPaymentOrder(bizPoHeader).getRight();
            addMessage(redirectAttributes, msg);
            return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
        }

        if (!beanValidator(model, bizPoHeader)) {
            return form(bizPoHeader, model, prewStatus, null);
        }
        bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
        bizPoHeaderService.savePoHeader(bizPoHeader);

        addMessage(redirectAttributes, "保存采购订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "createPay4Mobile")
    @ResponseBody
    public String createPay4Mobile(HttpServletRequest request, int id, BigDecimal planPay, Date deadline) {
        Pair<Boolean, String> result = bizPoHeaderService.genPaymentOrder(id, planPay, deadline);
        if (result.getLeft()) {
            return JsonUtil.generateData(result, request.getParameter("callback"));
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "delete")
    public String delete(BizPoHeader bizPoHeader, RedirectAttributes redirectAttributes) {
        bizPoHeaderService.delete(bizPoHeader);
        addMessage(redirectAttributes, "删除采购订单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoHeader/?repage";
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "payOrder")
    @ResponseBody
    public String payOrder(RedirectAttributes redirectAttributes, Integer poHeaderId,@RequestParam(required = false) Integer reqHeaderId, Integer paymentOrderId, BigDecimal payTotal, String img, String paymentRemark) {
        return bizPoHeaderService.payOrder(poHeaderId,reqHeaderId, paymentOrderId, payTotal, img, paymentRemark);
    }

    @RequiresPermissions("biz:po:bizPoHeader:audit")
    @RequestMapping(value = "startAudit")
    @ResponseBody
    public String startAudit(HttpServletRequest request, int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, @RequestParam(defaultValue = "1") Integer auditType, String desc, String action) {
        String mark = "oldAudit";
        if (StringUtils.isNotBlank(action)) {
            mark = "startAuditAfterReject";
        }
        Pair<Boolean, String> result = bizPoHeaderService.startAudit(id, prew, prewPayTotal, prewPayDeadline, auditType, desc, mark);
        if (result.getLeft()) {
            return JsonUtil.generateData(result, request.getParameter("callback"));
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, result.getRight(), request.getParameter("callback"));
    }

    /**
     * 采购单导出
     *
     * @param bizPoHeader
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("biz:po:bizPoHeader:view")
    @RequestMapping(value = "poHeaderExport")
    public String poHeaderExport(BizPoHeader bizPoHeader,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        String fromPage = bizPoHeader.getFromPage();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileNmaeTitle = "采购单";
            if ("listV2".equals(fromPage)) {
                fileNmaeTitle = "订单支出信息";
            }
            String fileName = fileNmaeTitle + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizPoHeader> list = bizPoHeaderService.findList(bizPoHeader);
            //1采购单整体数据
            List<List<String>> data = new ArrayList<List<String>>();
            if(list != null && !list.isEmpty()){
                for(BizPoHeader poHeader:list){
//                    List<BizPoDetail> poDetailList=Lists.newArrayList();
                    poHeader = this.get(poHeader.getId());
                    BizPoDetail bizPoDetail=new BizPoDetail();
                    bizPoDetail.setPoHeader(poHeader);
                    List<BizPoDetail> poDetailList = bizPoDetailService.findList(bizPoDetail);
                    if(poDetailList != null && !poDetailList.isEmpty()){
                        for(BizPoDetail poDetail:poDetailList){
                            BizSkuInfo skuInfo=bizSkuInfoService.findListProd(bizSkuInfoService.get(poDetail.getSkuInfo().getId()));
                            poDetail.setSkuInfo(skuInfo);
                            poDetail.setPoHeader(poHeader);
                        }
                    }
                    Set<String> orderNumSet = poHeader.getOrderSourceMap().keySet();
                    String orderNum = "";
                    for (String value : orderNumSet) {
                        orderNum = value;
                    }
                    Integer orderType = null;
                    BizOrderHeader orderHeader = bizOrderHeaderService.getByOrderNum(orderNum);
                    if (orderHeader != null){
                        orderType = orderHeader.getOrderType();
                    }
                    if (BizOrderTypeEnum.PHOTO_ORDER.getState().equals(orderType)){
                        List<String> headerListData = new ArrayList();
                        //采购单遍历
                        if ("listV2".equals(fromPage)) {
                            List<String> orderNumList = new ArrayList<>(poHeader.getOrderSourceMap().keySet());
                            if (CollectionUtils.isNotEmpty(orderNumList)) {
                                headerListData.add(orderNumList.get(0));
                            }
                        } else {
                            headerListData.add(poHeader.getOrderNum());
                        }
                        //供应商
                        headerListData.add(poHeader.getVendOffice().getName());
                        //采购总价
                        headerListData.add(String.valueOf(poHeader.getTotalDetail()));
                        //交易费用
                        headerListData.add(String.valueOf(poHeader.getTotalExp()));
                        //应付金额
                        headerListData.add(String.valueOf(poHeader.getTotalDetail() + poHeader.getTotalExp()));
                        //累计支付金额
                        headerListData.add(String.valueOf(poHeader.getPayTotal()));
                        //支付比例
                        Double EP = 0.001;
                        if ((poHeader.getTotalDetail() + poHeader.getTotalExp()) > EP) {
                            headerListData.add(String.valueOf(poHeader.getPayTotal().multiply(new BigDecimal(100)).divide(new BigDecimal(poHeader.getTotalDetail() + poHeader.getTotalExp()), 2, RoundingMode.HALF_UP)) + "%");
                        } else {
                            headerListData.add("0.00%");
                        }
                        //订单状态
                        Dict dict = new Dict();
                        dict.setType("biz_po_status");
                        List<Dict> dictList = dictService.findList(dict);
                        String str = "";
                        for (Dict bizDict : dictList) {
                            if (bizDict.getValue().equals(String.valueOf(poHeader.getBizStatus()))) {
                                //业务状态
                                str = bizDict.getLabel();
//                                headerListData.add(String.valueOf(bizDict.getLabel()));
                                break;
                            }
                        }
                        headerListData.add(str);
//                    //采购单来源
//                    headerListData.add(String.valueOf());
                        //审核状态
                        CommonProcessEntity commonProcessEntity = commonProcessService.get(poHeader.getProcessId());
                        if (commonProcessEntity != null) {
                            headerListData.add(commonProcessEntity.getPurchaseOrderProcess().getName() == null ? "当前无审批流程" : commonProcessEntity.getPurchaseOrderProcess().getName());
                        } else {
                            headerListData.add("当前无审批流程");
                        }
                        //创建时间
                        headerListData.add(String.valueOf(sdf.format(poHeader.getCreateDate())));
                        //所属单号
                        headerListData.add(orderNum);
                        //商品名称
                        headerListData.add("");
                        //商品货号
                        headerListData.add("");
                        //采购数量
                        headerListData.add("");
                        //已供货数量
                        headerListData.add("");
                        //结算价
                        headerListData.add("");
                        data.add(headerListData);
                    } else {
                        for (BizPoDetail poDetail:poDetailList) {
                            List<String> headerListData = new ArrayList();
                            //采购单遍历
                            //headerListData.add(poHeader.getOrderNum());
                            if ("listV2".equals(fromPage)) {
                                List<String> orderNumList = new ArrayList<>(poHeader.getOrderSourceMap().keySet());
                                if (CollectionUtils.isNotEmpty(orderNumList)) {
                                    headerListData.add(orderNumList.get(0));
                                }
                            } else {
                                headerListData.add(poHeader.getOrderNum());
                            }
                            //供应商
                            headerListData.add(poHeader.getVendOffice().getName());
                            //采购总价
                            headerListData.add(String.valueOf(poHeader.getTotalDetail()));
                            //交易费用
                            headerListData.add(String.valueOf(poHeader.getTotalExp()));
                            //应付金额
                            headerListData.add(String.valueOf(poHeader.getTotalDetail() + poHeader.getTotalExp()));
                            //累计支付金额
                            headerListData.add(String.valueOf(poHeader.getPayTotal()));
                            //支付比例
                            Double EP = 0.001;
                            if ((poHeader.getTotalDetail() + poHeader.getTotalExp()) > EP) {
                                headerListData.add(String.valueOf(poHeader.getPayTotal().multiply(new BigDecimal(100)).divide(new BigDecimal(poHeader.getTotalDetail() + poHeader.getTotalExp()), 2, RoundingMode.HALF_UP)) + "%");
                            } else {
                                headerListData.add("0.00%");
                            }
                            //订单状态
                            Dict dict = new Dict();
                            dict.setType("biz_po_status");
                            List<Dict> dictList = dictService.findList(dict);
                            String str = "";
                            for (Dict bizDict : dictList) {
                                if (bizDict.getValue().equals(String.valueOf(poHeader.getBizStatus()))) {
                                    //业务状态
                                    str = bizDict.getLabel();
//                                headerListData.add(String.valueOf(bizDict.getLabel()));
                                    break;
                                }
                            }
                            headerListData.add(str);
//                    //采购单来源
//                    headerListData.add(String.valueOf());
                            //审核状态
                            CommonProcessEntity commonProcessEntity = commonProcessService.get(poHeader.getProcessId());
                            if (commonProcessEntity != null) {
                                headerListData.add(commonProcessEntity.getPurchaseOrderProcess().getName()==null?"当前无审批流程":commonProcessEntity.getPurchaseOrderProcess().getName());
                            }else {
                                headerListData.add("当前无审批流程");
                            }
                            //创建时间
                            headerListData.add(String.valueOf(sdf.format(poHeader.getCreateDate())));
                            //所属单号
                            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                            bizPoOrderReq.setPoHeader(poHeader);
                            bizPoOrderReq.setPoLineNo(poDetail.getLineNo());
                            List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                            BizOrderHeader bizOrderHeader = new BizOrderHeader();
                            BizRequestHeader bizRequestHeader = new BizRequestHeader();
                            StringBuffer nums = new StringBuffer();
                            for (BizPoOrderReq poOrderReq : poOrderReqList) {
                                if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())) {
                                    bizOrderHeader = bizOrderHeaderService.get(poOrderReq.getSoId());
                                    BizOrderDetail bizOrderDetail = new BizOrderDetail();
                                    bizOrderDetail.setOrderHeader(bizOrderHeader);
                                    bizOrderDetail.setLineNo(poOrderReq.getSoLineNo());
                                    List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                                    if (orderDetailList != null && !orderDetailList.isEmpty()) {
                                        BizOrderDetail orderDetail = orderDetailList.get(0);
                                        if (orderDetail.getSkuInfo() != null) {
                                            if (orderDetail.getSkuInfo().getId().equals(poDetail.getSkuInfo().getId())) {
                                                nums.append(bizOrderHeader.getOrderNum()).append(";");
                                            }
                                        }
                                    }
                                }
                                if (poOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())) {
                                    bizRequestHeader = bizRequestHeaderService.get(poOrderReq.getSoId());
                                    BizRequestDetail requestDetail = new BizRequestDetail();
                                    requestDetail.setRequestHeader(bizRequestHeader);
                                    requestDetail.setLineNo(poOrderReq.getSoLineNo());
                                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(requestDetail);
                                    if (requestDetailList != null && !requestDetailList.isEmpty()) {
                                        BizRequestDetail bizRequestDetail = requestDetailList.get(0);
                                        if (bizRequestDetail.getSkuInfo() != null && bizRequestDetail.getSkuInfo().getId().equals(poDetail.getSkuInfo().getId())) {
                                            nums.append(bizRequestHeader.getReqNo()).append(";");
                                        }
                                    }
                                }
                            }
                            if (!"".equals(nums.toString())) {
                                headerListData.add(nums.toString());
                            }else {
                                headerListData.add("");
                            }
                            //商品名称
                            headerListData.add(String.valueOf(poDetail.getSkuInfo().getName()));
                            //商品货号
                            headerListData.add(String.valueOf(poDetail.getSkuInfo().getItemNo()));
                            //采购数量
                            headerListData.add(String.valueOf(poDetail.getOrdQty()));
                            //已供货数量
                            headerListData.add(String.valueOf(poDetail.getSendQty()));
                            //结算价
                            headerListData.add(String.valueOf(poDetail.getSkuInfo().getBuyPrice()));
                            data.add(headerListData);
                        }
                    }
                }
            }
            String orderTitle = "采购单号";
            String sheetName = "采购单数据";
            if ("listV2".equals(fromPage)) {
                orderTitle = "订单/备货单号";
                sheetName = "订单支出信息数据";
            }
            String[] headers = {orderTitle, "供应商", "采购总价", "交易费用","应付金额", "累计支付金额", "支付比例","订单状态","审核状态","创建时间","所属单号","商品名称","商品货号","采购数量","已供货数量","结算价"};
            ExportExcelUtils eeu = new ExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, sheetName, headers, data, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        }catch (Exception e){
            logger.error("可能没有采购单详情",e);
            addMessage(redirectAttributes, "导出采购单数据失败！失败信息：" + e.getMessage());
        }
        String toPage = "redirect:" + adminPath + "/biz/po/bizPoHeader/list";
        if ("listV2".equals(fromPage)) {
            toPage = "redirect:" + adminPath + "/biz/po/bizPoHeader/listV2";
        }
        return toPage;

    }


    @RequiresPermissions("biz:po:bizPoHeader:edit")
    @RequestMapping(value = "cancel")
    @ResponseBody
    public String cancel(int id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        bizPoHeaderService.updateBizStatus(id, BizPoHeader.BizStatus.CANCEL);
        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), id);
        return "取消采购订单成功";
    }


    @RequestMapping(value = "scheduling")
    public String scheduling(HttpServletRequest request, BizPoHeader bizPoHeader, Model model, String prewStatus, String type) {
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
            Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
            if ("8".equals(office.getType())) {
                bizPoHeader.setDeliveryStatus(0);
            } else {
                bizPoHeader.setDeliveryStatus(1);
            }
        }

        //按订单排产时，获取排产记录
        Integer schedulingType = bizPoHeader.getSchedulingType();
        Boolean detailHeaderFlg = false;
        List<BizCompletePaln> bizCompletePalns = new ArrayList<BizCompletePaln>();
        if (SCHEDULING_FOR_HEADER.equals(schedulingType)) {
            BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(bizPoHeader.getId(), PO_HEADER_TABLE_NAME);
            if (bizSchedulingPlan != null) {
                bizCompletePalns = bizSchedulingPlan.getCompletePalnList();
            }
            if (bizSchedulingPlan != null) {
                detailHeaderFlg = true;
            }
        }
        model.addAttribute("detailHeaderFlg", detailHeaderFlg);
        model.addAttribute("bizCompletePalns", bizCompletePalns);

        List<Integer> poDetailIdList = Lists.newArrayList();
        Boolean detailSchedulingFlg = false;
        List<BizPoDetail> bizPoDetailList = bizPoHeader.getPoDetailList();
        List<BizPoDetail> poDetailList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(bizPoDetailList)) {
            for (BizPoDetail bizpodetail : bizPoDetailList) {
                //排产类型为按商品排产时，获取排产记录
                if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
                    BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(bizpodetail.getId(), PO_DETAIL_TABLE_NAME);
                    if (bizSchedulingPlan != null) {
                        detailSchedulingFlg = true;
                    }
                    bizpodetail.setBizSchedulingPlan(bizSchedulingPlan);
                }
                poDetailList.add(bizpodetail);
                poDetailIdList.add(bizpodetail.getId());
            }
        }
        bizPoHeader.setPoDetailList(poDetailList);

        model.addAttribute("poDetailIdListJson", poDetailIdList);
        model.addAttribute("detailHeaderFlg", detailHeaderFlg);
        model.addAttribute("detailSchedulingFlg", detailSchedulingFlg);
        model.addAttribute("bizPoHeader", bizPoHeader);
        String forward = request.getParameter("forward");
        String forwardPage = "";
        if ("confirmScheduling".equals(forward)) {
            forwardPage = "modules/biz/po/bizPoHeaderCompleteScheduling";
        } else {
            forwardPage = "modules/biz/po/bizPoHeaderScheduling";
        }
        //判断当前用户是否为供应商
        Boolean roleFlag = false;
        List<Role> roleList = UserUtils.getUser().getRoleList();
        if (roleList != null) {
            for (Role role : roleList) {
                String roleName = role.getEnname();
                if (RoleEnNameEnum.SUPPLY_CHAIN.getDesc().equals(roleName)) {
                    roleFlag = true;
                    break;
                }
            }
        }
        model.addAttribute("roleFlag", roleFlag);
        return forwardPage;
    }

    @RequestMapping(value = "scheduling4Mobile")
    @ResponseBody
    public String scheduling4Mobile(HttpServletRequest request, BizPoHeader bizPoHeader, Model model, String prewStatus, String type) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
            Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
            if ("8".equals(office.getType())) {
                bizPoHeader.setDeliveryStatus(0);
            } else {
                bizPoHeader.setDeliveryStatus(1);
            }
        }

        //按订单排产时，获取排产记录
        Integer schedulingType = bizPoHeader.getSchedulingType();
        Boolean detailHeaderFlg = false;
        List<BizCompletePaln> bizCompletePalns = new ArrayList<BizCompletePaln>();
        if (SCHEDULING_FOR_HEADER.equals(schedulingType)) {
            BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(bizPoHeader.getId(), PO_HEADER_TABLE_NAME);
            if (bizSchedulingPlan != null) {
                bizCompletePalns = bizSchedulingPlan.getCompletePalnList();
            }
            if (bizSchedulingPlan != null) {
                detailHeaderFlg = true;
            }
        }
        model.addAttribute("detailHeaderFlg", detailHeaderFlg);
        model.addAttribute("bizCompletePalns", bizCompletePalns);
        resultMap.put("detailHeaderFlg", detailHeaderFlg);
        resultMap.put("bizCompletePalns", bizCompletePalns);

        List<Integer> poDetailIdList = Lists.newArrayList();
        Boolean detailSchedulingFlg = false;
        List<BizPoDetail> bizPoDetailList = bizPoHeader.getPoDetailList();
        List<BizPoDetail> poDetailList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(bizPoDetailList)) {
            for (BizPoDetail bizpodetail : bizPoDetailList) {
                //排产类型为按商品排产时，获取排产记录
                if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
                    BizSchedulingPlan bizSchedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(bizpodetail.getId(), PO_DETAIL_TABLE_NAME);
                    if (bizSchedulingPlan != null) {
                        detailSchedulingFlg = true;
                    }
                    bizpodetail.setBizSchedulingPlan(bizSchedulingPlan);
                }
                poDetailList.add(bizpodetail);
                poDetailIdList.add(bizpodetail.getId());
            }
        }
        bizPoHeader.setPoDetailList(poDetailList);

        model.addAttribute("poDetailIdListJson", poDetailIdList);
        model.addAttribute("detailHeaderFlg", detailHeaderFlg);
        model.addAttribute("detailSchedulingFlg", detailSchedulingFlg);
        model.addAttribute("bizPoHeader", bizPoHeader);

        resultMap.put("poDetailIdListJson", poDetailIdList);
        resultMap.put("detailHeaderFlg", detailHeaderFlg);
        resultMap.put("detailSchedulingFlg", detailSchedulingFlg);
        resultMap.put("bizPoHeader", bizPoHeader);

        //判断当前用户是否为供应商
        Boolean roleFlag = false;
        List<Role> roleList = UserUtils.getUser().getRoleList();
        if (roleList != null) {
            for (Role role : roleList) {
                String roleName = role.getEnname();
                if (RoleEnNameEnum.SUPPLY_CHAIN.getDesc().equals(roleName)) {
                    roleFlag = true;
                    break;
                }
            }
        }
        model.addAttribute("roleFlag", roleFlag);
        resultMap.put("roleFlag", roleFlag);
        return JsonUtil.generateData(resultMap, null);
    }

    @RequestMapping(value = "checkSchedulingNum")
    @ResponseBody
    public String checkSchedulingNum(HttpServletRequest request, Integer id) {
        BizPoHeader bizPoHeader = bizPoHeaderService.getTotalQtyAndSchedulingNum(id);
        Map resultMap = new HashMap();
        resultMap.put("totalOrdQty", bizPoHeader.getTotalOrdQty());
        resultMap.put("totalSchedulingDetailNum", bizPoHeader.getTotalSchedulingDetailNum());
        resultMap.put("totalSchedulingHeaderNum", bizPoHeader.getTotalSchedulingHeaderNum());
        resultMap.put("totalCompleteScheduHeaderNum", bizPoHeader.getTotalCompleteScheduHeaderNum());

        return JSONObject.fromObject(resultMap).toString();
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
            String objectName = PO_HEADER_TABLE_NAME;
            //按商品排产时
            if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
                objectName = PO_DETAIL_TABLE_NAME;
                Integer poHeaderId = dtoList.get(0).getId();
                BizSchedulingPlan schedulingPlanPoh = bizSchedulingPlanService.getByObjectIdAndObjectName(poHeaderId, PO_HEADER_TABLE_NAME);
                for (int i = 0; i < dtoList.size(); i++) {
                    BizHeaderSchedulingDto dto = dtoList.get(i);
                    if (i == 0) {
                        if (schedulingPlanPoh == null) {
                            schedulingPlanPoh = new BizSchedulingPlan();
                        }
                        schedulingPlanPoh.setObjectId(dto.getId());
                        schedulingPlanPoh.setObjectName(PO_HEADER_TABLE_NAME);
                        schedulingPlanPoh.setOriginalNum(0);
                        schedulingPlanPoh.setRemark(dto.getRemark());
                        schedulingPlanPoh.setPoSchType(Integer.valueOf(dto.getPoSchType()));
                        bizSchedulingPlanService.save(schedulingPlanPoh);
                    }

                    Integer detailId = dtoList.get(i).getObjectId();
                    BizSchedulingPlan schedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(detailId, objectName);
                    if (schedulingPlan == null) {
                        schedulingPlan = new BizSchedulingPlan();
                        schedulingPlan.setObjectId(dto.getObjectId());
                        schedulingPlan.setObjectName(objectName);
                        schedulingPlan.setOriginalNum(dto.getOriginalNum());
                        schedulingPlan.setPoSchType(Integer.valueOf(dto.getPoSchType()));
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
                    bizInvoiceService.saveDeliver(dtoList.get(0).getId());
                }
                //排产类型为按商品排产时，更新备货单排产类型
                Integer detailId = dtoList.get(0).getObjectId();
                BizPoDetail bizPoDetail = bizPoDetailService.get(detailId);
                BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoDetail.getPoHeader().getId());
                bizPoHeader.setSchedulingType(SCHEDULING_FOR_DETAIL);
                bizPoHeaderService.updateSchedulingType(bizPoHeader);

            } else {
                Integer poHeaderId = dtoList.get(0).getId();
                BizSchedulingPlan schedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(poHeaderId, objectName);
                for (int i = 0; i < dtoList.size(); i++) {
                    BizHeaderSchedulingDto dto = dtoList.get(i);
                    if (i == 0) {
                        if (schedulingPlan == null) {
                            schedulingPlan = new BizSchedulingPlan();
                        }
                        schedulingPlan.setObjectId(dto.getId());
                        schedulingPlan.setObjectName(objectName);
                        schedulingPlan.setOriginalNum(dto.getOriginalNum());
                        schedulingPlan.setRemark(dto.getRemark());
                        schedulingPlan.setPoSchType(Integer.valueOf(dto.getPoSchType()));
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
                    bizInvoiceService.saveDeliver(dtoList.get(0).getId());
                }

            }
        }

        return boo;
    }

    @RequestMapping(value = "batchSaveSchedulingPlan")
    @ResponseBody
    public boolean batchSaveSchedulingPlan(HttpServletRequest request, @RequestBody String params) throws ParseException {
        List<BizHeaderSchedulingDto> dtoList = JsonUtil.parseArray(params, new TypeReference<List<BizHeaderSchedulingDto>>() {
        });
        boolean boo = false;
        if (dtoList != null && dtoList.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //通过排产类型获取排产表中objectName的值
            Integer schedulingType = Integer.parseInt(dtoList.get(0).getSchedulingType());
            String objectName = PO_HEADER_TABLE_NAME;
            //按商品排产时
            if (SCHEDULING_FOR_DETAIL.equals(schedulingType)) {
                objectName = PO_DETAIL_TABLE_NAME;
                Integer poHeaderId = dtoList.get(0).getId();
                BizSchedulingPlan schedulingPlanPoh = bizSchedulingPlanService.getByObjectIdAndObjectName(poHeaderId, PO_HEADER_TABLE_NAME);
                for (int i = 0; i < dtoList.size(); i++) {
                    BizHeaderSchedulingDto dto = dtoList.get(i);
                    if (i == 0) {
                        if (schedulingPlanPoh == null) {
                            schedulingPlanPoh = new BizSchedulingPlan();
                        }
                        schedulingPlanPoh.setObjectId(dto.getId());
                        schedulingPlanPoh.setObjectName(PO_HEADER_TABLE_NAME);
                        schedulingPlanPoh.setOriginalNum(0);
                        schedulingPlanPoh.setRemark(dto.getRemark());
                        schedulingPlanPoh.setPoSchType(Integer.valueOf(dto.getPoSchType()));
                        bizSchedulingPlanService.save(schedulingPlanPoh);
                    }

                    Integer skuInfoId = dto.getObjectId();
                    Integer detailId = bizOrderHeaderService.getOrderDetailIdBySkuInfoId(poHeaderId,skuInfoId);

                    BizSchedulingPlan schedulingPlan = bizSchedulingPlanService.getByObjectIdAndObjectName(detailId, objectName);
                    if (schedulingPlan == null) {
                        schedulingPlan = new BizSchedulingPlan();
                        schedulingPlan.setObjectId(detailId);
                        schedulingPlan.setObjectName(objectName);
                        schedulingPlan.setOriginalNum(dto.getOriginalNum());
                        schedulingPlan.setPoSchType(Integer.valueOf(dto.getPoSchType()));
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
                    bizInvoiceService.saveDeliver(dtoList.get(0).getId());
                }
                //排产类型为按商品排产时，更新备货单排产类型
                Integer detailId =  bizOrderHeaderService.getOrderDetailIdBySkuInfoId(dtoList.get(0).getId(), dtoList.get(0).getObjectId());
                BizPoDetail bizPoDetail = bizPoDetailService.get(detailId);
                BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoDetail.getPoHeader().getId());
                bizPoHeader.setSchedulingType(SCHEDULING_FOR_DETAIL);
                bizPoHeaderService.updateSchedulingType(bizPoHeader);

            }
        }

        return boo;
    }

    @RequestMapping(value = "confirm")
    @ResponseBody
    public boolean confirm(HttpServletRequest request, @RequestBody String params, Integer poHeaderId) {
        Boolean resultFlag = false;
        JSONArray jsonArray = JSONArray.fromObject(params);
        System.out.println(jsonArray);
        //备货单号
        String reqNo = (String) jsonArray.get(0);

        List<String> paramList = Lists.newArrayList();
        for (int i = 1; i < jsonArray.size(); i++) {
            Object item = jsonArray.get(i);
            paramList.add(String.valueOf(item));
        }
        try {
            bizCompletePalnService.batchUpdateCompleteStatus(paramList, poHeaderId);
            resultFlag = true;

//				//供应商已确认排产，发短息通知采销部经理
//			List<User> userList = systemService.findUserByRoleEnName(MARKETING_MANAGER);
//			if (!CollectionUtils.isEmpty(userList)) {
//				String reqNo_1 = reqNo.substring(0,11);
//				String reqNo_2 = reqNo.substring(11);
//				StringBuilder phones = new StringBuilder();
//				for (User user : userList) {
//					if (StringUtils.isNotBlank(user.getMobile())) {
//						phones.append(user.getMobile()).append(",");
//					}
//				}
//				AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.COMPLETE_SCHEDULING.getCode(), phones.toString(), ImmutableMap.of("order", "备货单", "reqNo_1", reqNo_1, "reqNo_2", reqNo_2));
//			}


        } catch (Exception e) {
            resultFlag = false;
            logger.error(e.getMessage());
        }

        return resultFlag;
    }
}
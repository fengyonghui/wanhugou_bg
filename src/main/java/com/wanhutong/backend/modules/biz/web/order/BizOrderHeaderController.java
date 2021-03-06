/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.order;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.common.utils.*;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.logistic.BizOrderLogistics;
import com.wanhutong.backend.modules.biz.entity.order.*;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.message.BizMessageInfoService;
import com.wanhutong.backend.modules.biz.service.order.*;
import com.wanhutong.backend.modules.biz.service.pay.BizPayRecordService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.service.vend.BizVendInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.*;
import com.wanhutong.backend.modules.config.parse.Process;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Controller
 *
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/order/bizOrderHeader")
public class BizOrderHeaderController extends BaseController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BizOrderHeaderController.class);


    private static final Integer IMGTYPE = 37;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizPayRecordService bizPayRecordService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private DictService dictService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizOrderHeaderUnlineService bizOrderHeaderUnlineService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private DefaultPropService defaultPropService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private BizOrderAppointedTimeService bizOrderAppointedTimeService;
    @Autowired
    private BizOrderCommentService bizOrderCommentService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizPoPaymentOrderService bizPoPaymentOrderService;
    @Autowired
    private BizInvoiceService bizInvoiceService;
    @Autowired
    private BizVendInfoService bizVendInfoService;
    @Autowired
    private BizOpShelfSkuService bizOpShelfSkuService;
    @Autowired
    private BizCommissionOrderService bizCommissionOrderService;
    @Autowired
    private BizCommissionService bizCommissionService;
    @Autowired
    private BizMessageInfoService bizMessageInfoService;


    @ModelAttribute
    public BizOrderHeader get(@RequestParam(required = false) Integer id) {
        BizOrderHeader entity = null;
        if (id != null && id != 0) {
            entity = bizOrderHeaderService.get(id);

            String type = "1";
            if (entity.getSuplys() != null && (0 == entity.getSuplys() || 721 == entity.getSuplys())) {
                type = "0";
            }
            CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
            commonProcessEntity.setObjectId(String.valueOf(entity.getId()));
            commonProcessEntity.setCurrent(1);
            commonProcessEntity.setObjectName("0".equals(type) ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
            if (entity.getOrderNum().startsWith("DO")) {
                commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
            }
            List<CommonProcessEntity> commonProcessEntities = commonProcessService.findList(commonProcessEntity);
            if (CollectionUtils.isNotEmpty(commonProcessEntities)) {
                entity.setCommonProcess(commonProcessEntities.get(0));
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(entity);
            List<BizOrderDetail> list = bizOrderDetailService.findList(bizOrderDetail);
            double totalPrice = 0.0;
            for (BizOrderDetail orderDetail : list) {
                totalPrice += orderDetail.getOrdQty() * orderDetail.getBuyPrice();
                if (orderDetail.getSuplyis() != null && orderDetail.getSuplyis().getId() != null) {
                    if (orderDetail.getSuplyis().getId().equals(0) || orderDetail.getSuplyis().getId().equals(721)) {
                        Office office = new Office();
                        office.setName("供货部");
                        office.setId(orderDetail.getSuplyis().getId());
                        orderDetail.setSuplyis(office);
                    }
                }
            }
            entity.setTotalDetail(entity.getTotalDetail());
            entity.setTotalBuyPrice(totalPrice);
            entity.setOrderDetailList(list);

            if (entity.getCommonProcess() != null && entity.getCommonProcess().getId() != null) {
                List<CommonProcessEntity> commonProcessList = Lists.newArrayList();
                bizOrderHeaderService.getCommonProcessListFromDB(entity.getCommonProcess().getId(), commonProcessList);
                Collections.reverse(commonProcessList);
                entity.setCommonProcessList(commonProcessList);
            }

            BizPoHeader bizPoHeader = new BizPoHeader();
            bizPoHeader.setBizOrderHeader(entity);
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
        }
        if (entity == null) {
            entity = new BizOrderHeader();
        }
        return entity;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        //判断是否为代销订单
        if ("COMMISSION_ORDER".equals(bizOrderHeader.getTargetPage())){
            //零售订单
            bizOrderHeader.setOrderType(8);
            //收货完成
            bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.COMPLETE.getState());
            //未结佣
            //bizOrderHeader.setCommissionStatus(OrderHeaderCommissionStatusEnum.NO_COMMISSSION.getComStatus());
        }
        if (bizOrderHeader.getSkuChickCount() != null) {
            //商品下单量标识
            bizOrderHeader.setSkuChickCount(bizOrderHeader.getSkuChickCount());
        }

        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();
        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessFifthConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        Map<String, String> originConfigMap = Maps.newLinkedHashMap();

        originConfigMap.put("渠道经理", "渠道经理");
        originConfigMap.put("总经理", "总经理");
        originConfigMap.put("采销经理", "采销经理");
        originConfigMap.put("财务经理", "财务经理");
        originConfigMap.put("审批完成", "审批完成");
        originConfigMap.put("驳回", "驳回");
        originConfigMap.put("不需要审批", "不需要审批");

        //支付申请单合并搜索条件审核状态
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        List<com.wanhutong.backend.modules.config.parse.Process> processList = purchaseOrderProcessConfig.getShowFilterProcessList();

        if (org.apache.commons.lang3.StringUtils.isNotBlank(bizOrderHeader.getProcessTypeStr())) {
            List<Process> processListTemp = purchaseOrderProcessConfig.getNameProcessMap().get(bizOrderHeader.getProcessTypeStr());
            List<String> transform = processListTemp.stream().map(process -> String.valueOf(process.getCode())).collect(Collectors.toList());
            bizOrderHeader.setProcessTypeList(transform);
        }


        Set<String> processSet = Sets.newHashSet();
        for (com.wanhutong.backend.modules.config.parse.Process process : processList) {
            processSet.add(process.getName());
        }

        model.addAttribute("processList", processSet);

        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        if ("COMMISSION_ORDER".equals(bizOrderHeader.getTargetPage())){
            List<BizOrderHeader> bizOrderHeaderList = page.getList();
            List<BizOrderHeader> bizOrderHeaderListNew = new ArrayList<BizOrderHeader>();
            if (CollectionUtils.isNotEmpty(bizOrderHeaderList)) {
                for (BizOrderHeader orderHeader :bizOrderHeaderList) {
                    BizCommissionOrder bizCommissionOrder = new BizCommissionOrder();
                    String applyCommStatus = "no";
                    bizCommissionOrder.setOrderId(orderHeader.getId());
                    List<BizCommissionOrder> commissionOrderList = bizCommissionOrderService.findList(bizCommissionOrder);
                    if (CollectionUtils.isNotEmpty(commissionOrderList)) {
                        applyCommStatus = "yes";
                        bizCommissionOrder = commissionOrderList.get(0);
                        Integer commId = bizCommissionOrder.getCommId();
                        BizCommission bizCommission = bizCommissionService.get(commId);
                        orderHeader.setBizCommission(bizCommission);
                    }
                    orderHeader.setApplyCommStatus(applyCommStatus);
//                    BigDecimal commission = BigDecimal.ZERO;
//                    List<BizOrderDetail> orderDetails = orderHeader.getOrderDetailList();
//                    if (CollectionUtils.isNotEmpty(orderDetails)) {
//                        BigDecimal detailCommission = BigDecimal.ZERO;
//                        for (BizOrderDetail orderDetail : orderDetails) {
//                            BizOpShelfSku bizOpShelfSku = orderDetail.getSkuInfo().getBizOpShelfSku();
//                            BigDecimal orgPrice = new BigDecimal(bizOpShelfSku.getOrgPrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
//                            BigDecimal salePrice = new BigDecimal(bizOpShelfSku.getSalePrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
//                            Integer ordQty = orderDetail.getOrdQty();
//                            BigDecimal commissionRatio = bizOpShelfSku.getCommissionRatio();
//                            if (commissionRatio == null || commissionRatio.compareTo(BigDecimal.ZERO) <= 0) {
//                                commissionRatio = BigDecimal.ZERO;
//                            }
//                            detailCommission = (salePrice.subtract(orgPrice)).multiply(BigDecimal.valueOf(ordQty)).multiply(commissionRatio).divide(BigDecimal.valueOf(100));
//                            commission = commission.add(detailCommission);
//                        }
//                    }
//                    orderHeader.setCommission(commission);
                    bizOrderHeaderListNew.add(orderHeader);
                }
            }
            page.setList(bizOrderHeaderListNew);
        }

        model.addAttribute("page", page);
        if (bizOrderHeader.getSource() != null) {
            model.addAttribute("source", bizOrderHeader.getSource());
        }


        List<Callable<Pair<Boolean, String>>> tasks = new ArrayList<>();
        for (BizOrderHeader b : page.getList()) {
           tasks.add(new Callable<Pair<Boolean, String>>() {
               @Override
               public Pair<Boolean, String> call() {
                   BizPoHeader bizPoHeader = new BizPoHeader();
                   bizPoHeader.setBizOrderHeader(b);
                   List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);

                    List<CommonProcessEntity> list = null;
                    if (b.getOrderNum().startsWith("SO") || b.getOrderNum().startsWith("RO")) {
                        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
                        commonProcessEntity.setObjectId(String.valueOf(b.getId()));
                        commonProcessEntity.setObjectName(JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);

                       if (b.getSuplys() == null || b.getSuplys() == 0 || b.getSuplys() == 721) {
                           commonProcessEntity.setObjectName(JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME);
                       }

                       list = commonProcessService.findList(commonProcessEntity);

                       if (CollectionUtils.isNotEmpty(list)) {
                           b.setCommonProcess(list.get(list.size() - 1));
                       }

                       if (CollectionUtils.isEmpty(poList) && CollectionUtils.isEmpty(list)
                               && b.getBizStatus() >= OrderHeaderBizStatusEnum.SUPPLYING.getState()
                               && !OrderHeaderBizStatusEnum.CANCLE.getState().equals(b.getBizStatus())
                               && !OrderHeaderBizStatusEnum.DELETE.getState().equals(b.getBizStatus())) {
                           OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(b);
                           //b.setPayProportion(orderPayProportionStatusEnum.getState());
                           bizOrderHeaderService.saveOrderHeader(b);
                           genAuditProcess(orderPayProportionStatusEnum, b, Boolean.FALSE, Boolean.FALSE);
                       }
                   }
                   if (b.getOrderNum().startsWith("DO")) {
                       CommonProcessEntity commonProcessEntityTemp = new CommonProcessEntity();
                       commonProcessEntityTemp.setObjectId(String.valueOf(b.getId()));
                       commonProcessEntityTemp.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
                       list = commonProcessService.findList(commonProcessEntityTemp);
                       if (CollectionUtils.isEmpty(list) && b.getBizStatus() >= OrderHeaderBizStatusEnum.SUPPLYING.getState()
                               && !OrderHeaderBizStatusEnum.CANCLE.getState().equals(b.getBizStatus())
                               && !OrderHeaderBizStatusEnum.DELETE.getState().equals(b.getBizStatus())) {
                           OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(b);
                           Integer state = orderPayProportionStatusEnum.getState();
                           if (state > 0) {
                               bizOrderHeaderService.saveCommonProcess(orderPayProportionStatusEnum, b, Boolean.FALSE, Boolean.FALSE);
                           }
                       }

                       CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
                       commonProcessEntity.setObjectId(String.valueOf(b.getId()));
                       commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);

                       list = commonProcessService.findList(commonProcessEntity);

                       if (CollectionUtils.isNotEmpty(list)) {
                           b.setCommonProcess(list.get(list.size() - 1));
                       }
                   }
                   BizInvoice bizInvoice = new BizInvoice();
                   bizInvoice.setOrderNum(b.getOrderNum());
                   bizInvoice.setShip(0);
                   b.setBizInvoiceList(bizInvoiceService.findList(bizInvoice));
                   return Pair.of(Boolean.TRUE, "操作成功");
               }
           });
        }
        try {
            ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
        } catch (InterruptedException e) {
            LOGGER.error("init order list data error", e);
        }

        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }

        model.addAttribute("originConfigMap", originConfigMap);
        model.addAttribute("roleSet", roleSet);
        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        model.addAttribute("auditFithStatus", doOrderHeaderProcessFifthConfig.getAutProcessId());
        model.addAttribute("auditStatus", originConfig.getPayProcessId());

        //付款单审核用
        model.addAttribute("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());
        //判断是否为代销订单
        if ("COMMISSION_ORDER".equals(bizOrderHeader.getTargetPage())){
            return "modules/biz/order/bizCommissionOrderHeaderList";
        }

        return "modules/biz/order/bizOrderHeaderList";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = {"listData4mobile"})
    @ResponseBody
    public String listData4mobile(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        //判断是否为代销订单
        if ("COMMISSION_ORDER".equals(bizOrderHeader.getTargetPage())){
            //零售订单
            bizOrderHeader.setOrderType(8);
            //收货完成
            bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.COMPLETE.getState());
            //未结佣
            //bizOrderHeader.setCommissionStatus(OrderHeaderCommissionStatusEnum.NO_COMMISSSION.getComStatus());
        }
        if (bizOrderHeader.getSkuChickCount() != null) {
            //商品下单量标识
            bizOrderHeader.setSkuChickCount(bizOrderHeader.getSkuChickCount());
        }

        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();
        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessFifthConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        Map<String, String> originConfigMap = Maps.newLinkedHashMap();

        originConfigMap.put("渠道经理", "渠道经理");
        originConfigMap.put("总经理", "总经理");
        originConfigMap.put("采销经理", "采销经理");
        originConfigMap.put("财务经理", "财务经理");
        originConfigMap.put("完成", "完成");
        originConfigMap.put("驳回", "驳回");
        originConfigMap.put("不需要审批", "不需要审批");

        //支付申请单合并搜索条件审核状态
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        List<com.wanhutong.backend.modules.config.parse.Process> processList = purchaseOrderProcessConfig.getShowFilterProcessList();

        if (org.apache.commons.lang3.StringUtils.isNotBlank(bizOrderHeader.getProcessTypeStr())) {
            List<Process> processListTemp = purchaseOrderProcessConfig.getNameProcessMap().get(bizOrderHeader.getProcessTypeStr());
            List<String> transform = processListTemp.stream().map(process -> String.valueOf(process.getCode())).collect(Collectors.toList());
            bizOrderHeader.setProcessTypeList(transform);
        }

        Set<String> processSet = Sets.newHashSet();
        for (com.wanhutong.backend.modules.config.parse.Process process : processList) {
            processSet.add(process.getName());
        }
        model.addAttribute("processList", processSet);
        resultMap.put("processList", processSet);

        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        if ("COMMISSION_ORDER".equals(bizOrderHeader.getTargetPage())){
            List<BizOrderHeader> bizOrderHeaderList = page.getList();
            List<BizOrderHeader> bizOrderHeaderListNew = new ArrayList<BizOrderHeader>();
            if (CollectionUtils.isNotEmpty(bizOrderHeaderList)) {
                for (BizOrderHeader orderHeader :bizOrderHeaderList) {
                    BizCommissionOrder bizCommissionOrder = new BizCommissionOrder();
                    String applyCommStatus = "no";
                    bizCommissionOrder.setOrderId(orderHeader.getId());
                    List<BizCommissionOrder> commissionOrderList = bizCommissionOrderService.findList(bizCommissionOrder);
                    if (CollectionUtils.isNotEmpty(commissionOrderList)) {
                        applyCommStatus = "yes";
                        bizCommissionOrder = commissionOrderList.get(0);
                        Integer commId = bizCommissionOrder.getCommId();
                        BizCommission bizCommission = bizCommissionService.get(commId);
                        orderHeader.setBizCommission(bizCommission);
                    }
                    orderHeader.setApplyCommStatus(applyCommStatus);
//                    BigDecimal commission = BigDecimal.ZERO;
//                    List<BizOrderDetail> orderDetails = orderHeader.getOrderDetailList();
//                    if (CollectionUtils.isNotEmpty(orderDetails)) {
//                        BigDecimal detailCommission = BigDecimal.ZERO;
//                        for (BizOrderDetail orderDetail : orderDetails) {
//                            BizOpShelfSku bizOpShelfSku = orderDetail.getSkuInfo().getBizOpShelfSku();
//                            BigDecimal orgPrice = new BigDecimal(bizOpShelfSku.getOrgPrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
//                            BigDecimal salePrice = new BigDecimal(bizOpShelfSku.getSalePrice()).setScale(0, BigDecimal.ROUND_HALF_UP);
//                            Integer ordQty = orderDetail.getOrdQty();
//                            BigDecimal commissionRatio = bizOpShelfSku.getCommissionRatio();
//                            if (commissionRatio == null || commissionRatio.compareTo(BigDecimal.ZERO) <= 0) {
//                                commissionRatio = BigDecimal.ZERO;
//                            }
//                            detailCommission = (salePrice.subtract(orgPrice)).multiply(BigDecimal.valueOf(ordQty)).multiply(commissionRatio).divide(BigDecimal.valueOf(100));
//                            commission = commission.add(detailCommission);
//                        }
//                    }
//                    orderHeader.setCommission(commission);
                    bizOrderHeaderListNew.add(orderHeader);
                }
            }
            page.setList(bizOrderHeaderListNew);
        }
        model.addAttribute("page", page);
        resultMap.put("page", page);
        if (bizOrderHeader.getSource() != null) {
            model.addAttribute("source", bizOrderHeader.getSource());
        }

        List<Callable<Pair<Boolean, String>>> tasks = new ArrayList<>();
        for (BizOrderHeader b : page.getList()) {
            tasks.add(new Callable<Pair<Boolean, String>>() {
                @Override
                public Pair<Boolean, String> call() {
                    BizPoHeader bizPoHeader = new BizPoHeader();
                    bizPoHeader.setBizOrderHeader(b);
                    List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);

                    List<CommonProcessEntity> list = null;
                    if (b.getOrderNum().startsWith("SO") || b.getOrderNum().startsWith("RO")) {
                        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
                        commonProcessEntity.setObjectId(String.valueOf(b.getId()));
                        commonProcessEntity.setObjectName(JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);

                        if (b.getSuplys() == null || b.getSuplys() == 0 || b.getSuplys() == 721) {
                            commonProcessEntity.setObjectName(JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME);
                        }

                        list = commonProcessService.findList(commonProcessEntity);

                        if (CollectionUtils.isNotEmpty(list)) {
                            b.setCommonProcess(list.get(list.size() - 1));
                        }

                        if (CollectionUtils.isEmpty(poList) && CollectionUtils.isEmpty(list)
                                && b.getBizStatus() >= OrderHeaderBizStatusEnum.SUPPLYING.getState()
                                && !OrderHeaderBizStatusEnum.CANCLE.getState().equals(b.getBizStatus())
                                && !OrderHeaderBizStatusEnum.DELETE.getState().equals(b.getBizStatus())) {
                            OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(b);
                            //b.setPayProportion(orderPayProportionStatusEnum.getState());
                            bizOrderHeaderService.saveOrderHeader(b);
                            genAuditProcess(orderPayProportionStatusEnum, b, Boolean.FALSE, Boolean.FALSE);
                        }
                    }
                    if (b.getOrderNum().startsWith("DO")) {
                        CommonProcessEntity commonProcessEntityTemp = new CommonProcessEntity();
                        commonProcessEntityTemp.setObjectId(String.valueOf(b.getId()));
                        commonProcessEntityTemp.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
                        list = commonProcessService.findList(commonProcessEntityTemp);
                        if (CollectionUtils.isEmpty(list) && b.getBizStatus() >= OrderHeaderBizStatusEnum.SUPPLYING.getState()
                                && !OrderHeaderBizStatusEnum.CANCLE.getState().equals(b.getBizStatus())
                                && !OrderHeaderBizStatusEnum.DELETE.getState().equals(b.getBizStatus())) {
                            OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(b);
                            Integer state = orderPayProportionStatusEnum.getState();
                            if (state > 0) {
                                bizOrderHeaderService.saveCommonProcess(orderPayProportionStatusEnum, b, Boolean.FALSE, Boolean.FALSE);
                            }
                        }

                        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
                        commonProcessEntity.setObjectId(String.valueOf(b.getId()));
                        commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);

                        list = commonProcessService.findList(commonProcessEntity);

                        if (CollectionUtils.isNotEmpty(list)) {
                            b.setCommonProcess(list.get(list.size() - 1));
                        }
                    }
                    BizInvoice bizInvoice = new BizInvoice();
                    bizInvoice.setOrderNum(b.getOrderNum());
                    bizInvoice.setShip(0);
                    b.setBizInvoiceList(bizInvoiceService.findList(bizInvoice));
                    return Pair.of(Boolean.TRUE, "操作成功");
                }
            });
        }
        try {
            ThreadPoolManager.getDefaultThreadPool().invokeAll(tasks);
        } catch (InterruptedException e) {
            LOGGER.error("init order list data error", e);
        }

        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("originConfigMap", originConfigMap);

        model.addAttribute("roleSet", roleSet);
        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        model.addAttribute("auditFithStatus", doOrderHeaderProcessFifthConfig.getAutProcessId());
        model.addAttribute("auditStatus", originConfig.getPayProcessId());

        //付款单审核用
        model.addAttribute("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());

        resultMap.put("originConfigMap", originConfigMap);
        resultMap.put("roleSet", roleSet);
        resultMap.put("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        resultMap.put("auditFithStatus", doOrderHeaderProcessFifthConfig.getAutProcessId());
        resultMap.put("auditStatus", originConfig.getPayProcessId());

        //页面常量值获取
        resultMap.put("SUPPLYING", OrderHeaderBizStatusEnum.SUPPLYING.getState());
        resultMap.put("CANCLE", OrderHeaderBizStatusEnum.CANCLE.getState());
        resultMap.put("DELETE", OrderHeaderBizStatusEnum.DELETE.getState());
        resultMap.put("UNAPPROVE", OrderHeaderBizStatusEnum.UNAPPROVE.getState());
        resultMap.put("STOCKING", OrderHeaderBizStatusEnum.STOCKING.getState());
        resultMap.put("PHOTO_ORDER", BizOrderTypeEnum.PHOTO_ORDER.getState());
        resultMap.put("PURCHASE_ORDER", BizOrderTypeEnum.PURCHASE_ORDER.getState());
        resultMap.put("ORDINARY_ORDER", BizOrderTypeEnum.ORDINARY_ORDER.getState());
        resultMap.put("REFUND", OrderHeaderDrawBackStatusEnum.REFUND.getState());
        resultMap.put("REFUNDING", OrderHeaderDrawBackStatusEnum.REFUNDING.getState());
        resultMap.put("REFUNDREJECT", OrderHeaderDrawBackStatusEnum.REFUNDREJECT.getState());
        resultMap.put("REFUNDED", OrderHeaderDrawBackStatusEnum.REFUNDED.getState());
        resultMap.put("COMMISSION_ORDER", BizOrderTypeEnum.COMMISSION_ORDER.getState());
        resultMap.put("payStatus", ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getPayProcessId());

        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form")
    public String form(BizOrderHeader bizOrderHeader, Model model,
                       String orderNoEditable, String orderDetails,
                       HttpServletRequest request, HttpServletResponse response
    ) {
        model.addAttribute("orderType", bizOrderHeader.getOrderType());
        if(!Objects.isNull(bizOrderHeader.getBizPoHeader())) {
            BizPoHeader  bizPoHeader=bizOrderHeader.getBizPoHeader();
            if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null && bizPoHeader.getDeliveryOffice().getId() != 0) {
                Office office = officeService.get(bizPoHeader.getDeliveryOffice().getId());
                if ("8".equals(office.getType())) {
                    bizPoHeader.setDeliveryStatus(0);
                } else {
                    bizPoHeader.setDeliveryStatus(1);
                }
            }
            bizOrderHeader.setBizPoHeader(bizPoHeader);
            BizPoHeader bizPoHeader2 = bizPoHeaderService.get(bizOrderHeader.getBizPoHeader().getId());
            model.addAttribute("bizPoHeader", bizPoHeader2);
            //BizPoHeader bizPoHeader =bizOrderHeader.getBizPoHeader();
            BizPoPaymentOrder bizPoPaymentOrder2 = new BizPoPaymentOrder();
            bizPoPaymentOrder2.setPoHeaderId(bizOrderHeader.getBizPoHeader().getId());
            bizPoPaymentOrder2.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
//        bizPoPaymentOrder2.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
            Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder2);
            //更新BizPoPaymentOrder审核按钮控制flag
            bizPoPaymentOrderService.updateHasRole(page);
            model.addAttribute("poPaymentOrderPage", page);
            List<BizPoPaymentOrder> payList = page.getList();
            String str = bizOrderHeader.getStr();
            if ("pay".equals(str)) {
                if (CollectionUtils.isNotEmpty(payList)) {
                    bizPoPaymentOrder2 = payList.get(0);
                }
                bizOrderHeader.setBizPoPaymentOrder(bizPoPaymentOrder2);
            }
        }
        if (bizOrderHeader.getSource() != null) {
            model.addAttribute("source", bizOrderHeader.getSource());
        }
        BizOrderComment bizOrderComment = new BizOrderComment();
        bizOrderComment.setOrder(bizOrderHeader);
        List<BizOrderComment> commentList = bizOrderCommentService.findList(bizOrderComment);
        model.addAttribute("commentList", commentList);
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null) {
                bizOrderHeader.setCustomer(office);
                model.addAttribute("entity2", bizOrderHeader);
            }
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants() != null &&
                    bizCustomCenterConsultant.getConsultants().getName() != null) {
                bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
                model.addAttribute("orderCenter", bizCustomCenterConsultant);
            } else {
                model.addAttribute("orderCenter", new BizCustomCenterConsultant());
            }
        }
        BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());

        String type = "1";
        if (bizOrderHeaderTwo.getSuplys() != null && (bizOrderHeaderTwo.getSuplys() == 0 || bizOrderHeaderTwo.getSuplys() == 721)) {
            type = "0";
        }

        bizOrderHeaderTwo.setStr(bizOrderHeader.getStr());
        bizOrderHeaderTwo.setCommonProcess(bizOrderHeader.getCommonProcess());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = bizOrderHeaderTwo.getTotalDetail();//订单详情总价
            Double totalExp = bizOrderHeaderTwo.getTotalExp();//订单总费用
            Double freight = bizOrderHeaderTwo.getFreight();//运费
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            bizOrderHeader.setTobePaid(orderHeaderTotal - bizOrderHeaderTwo.getReceiveTotal());//页面显示待支付总价
            if (orderNoEditable != null && orderNoEditable.equals("editable")) {//不可编辑标识符
                bizOrderHeaderTwo.setOrderNoEditable("editable");//待支付页面不能修改
            }
            if (orderDetails != null && orderDetails.equals("details")) {
                bizOrderHeaderTwo.setOrderDetails("details");//查看详情页面不能修改
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            for (BizOrderAddress orderAddress : list) {
//				    收货地址
                if (orderAddress.getType() == 1) {
                    model.addAttribute("orderAddress", orderAddress);
                }
            }
            BizOrderAddress orderAddress = new BizOrderAddress();
            orderAddress.setOrderHeaderID(bizOrderHeaderTwo);
            List<BizOrderAddress> addresslist = bizOrderAddressService.findList(orderAddress);
            if (CollectionUtils.isNotEmpty(addresslist)) {
                for (BizOrderAddress address : addresslist) {
                    //				交货地址
                    if (address.getType() == 2) {
                        model.addAttribute("address", address);
                    }
                }
            }

            //经销店
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                User user = systemService.getUser(office.getPrimaryPerson().getId());
                model.addAttribute("custUser", user);
            }
            //供应商
            List<User> vendUser = bizOrderHeaderService.findVendUserV2(bizOrderHeader.getId());
            if (CollectionUtils.isNotEmpty(vendUser) && vendUser.get(0) != null) {
                model.addAttribute("vendUser", vendUser.get(0));
//                entity.sellers.bizVendInfo.office.id
                bizOrderHeader.setVendorId(vendUser.get(0).getVendor().getId());
                bizOrderHeader.setVendorName(vendUser.get(0).getVendor().getName());
                bizOrderHeader.setSellersId(vendUser.get(0).getVendor().getId());
            }

            //代采
            if (bizOrderHeaderTwo != null && BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(bizOrderHeader.getOrderType())) {
                if (bizOrderHeaderTwo.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                    bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                    List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                    if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                        model.addAttribute("appointedTimeList", appointedTimeList);
                    }
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);

            List<Integer> skuInfoIdList = Lists.newArrayList();
            List<BizOrderDetail> bizOrderDetails = bizOrderHeader.getOrderDetailList();
            if (CollectionUtils.isNotEmpty(bizOrderDetails)) {
                for (BizOrderDetail orderDetail : bizOrderDetails) {
                    BizSkuInfo bizSkuInfo = orderDetail.getSkuInfo();
                    skuInfoIdList.add(bizSkuInfo.getId());
                }
                model.addAttribute("skuInfoIdListListJson", skuInfoIdList);
            }

            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                if (bizSkuInfo != null) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfo);
                    if (skuInfo != null) {
                        orderDetail.setSkuInfo(skuInfo);
                    }
                }
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getOrderNum() != null) {
                    String orderNum = orderDetail.getPoHeader().getOrderNum();
                    orderNumMap.put(keyId, orderNum);
                }
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getId() != null) {
                    int detailId = orderDetail.getPoHeader().getId();
                    detailIdMap.put(keyId, detailId);
                }
            }
        }
        boolean flag = false;
        User user = UserUtils.getUser();
        if (user.getRoleList() != null) {

            for (Role role : user.getRoleList()) {
                if (RoleEnNameEnum.FINANCE.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }
        model.addAttribute("roleSet", roleSet);
        model.addAttribute("roleEnNameSet", roleEnNameSet);

        if (bizOrderHeader.getId() != null) {
            BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
            bizOrderHeaderUnline.setOrderHeader(bizOrderHeader);
            List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
            if (CollectionUtils.isNotEmpty(unlineList)) {
                model.addAttribute("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());
        List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
        statusList.sort(Comparator.comparing(BizOrderStatus::getCreateDate).thenComparing(BizOrderStatus::getId));

        Map<Integer, OrderHeaderBizStatusEnum> statusMap = OrderHeaderBizStatusEnum.getStatusMap();

        String statuPath = request.getParameter("statu");
        model.addAttribute("statuPath", statuPath);

        String refundSkip = request.getParameter("refundSkip");
        model.addAttribute("refundSkip", refundSkip);

        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        String drawbackStatusStr = request.getParameter("drawbackStatus");
        if (StringUtils.isNotBlank(drawbackStatusStr)) {
            Integer drawbackStatus = Integer.valueOf(drawbackStatusStr);
            BizDrawBack bizDrawBack = new BizDrawBack();
            bizDrawBack.setDrawbackStatus(drawbackStatus);
            bizOrderHeader.setDrawBack(bizDrawBack);
        }
        model.addAttribute("entity", bizOrderHeader);
        //判断订单是否是产地直发
        boolean logisticsLinesFlag = false;
        if (bizOrderHeader != null && bizOrderHeader.getBizOrderLogistics() != null) {
            BizOrderLogistics bizOrderLogistics = bizOrderHeader.getBizOrderLogistics();
            String logisticsLines = bizOrderLogistics.getLogisticsLines();
            if (logisticsLines != null && !"".equals(logisticsLines) && logisticsLines.contains(BizOrderLogisticsEnum.DIRECT_MANUFACTURERS.getDesc())) {
                logisticsLinesFlag = true;
            }
        }
        model.addAttribute("logisticsLinesFlag", logisticsLinesFlag);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);
        model.addAttribute("statusMap", statusMap);

        //图片处理
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.UNlINE_REFUND_VOUCHER.getCode());
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName("biz_order_header");
        if (bizOrderHeader.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            String photos = "";
            Map<String, Integer> photosMap = new LinkedHashMap<>();

            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer() + img.getImgPath(), img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizOrderHeader.setPhotos(photos);
            }
            if (imgList != null && !imgList.isEmpty()) {
                model.addAttribute("photosMap", photosMap);
            }
        }

        if ("audit".equalsIgnoreCase(bizOrderHeaderTwo.getStr()) && bizOrderHeaderTwo.getBizPoHeader() != null && bizOrderHeaderTwo.getBizPoHeader().getCommonProcess() != null) {
            com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizOrderHeaderTwo.getBizPoHeader().getCommonProcess().getType()));
            model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
        }

        // type = 0 产地直发
        // type = 1 本地备货
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
        commonProcessEntity.setObjectName("0".equals(type) ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        if (bizOrderHeader.getOrderNum().startsWith("DO")) {
            commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        }

        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);

        List<CommonProcessEntity> poAuditList = null;
        if (bizOrderHeader.getId() != null) {
            BizPoHeader bizPoHeader = new BizPoHeader();
            bizPoHeader.setBizOrderHeader(bizOrderHeader);
            List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);

            if (CollectionUtils.isNotEmpty(poList)) {
                bizPoHeader = poList.get(0);
                CommonProcessEntity poCommonProcessEntity = new CommonProcessEntity();
                poCommonProcessEntity.setObjectId(String.valueOf(bizPoHeader.getId()));
                poCommonProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
                poAuditList = commonProcessService.findList(poCommonProcessEntity);

                BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
                bizPoPaymentOrder.setPoHeaderId(poList.get(0).getId());
                List<BizPoPaymentOrder> bizPoPaymentOrderList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
                BigDecimal totalPayTotal = new BigDecimal(String.valueOf(BigDecimal.ZERO));
                for (BizPoPaymentOrder poPaymentOrder :bizPoPaymentOrderList) {
                    BigDecimal payTotal = poPaymentOrder.getPayTotal();
                    totalPayTotal = totalPayTotal.add(payTotal);
                }
                model.addAttribute("totalPayTotal", totalPayTotal);
            }
        }


        if (CollectionUtils.isNotEmpty(poAuditList) && CollectionUtils.isNotEmpty(list)) {
            if (bizOrderHeader.getOrderNum().startsWith("DO")) {
                if (String.valueOf(ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get().getAutProcessId()).equals(list.get(list.size() - 1).getType())) {
                    list.remove(list.size() - 1);
                    list.addAll(poAuditList);
                    list.get(list.size() - 1).setCurrent(1);
                }
            }
            if ((bizOrderHeader.getOrderNum().startsWith("SO") || bizOrderHeader.getOrderNum().startsWith("RO")) && ("666".equals(list.get(list.size() - 1).getType()) || "777".equals(list.get(list.size() - 1).getType()))) {
                list.remove(list.size() - 1);
                list.addAll(poAuditList);
                list.get(list.size() - 1).setCurrent(1);
            }
        }

        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> currentList = commonProcessService.findList(commonProcessEntity);

        request.setAttribute("id", bizOrderHeader.getId());
        request.setAttribute("poAuditList", poAuditList);
        request.setAttribute("auditList", list);
        request.setAttribute("currentAuditStatus", CollectionUtils.isNotEmpty(currentList) ? currentList.get(0) : new CommonProcessEntity());
        request.setAttribute("type", type);
        request.setAttribute("processMap", "0".equals(type) ?
                ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap()
                : ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap());

        String createPo = "no";
        Integer orderType = bizOrderHeader.getOrderType();
        OrderPayProportionStatusEnum statusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
        Integer statusEnumState = statusEnum.getState();
        request.setAttribute("statusEnumState", statusEnumState);
        if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(orderType)){
            if (bizOrderHeader.getCommonProcess() != null && ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get().getCreatePoProcessId().toString().equals(bizOrderHeader.getCommonProcess().getType())) {
                createPo = "yes";
            }
        } else if ((bizOrderHeader.getOrderNum().startsWith("SO") || bizOrderHeader.getOrderNum().startsWith("RO")) && bizOrderHeader.getCommonProcess() != null && (bizOrderHeader.getSuplys() ==0 || bizOrderHeader.getSuplys() == 721)) {
            String processType = bizOrderHeader.getCommonProcess().getType();
            String zeroCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getZeroCreatePoProcessId());
            String fifthCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getFifthCreatePoProcessId());
            String allCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getAllCreatePoProcessId());
            if (zeroCreatePoProcessId.equals(processType) || fifthCreatePoProcessId.equals(processType) || allCreatePoProcessId.equals(processType)) {
                createPo = "yes";
            }
        }
        model.addAttribute("createPo",createPo);
        //展示库存数量
        Map<Integer,Integer> invSkuNumMap = bizOrderHeaderService.getInvSkuNum(bizOrderHeader);
        model.addAttribute("invSkuNumMap",invSkuNumMap);

        return "modules/biz/order/bizOrderHeaderForm";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "form4Mobile")
    @ResponseBody
    public String form4Mobile(BizOrderHeader bizOrderHeader, Model model,
                       String orderNoEditable, String orderDetails,
                       HttpServletRequest request, HttpServletResponse response
    ) {
        Map<String, Object> resultMap = Maps.newHashMap();
        model.addAttribute("orderType", bizOrderHeader.getOrderType());
        resultMap.put("orderType", bizOrderHeader.getOrderType());
        String str = bizOrderHeader.getStr();
        if ("pay".equals(str)) {
            BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
            bizPoPaymentOrder.setPoHeaderId(bizOrderHeader.getBizPoHeader().getId());
            bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
            bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
            List<BizPoPaymentOrder> payList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
            if (CollectionUtils.isNotEmpty(payList)) {
                bizPoPaymentOrder = payList.get(0);
            }
            bizOrderHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
        }
        if (bizOrderHeader.getSource() != null) {
            model.addAttribute("source", bizOrderHeader.getSource());
            resultMap.put("source", bizOrderHeader.getSource());
        }
        BizOrderComment bizOrderComment = new BizOrderComment();
        bizOrderComment.setOrder(bizOrderHeader);
        List<BizOrderComment> commentList = bizOrderCommentService.findList(bizOrderComment);
        model.addAttribute("commentList", commentList);
        resultMap.put("commentList", commentList);
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null) {
                bizOrderHeader.setCustomer(office);
                model.addAttribute("entity2", bizOrderHeader);
                resultMap.put("entity2", bizOrderHeader);
            }
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
            if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants() != null &&
                    bizCustomCenterConsultant.getConsultants().getName() != null) {
                bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
                model.addAttribute("orderCenter", bizCustomCenterConsultant);
                resultMap.put("orderCenter", bizCustomCenterConsultant);
            } else {
                model.addAttribute("orderCenter", new BizCustomCenterConsultant());
                resultMap.put("orderCenter", new BizCustomCenterConsultant());
            }
        }
        BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());

        String type = "1";
        if (bizOrderHeaderTwo.getSuplys() != null && (bizOrderHeaderTwo.getSuplys() == 0 || bizOrderHeaderTwo.getSuplys() == 721)) {
            type = "0";
        }

        bizOrderHeaderTwo.setStr(bizOrderHeader.getStr());
        bizOrderHeaderTwo.setCommonProcess(bizOrderHeader.getCommonProcess());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = bizOrderHeaderTwo.getTotalDetail();//订单详情总价
            Double totalExp = bizOrderHeaderTwo.getTotalExp();//订单总费用
            Double freight = bizOrderHeaderTwo.getFreight();//运费
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            bizOrderHeader.setTobePaid(orderHeaderTotal - bizOrderHeaderTwo.getReceiveTotal());//页面显示待支付总价
            if (orderNoEditable != null && orderNoEditable.equals("editable")) {//不可编辑标识符
                bizOrderHeaderTwo.setOrderNoEditable("editable");//待支付页面不能修改
            }
            if (orderDetails != null && orderDetails.equals("details")) {
                bizOrderHeaderTwo.setOrderDetails("details");//查看详情页面不能修改
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            for (BizOrderAddress orderAddress : list) {
//				    收货地址
                if (orderAddress.getType() == 1) {
                    model.addAttribute("orderAddress", orderAddress);
                    resultMap.put("orderAddress", orderAddress);
                }
            }
            BizOrderAddress orderAddress = new BizOrderAddress();
            orderAddress.setOrderHeaderID(bizOrderHeaderTwo);
            List<BizOrderAddress> addresslist = bizOrderAddressService.findList(orderAddress);
            if (CollectionUtils.isNotEmpty(addresslist)) {
                for (BizOrderAddress address : addresslist) {
                    //				交货地址
                    if (address.getType() == 2) {
                        model.addAttribute("address", address);
                        resultMap.put("address", address);
                    }
                }
            }

            //经销店
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                User user = systemService.getUser(office.getPrimaryPerson().getId());
                model.addAttribute("custUser", user);
                resultMap.put("custUser", user);
            }
            //供应商
            List<User> vendUser = bizOrderHeaderService.findVendUserV2(bizOrderHeader.getId());
            if (CollectionUtils.isNotEmpty(vendUser) && vendUser.get(0) != null) {
                model.addAttribute("vendUser", vendUser.get(0));
                resultMap.put("vendUser", vendUser.get(0));
                bizOrderHeader.setVendorId(vendUser.get(0).getVendor().getId());
                bizOrderHeader.setVendorName(vendUser.get(0).getVendor().getName());
                bizOrderHeader.setSellersId(vendUser.get(0).getVendor().getId());
            }

            //代采
            if (bizOrderHeaderTwo != null && BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(bizOrderHeader.getOrderType())) {
                if (bizOrderHeaderTwo.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                    bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                    List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                    if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                        model.addAttribute("appointedTimeList", appointedTimeList);
                        resultMap.put("appointedTimeList", appointedTimeList);
                    }
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);

            List<Integer> skuInfoIdList = Lists.newArrayList();
            List<BizOrderDetail> bizOrderDetails = bizOrderHeader.getOrderDetailList();
            if (CollectionUtils.isNotEmpty(bizOrderDetails)) {
                for (BizOrderDetail orderDetail : bizOrderDetails) {
                    BizSkuInfo bizSkuInfo = orderDetail.getSkuInfo();
                    skuInfoIdList.add(bizSkuInfo.getId());
                }
                model.addAttribute("skuInfoIdListListJson", skuInfoIdList);
                resultMap.put("skuInfoIdListListJson", skuInfoIdList);
            }

            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                if (bizSkuInfo != null) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfo);
                    if (skuInfo != null) {
                        orderDetail.setSkuInfo(skuInfo);
                    }
                }
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getOrderNum() != null) {
                    String orderNum = orderDetail.getPoHeader().getOrderNum();
                    orderNumMap.put(keyId, orderNum);
                }
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getId() != null) {
                    int detailId = orderDetail.getPoHeader().getId();
                    detailIdMap.put(keyId, detailId);
                }
            }
        }
        boolean flag = false;
        User user = UserUtils.getUser();
        if (user.getRoleList() != null) {
            for (Role role : user.getRoleList()) {
                if (RoleEnNameEnum.FINANCE.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        Set<String> roleEnNameSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
                roleEnNameSet.add(parse.getState());
            }
        }
        model.addAttribute("roleSet", roleSet);
        model.addAttribute("roleEnNameSet", roleEnNameSet);
        resultMap.put("roleSet", roleSet);
        resultMap.put("roleEnNameSet", roleEnNameSet);

        if (bizOrderHeader.getId() != null) {
            BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
            bizOrderHeaderUnline.setOrderHeader(bizOrderHeader);
            List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
            if (CollectionUtils.isNotEmpty(unlineList)) {
                model.addAttribute("unlineList", unlineList);
                resultMap.put("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());
        List<BizOrderStatus> statusList = bizOrderStatusService.findList(bizOrderStatus);
        statusList.sort(Comparator.comparing(BizOrderStatus::getCreateDate).thenComparing(BizOrderStatus::getId));

        Map<Integer, OrderHeaderBizStatusEnum> statusMap = OrderHeaderBizStatusEnum.getStatusMap();
        Map<Integer, String> stateDescMap = OrderHeaderBizStatusEnum.getStateDescMap();

        String statuPath = request.getParameter("statu");
        model.addAttribute("statuPath", statuPath);
        resultMap.put("statuPath", statuPath);

        String refundSkip = request.getParameter("refundSkip");
        model.addAttribute("refundSkip", refundSkip);
        resultMap.put("refundSkip", refundSkip);

        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        resultMap.put("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        String drawbackStatusStr = request.getParameter("drawbackStatus");
        if (StringUtils.isNotBlank(drawbackStatusStr)) {
            Integer drawbackStatus = Integer.valueOf(drawbackStatusStr);
            BizDrawBack bizDrawBack = new BizDrawBack();
            bizDrawBack.setDrawbackStatus(drawbackStatus);
            bizOrderHeader.setDrawBack(bizDrawBack);
        }
        model.addAttribute("bizOrderHeader", bizOrderHeader);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("statusList", statusList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);
        model.addAttribute("statusMap", statusMap);


        resultMap.put("bizOrderHeader", bizOrderHeader);
        resultMap.put("ordDetailList", ordDetailList);
        resultMap.put("statusList", statusList);
        resultMap.put("orderNumMap", orderNumMap);
        resultMap.put("detailIdMap", detailIdMap);
        resultMap.put("statusMap", statusMap);
        resultMap.put("stateDescMap", stateDescMap);

        //图片处理
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.UNlINE_REFUND_VOUCHER.getCode());
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName("biz_order_header");
        if (bizOrderHeader.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            String photos = "";
            Map<String, Integer> photosMap = new LinkedHashMap<>();

            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer() + img.getImgPath(), img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizOrderHeader.setPhotos(photos);
            }
            if (imgList != null && !imgList.isEmpty()) {
                model.addAttribute("photosMap", photosMap);
                resultMap.put("photosMap", photosMap);
            }
        }

        if ("audit".equalsIgnoreCase(bizOrderHeaderTwo.getStr()) && bizOrderHeaderTwo.getBizPoHeader() != null && bizOrderHeaderTwo.getBizPoHeader().getCommonProcess() != null) {
            com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get().getProcessMap().get(Integer.valueOf(bizOrderHeaderTwo.getBizPoHeader().getCommonProcess().getType()));
            model.addAttribute("purchaseOrderProcess", purchaseOrderProcess);
            resultMap.put("purchaseOrderProcess", purchaseOrderProcess);
        }

        // type = 0 产地直发
        // type = 1 本地备货
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
        commonProcessEntity.setObjectName("0".equals(type) ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        if (bizOrderHeader.getOrderNum().startsWith("DO")) {
            commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        }
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);

        List<CommonProcessEntity> poAuditList = null;
        if (bizOrderHeader.getId() != null) {
            BizPoHeader bizPoHeader = new BizPoHeader();
            bizPoHeader.setBizOrderHeader(bizOrderHeader);
            List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);

            if (CollectionUtils.isNotEmpty(poList)) {
                bizPoHeader = poList.get(0);
                CommonProcessEntity poCommonProcessEntity = new CommonProcessEntity();
                poCommonProcessEntity.setObjectId(String.valueOf(bizPoHeader.getId()));
                poCommonProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
                poAuditList = commonProcessService.findList(poCommonProcessEntity);

                BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
                bizPoPaymentOrder.setPoHeaderId(poList.get(0).getId());
                List<BizPoPaymentOrder> bizPoPaymentOrderList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
                BigDecimal totalPayTotal = new BigDecimal(String.valueOf(BigDecimal.ZERO));
                for (BizPoPaymentOrder poPaymentOrder :bizPoPaymentOrderList) {
                    BigDecimal payTotal = poPaymentOrder.getPayTotal();
                    totalPayTotal = totalPayTotal.add(payTotal);
                }
                model.addAttribute("totalPayTotal", totalPayTotal);
            }
        }

        if (CollectionUtils.isNotEmpty(poAuditList) && CollectionUtils.isNotEmpty(list)) {
            if (bizOrderHeader.getOrderNum().startsWith("DO")) {
                if (String.valueOf(ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get().getAutProcessId()).equals(list.get(list.size() - 1).getType())) {
                    list.remove(list.size() - 1);
                    list.addAll(poAuditList);
                    list.get(list.size() - 1).setCurrent(1);
                }
            }
            if ((bizOrderHeader.getOrderNum().startsWith("SO") || bizOrderHeader.getOrderNum().startsWith("RO")) && ("666".equals(list.get(list.size() - 1).getType()) || "777".equals(list.get(list.size() - 1).getType()))) {
                list.remove(list.size() - 1);
                list.addAll(poAuditList);
                list.get(list.size() - 1).setCurrent(1);
            }
        }

        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> currentList = commonProcessService.findList(commonProcessEntity);

        request.setAttribute("id", bizOrderHeader.getId());
        request.setAttribute("auditList", list);
        request.setAttribute("currentAuditStatus", CollectionUtils.isNotEmpty(currentList) ? currentList.get(0) : new CommonProcessEntity());
        request.setAttribute("type", type);
        request.setAttribute("processMap", "0".equals(type) ?
                ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap()
                : ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap());
//        }
        resultMap.put("id", bizOrderHeader.getId());
        resultMap.put("auditList", list);
        resultMap.put("currentAuditStatus", CollectionUtils.isNotEmpty(currentList) ? currentList.get(0) : new CommonProcessEntity());
        resultMap.put("type", type);
        resultMap.put("processMap", "0".equals(type) ?
                ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap()
                : ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap());


        String createPo = "no";
        Integer orderType = bizOrderHeader.getOrderType();
        OrderPayProportionStatusEnum statusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
        Integer statusEnumState = statusEnum.getState();
        request.setAttribute("statusEnumState", statusEnumState);
        resultMap.put("statusEnumState", statusEnumState);
        if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(orderType)){
            if (bizOrderHeader.getCommonProcess() != null && ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get().getCreatePoProcessId().toString().equals(bizOrderHeader.getCommonProcess().getType())) {
                createPo = "yes";
            }
        } else if ((bizOrderHeader.getOrderNum().startsWith("SO") || bizOrderHeader.getOrderNum().startsWith("RO")) && bizOrderHeader.getCommonProcess() != null && (bizOrderHeader.getSuplys() ==0 || bizOrderHeader.getSuplys() == 721)) {
            String processType = bizOrderHeader.getCommonProcess().getType();
            String zeroCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getZeroCreatePoProcessId());
            String fifthCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getFifthCreatePoProcessId());
            String allCreatePoProcessId = String.valueOf(ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getAllCreatePoProcessId());
            if (zeroCreatePoProcessId.equals(processType) || fifthCreatePoProcessId.equals(processType) || allCreatePoProcessId.equals(processType)) {
                createPo = "yes";
            }
        }
        model.addAttribute("createPo",createPo);
        resultMap.put("createPo", createPo);
        resultMap.put("PURCHASE_ORDER", BizOrderTypeEnum.PURCHASE_ORDER.getState());
        //展示库存数量
        Map<Integer,Integer> invSkuNumMap = bizOrderHeaderService.getInvSkuNum(bizOrderHeader);
        resultMap.put("invSkuNumMap",invSkuNumMap);

        //页面常量值获取
        resultMap.put("SUPPLYING", OrderHeaderBizStatusEnum.SUPPLYING.getState());
        resultMap.put("UNAPPROVE", OrderHeaderBizStatusEnum.UNAPPROVE.getState());
        resultMap.put("PURSEHANGER", DefaultPropEnum.PURSEHANGER.getPropValue());
        resultMap.put("REFUND", OrderHeaderDrawBackStatusEnum.REFUND.getState());
        resultMap.put("REFUNDING", OrderHeaderDrawBackStatusEnum.REFUNDING.getState());
        resultMap.put("PURCHASE_ORDER", BizOrderTypeEnum.PURCHASE_ORDER.getState());
        resultMap.put("ORDINARY_ORDER", BizOrderTypeEnum.ORDINARY_ORDER.getState());
        resultMap.put("COMMISSION_ORDER", BizOrderTypeEnum.COMMISSION_ORDER.getState());

        return JsonUtil.generateData(resultMap, null);
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

    @RequiresPermissions("biz:order:bizOrderHeader:audit")
    @RequestMapping(value = "audit")
    @ResponseBody
    public String audit(HttpServletRequest request, HttpServletResponse response, int id, String currentType, int auditType, String description, String createPo, String lastPayDateVal) {
        try {
            Pair<Boolean, String> audit = null;
            audit = auditFifty(request, response, id, currentType, auditType, description, createPo, lastPayDateVal);
            if (audit.getLeft()) {
                return JsonUtil.generateData(audit.getRight(), null);
            }
            return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, audit.getRight(), null);
        }catch (Exception e) {
            LOGGER.error("audit so error ", e);
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "操作失败,发生异常,请联系技术部", null);
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "refundReject")
    public boolean refundReject(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        String str = checkStatus;
        Integer idt = id;
        bizOrderHeader = bizOrderHeaderService.get(idt);
        BizDrawBack bizDrawBack = new BizDrawBack();
        bizDrawBack.setDrawbackStatus(Integer.parseInt(checkStatus));
        bizOrderHeader.setDrawBack(bizDrawBack);
        boolean boo = false;
        try {
            bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveDrawStatus")
    public boolean saveDrawStatus(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        bizOrderHeader = bizOrderHeaderService.get(id);
        BizDrawBack bizDrawBack = new BizDrawBack();
        bizDrawBack.setDrawbackStatus(Integer.parseInt(checkStatus));
        bizOrderHeader.setDrawBack(bizDrawBack);
        boolean boo = false;
        try {
            bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }


    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "save")
    public String save(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model, null, null, request, response);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            //后台默认保存为 系统后台订单
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        String statuPath = request.getParameter("statuPath");
        Boolean cancleFlag = false;
        if (OrderHeaderBizStatusEnum.CANCLE.getState().equals(bizOrderHeader.getBizStatus())) {
            cancleFlag = true;
        }
        if (bizOrderHeader.getId() != null) {
            OrderPayProportionStatusEnum statusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
            if (bizOrderHeader.getOrderNum().startsWith("DO")) {
                if (OrderPayProportionStatusEnum.ALL == statusEnum || OrderPayProportionStatusEnum.FIFTH == statusEnum) {
                    bizOrderHeaderService.saveCommonProcess(statusEnum, bizOrderHeader, Boolean.TRUE, cancleFlag);
                }
            }

            if (bizOrderHeader.getOrderNum().startsWith("SO")) {
                genAuditProcess(statusEnum, bizOrderHeader, Boolean.TRUE, cancleFlag);
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
            for (BizOrderDetail orderDetail : orderDetailList) {
                bizPoOrderReq.setSoLineNo(orderDetail.getLineNo());
                bizPoOrderReq.setOrderHeader(orderDetail.getOrderHeader());
                bizPoOrderReq.setSoType((byte) 1);
                List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                if (poOrderReqList != null && poOrderReqList.size() > 0) {
                    BizPoOrderReq poOrderReq = poOrderReqList.get(0);
                    BizPoHeader poHeader = poOrderReq.getPoHeader();
                    poHeader.setDelFlag("0");
                    poOrderReq.setDelFlag("0");
                    bizPoHeaderService.save(poHeader);
                    bizPoOrderReqService.save(poOrderReq);
                }
            }
//            BizPoHeader bizPoHeader = new BizPoHeader();
//            bizPoHeader.setBizOrderHeader(bizOrderHeader);
//            List<BizPoHeader> poList = bizPoHeaderService.findList(bizPoHeader);
//            if (CollectionUtils.isNotEmpty(poList)) {
//                bizPoHeaderService.updateProcessToInitAudit(poList.get(0), StringUtils.EMPTY);
//            }
        }

        bizOrderHeaderService.save(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        if (bizOrderHeader.getClientModify() != null && "client_modify".equals(bizOrderHeader.getClientModify())) {
//			保存跳回客户专员
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=" + bizOrderHeader.getConsultantId();
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?statu=" + statuPath + "&source=" + bizOrderHeader.getSource();
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "save4mobile")
    @ResponseBody
    public String save4mobile(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (!beanValidator(model, bizOrderHeader)) {
            return JsonUtil.generateErrorData(HttpStatus.SC_BAD_REQUEST, "数据验证失败!", null);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            //后台默认保存为 系统后台订单
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        String statuPath = request.getParameter("statuPath");
        Boolean cancleFlag = false;
        if (OrderHeaderBizStatusEnum.CANCLE.getState().equals(bizOrderHeader.getBizStatus())) {
            cancleFlag = true;
        }
        if (bizOrderHeader.getId() != null) {
            OrderPayProportionStatusEnum statusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
            if (bizOrderHeader.getOrderNum().startsWith("DO")) {
                if (OrderPayProportionStatusEnum.ALL == statusEnum || OrderPayProportionStatusEnum.FIFTH == statusEnum) {
                    bizOrderHeaderService.saveCommonProcess(statusEnum, bizOrderHeader, Boolean.TRUE, cancleFlag);
                }
            }

            if (bizOrderHeader.getOrderNum().startsWith("SO")) {
                genAuditProcess(statusEnum, bizOrderHeader, Boolean.TRUE, cancleFlag);
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
            for (BizOrderDetail orderDetail : orderDetailList) {
                bizPoOrderReq.setSoLineNo(orderDetail.getLineNo());
                bizPoOrderReq.setOrderHeader(orderDetail.getOrderHeader());
                bizPoOrderReq.setSoType((byte) 1);
                List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                if (poOrderReqList != null && poOrderReqList.size() > 0) {
                    BizPoOrderReq poOrderReq = poOrderReqList.get(0);
                    BizPoHeader poHeader = poOrderReq.getPoHeader();
                    poHeader.setDelFlag("0");
                    poOrderReq.setDelFlag("0");
                    bizPoHeaderService.save(poHeader);
                    bizPoOrderReqService.save(poOrderReq);
                }
            }
        }

        bizOrderHeaderService.save(bizOrderHeader);
        return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
    }

    @RequiresPermissions("biz:order:bizOrderHeader:doRefund")
    @RequestMapping(value = "saveRefund")
    public String saveRefund(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request, HttpServletResponse response) {
        if (!beanValidator(model, bizOrderHeader)) {
            return form(bizOrderHeader, model, null, null, request, response);
        }
        Double receiveTotal = (-1) * (bizOrderHeaderService.get(bizOrderHeader.getId()).getReceiveTotal());
        bizOrderHeaderService.save(bizOrderHeader);
        String drawbackStatusStr = request.getParameter("drawbackStatus");
        if (StringUtils.isNotBlank(drawbackStatusStr)) {
            Integer drawbackStatus = Integer.valueOf(drawbackStatusStr);
            BizDrawBack bizDrawBack = new BizDrawBack();
            bizDrawBack.setDrawbackStatus(drawbackStatus);
            bizOrderHeader.setDrawBack(bizDrawBack);
        }
        bizOrderHeaderService.updateDrawbackStatus(bizOrderHeader);
        User user = UserUtils.getUser();
        BizPayRecord bizPayRecord = new BizPayRecord();
        // 支付编号 *同订单号*
        bizPayRecord.setPayNum(bizOrderHeader.getOrderNum());
        // 订单编号
        bizPayRecord.setOrderNum(bizOrderHeader.getOrderNum());
        // 支付人
        bizPayRecord.setPayer(user.getId());
        // 客户ID
        bizPayRecord.setCustomer(bizOrderHeader.getCustomer());
        // 支付到账户
        bizPayRecord.setToAccount("1");
        // 交易类型：充值、提现、支付
        bizPayRecord.setRecordType(TradeTypeEnum.REFUND_PAY_TYPE.getCode());
        bizPayRecord.setRecordTypeName(TradeTypeEnum.REFUND_PAY_TYPE.getTradeNoType());
        // 支付类型：wx(微信) alipay(支付宝)
        bizPayRecord.setPayType(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getCode());
        bizPayRecord.setPayTypeName(OutTradeNoTypeEnum.OFFLINE_PAY_TYPE.getMessage());
        bizPayRecord.setPayMoney(receiveTotal);
        bizPayRecord.setBizStatus(1);
        bizPayRecord.setCreateBy(user);
        bizPayRecord.setUpdateBy(user);
        bizPayRecordService.save(bizPayRecord);

        addMessage(redirectAttributes, "保存订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list?statu=refund";
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "delete")
    public String delete(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_DELETE);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "删除订单信息成功");
        if (bizOrderHeader.getFlag() != null && "cendDelete".equals(bizOrderHeader.getFlag())) {
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId() + "&statu=" + bizOrderHeader.getStatu() + "&source=" + bizOrderHeader.getSource();
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "delete4Mobile")
    @ResponseBody
    public String delete4Mobile(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_DELETE);
        bizOrderHeaderService.delete(bizOrderHeader);

        return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "recovery")
    public String recovery(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_NORMAL);
        bizOrderHeaderService.delete(bizOrderHeader);
        addMessage(redirectAttributes, "恢复订单信息成功");
        if (bizOrderHeader.getFlag() != null && "cendRecover".equals(bizOrderHeader.getFlag())) {
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/?repage&customer.id=" + bizOrderHeader.getCustomer().getId() + "&statu=" + bizOrderHeader.getStatu();
    }

    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "recovery4Mobile")
    @ResponseBody
    public String recovery4Mobile(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        bizOrderHeader.setDelFlag(BizOrderHeader.DEL_FLAG_NORMAL);
        bizOrderHeaderService.delete(bizOrderHeader);

        return JsonUtil.generateData(Pair.of(true, "操作成功!"), null);
    }


    /**
     * @param bizOrderHeader
     * @param flag           0为采购中心出库
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderDetail:view")
    @RequestMapping(value = "findByOrderV2")
    public String findByOrderV2(BizOrderHeader bizOrderHeader, String flag, HttpServletRequest request, HttpServletResponse response, Model model) {
        BizOrderHeader order = bizOrderHeaderService.getByOrderNum(bizOrderHeader.getOrderNum());
        if (order != null) {
            order.setConsultantId(null);
            bizOrderHeader = order;
        }
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
        commonProcessEntity.setObjectName(JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> currentList = commonProcessService.findList(commonProcessEntity);
        if (CollectionUtils.isEmpty(currentList)) {
            commonProcessEntity.setCurrent(null);
            List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
            if (CollectionUtils.isEmpty(list)) {
                Map<String, Object> byOrder = findByOrder(bizOrderHeader, flag, request, response, model);

                BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
                Office office = new Office();
                office.setId(order.getCenterId());
                bizInventoryInfo.setCustomer(office);
                List<BizInventoryInfo> inventoryInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                byOrder.put("inventoryInfoList", inventoryInfoList);

                return JsonUtil.generateData(byOrder, null);
            }
        }

        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        if (!localConfig.getPayProcessId().contains(Integer.valueOf(currentList.get(0).getType()))) {
            return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "当前订单未审核完成!", null);
        }

        Map<String, Object> byOrder = findByOrder(bizOrderHeader, flag, request, response, model);
        BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
        Office office = new Office();
        office.setId(order.getCenterId());
        bizInventoryInfo.setCustomer(office);
        List<BizInventoryInfo> inventoryInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
        byOrder.put("inventoryInfoList", inventoryInfoList);

        return JsonUtil.generateData(byOrder, null);
    }

    /**
     * @param bizOrderHeader
     * @param flag           0为采购中心出库
     * @param request
     * @param response
     * @param model
     * @return
     */
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderDetail:view")
    @RequestMapping(value = "findByOrder")
    public Map<String, Object> findByOrder(BizOrderHeader bizOrderHeader, String flag, HttpServletRequest request, HttpServletResponse response, Model model) {

        User user = UserUtils.getUser();
        DefaultProp defaultProp = new DefaultProp();
        defaultProp.setPropKey("vend_center");
        List<DefaultProp> defaultProps = defaultPropService.findList(defaultProp);
        Integer vendCenterId = 0;

        if (defaultProps != null) {
            vendCenterId = Integer.parseInt(defaultProps.get(0).getPropValue());
        }
        if ("0".equals(flag)) {
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.APPROVE.getState());
            List<Role> roleList = user.getRoleList();
            Role role = new Role();
            role.setEnname(RoleEnNameEnum.DEPT.getState());
            if (user.isAdmin() || roleList.contains(role)) {
                bizOrderHeader.setSupplyId(-1); //判断orderDetail不等于0
            } else {
                bizOrderHeader.setSupplyId(user.getCompany() == null ? null : user.getCompany().getId());
            }
        } else {
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());

        }
        bizOrderHeader.setSendGoodsStatus(1);
        List<BizOrderHeader> list = bizOrderHeaderService.findList(bizOrderHeader);
        Map<String, Object> map = new HashMap<String, Object>();
        List<BizOrderHeader> bizOrderHeaderList = Lists.newArrayList();
        BizPoOrderReq bizPoOrderReq = null;
        if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
            bizPoOrderReq = new BizPoOrderReq();
        }

        for (BizOrderHeader orderHeader : list) {
            List<BizOrderDetail> bizOrderDetailList = Lists.newArrayList();
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(orderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
                bizPoOrderReq.setOrderHeader(orderHeader);
            }
            for (BizOrderDetail orderDetail : orderDetailList) {
                if (StringUtils.isNotBlank(flag) && !"0".equals(flag)) {
                    bizPoOrderReq.setSoLineNo(orderDetail.getLineNo());
                    bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                    List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (poOrderReqList != null && poOrderReqList.size() > 0) {
                        BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                        orderDetail.setSkuInfo(skuInfo);
                        bizOrderDetailList.add(orderDetail);
                    }
                } else if (orderDetail.getSuplyis() != null && orderDetail.getSuplyis().getId() != 0 && !orderDetail.getSuplyis().getId().equals(vendCenterId)) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                    orderDetail.setSkuInfo(skuInfo);
                    bizOrderDetailList.add(orderDetail);
                }

            }
            orderHeader.setOrderDetailList(bizOrderDetailList);

            bizOrderHeaderList.add(orderHeader);

        }
        List<BizInventoryInfo> inventoryInfoList = bizInventoryInfoService.findList(new BizInventoryInfo());
        map.put("inventoryInfoList", inventoryInfoList);
        map.put("bizOrderHeaderList", bizOrderHeaderList);

        return map;
    }

    /**
     * 用于客户专员页面-订单管理列表中的待审核验证
     **/
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "Commissioner")
    public String commissioner(BizOrderHeader bizOrderHeader, String localOriginType, Integer objJsp) {
        String commis = "comError";
        try {
            if (bizOrderHeader.getId() != null) {
                BizOrderHeader order = bizOrderHeaderService.get(bizOrderHeader);
                OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
                if (order != null) {
                    if (objJsp.equals(OrderHeaderBizStatusEnum.SUPPLYING.getState())) {

                        //order.setPayProportion(orderPayProportionStatusEnum.getState());
                        order.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        //bizOrderStatusService.saveOrderStatus(order);
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SELLORDER.getDesc(),BizOrderStatusOrderTypeEnum.SELLORDER.getState(),order.getId());
                        BizOrderAddress orderAddres = new BizOrderAddress();
                        orderAddres.setOrderHeaderID(order);
                        List<BizOrderAddress> list = bizOrderAddressService.findList(orderAddres);
                        for (BizOrderAddress bizOrderAddress : list) {
                            if (bizOrderAddress.getType() == 2) {
                                orderAddres.setId(bizOrderAddress.getId());
                                break;
                            }
                        }
                        orderAddres.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getProvince() != null && bizOrderHeader.getBizLocation().getCity() != null
                                && bizOrderHeader.getBizLocation().getRegion() != null) {
                            orderAddres.setProvince(bizOrderHeader.getBizLocation().getProvince());
                            orderAddres.setCity(bizOrderHeader.getBizLocation().getCity());
                            orderAddres.setRegion(bizOrderHeader.getBizLocation().getRegion());
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getAddress() != null) {
                            orderAddres.setAddress(bizOrderHeader.getBizLocation().getAddress());
                        } else {
                            orderAddres.setAddress(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getReceiver() != null) {
                            orderAddres.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
                        } else {
                            orderAddres.setReceiver(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getPhone() != null) {
                            orderAddres.setPhone(bizOrderHeader.getBizLocation().getPhone());
                        } else {
                            orderAddres.setPhone(StringUtils.EMPTY);
                        }
                        orderAddres.setType(2);
                        bizOrderAddressService.save(orderAddres);

                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(order);
                        List<BizOrderDetail> detailList = bizOrderDetailService.findList(bizOrderDetail);
                        for (BizOrderDetail b : detailList) {
                            if ("0".equals(localOriginType)) {
                                b.setSuplyis(officeService.get(0));
                            } else {
                                BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(order.getCustomer().getId());
                                b.setSuplyis(officeService.get(bizCustomCenterConsultant.getCenters().getId()));
                            }
                            bizOrderDetailService.saveStatus(b);
                        }

                        commis = "ok";
                    } else if (objJsp.equals(OrderHeaderBizStatusEnum.UNAPPROVE.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        bizOrderStatusService.saveOrderStatus(order);
                    }
                }
                if ("ok".equals(commis)) {
                    if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(bizOrderHeader.getOrderType())) {
                        bizOrderHeaderService.saveCommonProcess(orderPayProportionStatusEnum, bizOrderHeader, Boolean.FALSE, Boolean.FALSE);
                    } else {
                        genAuditProcess(orderPayProportionStatusEnum, bizOrderHeader, Boolean.FALSE, Boolean.FALSE);
                    }

                    //同意发货成功，发送站内信
                    String orderNum = bizOrderHeader.getOrderNum();
                    String title = "订单" + orderNum + "审核通过";
                    String content = "您好，您的订单" + orderNum + "审核通过";
                    bizMessageInfoService.autoSendMessageInfo(title, content, bizOrderHeader.getCustomer().getId(), "orderHeader");
                }
            }
        } catch (Exception e) {
            commis = "comError";
            e.printStackTrace();
        }
        return commis;
    }


    /**
     * 用于客户专员页面-订单管理列表中的待审核验证
     **/
    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "Commissioner4mobile")
    public String commissioner4mobile(BizOrderHeader bizOrderHeader, String localOriginType, Integer objJsp) {
        String commis = "comError";
        try {
            if (bizOrderHeader.getId() != null) {
                BizOrderHeader order = bizOrderHeaderService.get(bizOrderHeader);
                OrderPayProportionStatusEnum orderPayProportionStatusEnum = OrderPayProportionStatusEnum.parse(bizOrderHeader);
                if (order != null) {
                    if (objJsp.equals(OrderHeaderBizStatusEnum.SUPPLYING.getState())) {

                        //order.setPayProportion(orderPayProportionStatusEnum.getState());
                        order.setBizStatus(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        //bizOrderStatusService.saveOrderStatus(order);
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SELLORDER.getDesc(),BizOrderStatusOrderTypeEnum.SELLORDER.getState(),order.getId());
                        BizOrderAddress orderAddres = new BizOrderAddress();
                        orderAddres.setOrderHeaderID(order);
                        List<BizOrderAddress> list = bizOrderAddressService.findList(orderAddres);
                        for (BizOrderAddress bizOrderAddress : list) {
                            if (bizOrderAddress.getType() == 2) {
                                orderAddres.setId(bizOrderAddress.getId());
                                break;
                            }
                        }
                        orderAddres.setAppointedTime(bizOrderHeader.getBizLocation().getAppointedTime());
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getProvince() != null && bizOrderHeader.getBizLocation().getCity() != null
                                && bizOrderHeader.getBizLocation().getRegion() != null) {
                            orderAddres.setProvince(bizOrderHeader.getBizLocation().getProvince());
                            orderAddres.setCity(bizOrderHeader.getBizLocation().getCity());
                            orderAddres.setRegion(bizOrderHeader.getBizLocation().getRegion());
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getAddress() != null) {
                            orderAddres.setAddress(bizOrderHeader.getBizLocation().getAddress());
                        } else {
                            orderAddres.setAddress(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getReceiver() != null) {
                            orderAddres.setReceiver(bizOrderHeader.getBizLocation().getReceiver());
                        } else {
                            orderAddres.setReceiver(StringUtils.EMPTY);
                        }
                        if (bizOrderHeader.getBizLocation() != null && bizOrderHeader.getBizLocation().getPhone() != null) {
                            orderAddres.setPhone(bizOrderHeader.getBizLocation().getPhone());
                        } else {
                            orderAddres.setPhone(StringUtils.EMPTY);
                        }
                        orderAddres.setType(2);
                        bizOrderAddressService.save(orderAddres);

                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(order);
                        List<BizOrderDetail> detailList = bizOrderDetailService.findList(bizOrderDetail);
                        for (BizOrderDetail b : detailList) {
                            if ("0".equals(localOriginType)) {
                                b.setSuplyis(officeService.get(0));
                            } else {
                                BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(order.getCustomer().getId());
                                b.setSuplyis(officeService.get(bizCustomCenterConsultant.getCenters().getId()));
                            }
                            bizOrderDetailService.saveStatus(b);
                        }

                        commis = "ok";
                    } else if (objJsp.equals(OrderHeaderBizStatusEnum.UNAPPROVE.getState())) {
                        order.setBizStatus(OrderHeaderBizStatusEnum.UNAPPROVE.getState());
                        bizOrderHeaderService.saveOrderHeader(order);
                        bizOrderStatusService.saveOrderStatus(order);
                    }
                }
                if ("ok".equals(commis)) {
                    if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(bizOrderHeader.getOrderType())) {
                        bizOrderHeaderService.saveCommonProcess(orderPayProportionStatusEnum, bizOrderHeader, Boolean.FALSE, Boolean.FALSE);
                    } else {
                        genAuditProcess(orderPayProportionStatusEnum, bizOrderHeader, Boolean.FALSE, Boolean.FALSE);
                    }
                }
            }
        } catch (Exception e) {
            commis = "comError";
            e.printStackTrace();
        }
        return JsonUtil.generateData(commis, null);
    }

    /**
     * 生成审批流程
     *
     * @param orderPayProportionStatusEnum
     * @param bizOrderHeader
     */
    private void genAuditProcess(OrderPayProportionStatusEnum orderPayProportionStatusEnum, BizOrderHeader bizOrderHeader, boolean reGen, boolean cancleFlag) {
        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();

        // 产地直发
        CommonProcessEntity originEntity = new CommonProcessEntity();
        originEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
        originEntity.setObjectName(JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME);
        List<CommonProcessEntity> originList = commonProcessService.findList(originEntity);
        Integer code = 0;
        StringBuilder phone = new StringBuilder();
        com.wanhutong.backend.modules.config.parse.Process currentProcess = null;
        if (CollectionUtils.isEmpty(originList) || reGen) {
            originEntity.setCurrent(1);
            switch (orderPayProportionStatusEnum) {
                case ZERO:
                    originEntity.setType(String.valueOf(originConfig.getZeroDefaultProcessId()));
                    code = originConfig.getZeroDefaultProcessId();
                    break;
                case FIFTH:
                    originEntity.setType(String.valueOf(originConfig.getFifthDefaultProcessId()));
                    code = originConfig.getFifthDefaultProcessId();
                    break;
                case ALL:
                    originEntity.setType(String.valueOf(originConfig.getAllDefaultProcessId()));
                    code = originConfig.getAllDefaultProcessId();
                    break;
                default:
                    break;
            }
            List<CommonProcessEntity> list = commonProcessService.findList(originEntity);
            if (CollectionUtils.isEmpty(list)) {
                commonProcessService.updateCurrentByObject(bizOrderHeader.getId(), JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME, 0);
                commonProcessService.save(originEntity);

                //自动发送短信
                currentProcess = originConfig.getProcessMap().get(code);
            }
        }

        // 本地备货
        CommonProcessEntity localEntity = new CommonProcessEntity();
        localEntity.setObjectId(String.valueOf(bizOrderHeader.getId()));
        localEntity.setObjectName(JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        List<CommonProcessEntity> localList = commonProcessService.findList(localEntity);
        if (CollectionUtils.isEmpty(localList) || reGen) {
            localEntity.setCurrent(1);
            switch (orderPayProportionStatusEnum) {
                case ZERO:
                    localEntity.setType(String.valueOf(localConfig.getZeroDefaultProcessId()));
                    code = localConfig.getZeroDefaultProcessId();
                    break;
                case FIFTH:
                    localEntity.setType(String.valueOf(localConfig.getFifthDefaultProcessId()));
                    code = localConfig.getFifthDefaultProcessId();
                    break;
                case ALL:
                    localEntity.setType(String.valueOf(localConfig.getAllDefaultProcessId()));
                    code = localConfig.getAllDefaultProcessId();
                    break;
                default:
                    break;
            }
            List<CommonProcessEntity> list = commonProcessService.findList(localEntity);
            if (CollectionUtils.isEmpty(list)) {
                commonProcessService.updateCurrentByObject(bizOrderHeader.getId(), JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME, 0);
                commonProcessService.save(localEntity);

                //自动发送短信
                currentProcess = localConfig.getProcessMap().get(code);
            }
        }

        if (!cancleFlag) {
            if (currentProcess != null && currentProcess.getRoleEnNameEnum() != null && currentProcess.getRoleEnNameEnum().get(0) != null) {
                User sendUser = new User(systemService.getRoleByEnname(currentProcess.getRoleEnNameEnum().get(0).toLowerCase()));
                //不根据采购中心区分渠道经理，所以注释掉该行
                //sendUser.setCent(user.getCompany());
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
                            ImmutableMap.of("order","联营订单", "orderNum", bizOrderHeader.getOrderNum()));
                }
            }
        }

    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveBizOrderHeader")
    public String saveBizOrderHeader(BizOrderHeader bizOrderHeader, Integer orderId, Double money) {
        String flag = "";
        try {
            BizOrderHeader orderHeader = bizOrderHeaderService.get(orderId);
            orderHeader.setTotalExp(money);
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            flag = "ok";

        } catch (Exception e) {
            flag = "error";
            logger.error(e.getMessage());
        }
        return flag;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveBizOrderHeader4Mobile")
    public String saveBizOrderHeader4Mobile(BizOrderHeader bizOrderHeader, Integer orderId, Double money) {
        Map<String, Object> resultMap = Maps.newHashMap();
        String flag = "";
        try {
            BizOrderHeader orderHeader = bizOrderHeaderService.get(orderId);
            orderHeader.setTotalExp(money);
            bizOrderHeaderService.saveOrderHeader(orderHeader);
            flag = "ok";

        } catch (Exception e) {
            flag = "error";
            logger.error(e.getMessage());
        }
        resultMap.put("flag",flag);
        return JsonUtil.generateData(resultMap, null);
    }

    /**
     * 导出订单数据
     *
     * @return ouyang
     * 2018-03-27
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "orderHeaderExport", method = RequestMethod.POST)
    public String orderHeaderExportFile(BizOrderHeader bizOrderHeader, String cendExportbs, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        //判断当前用户是否拥有查看佣金的权限
        Boolean permFlag = RoleUtils.hasPermission("biz:order:buyPrice:view");

        //判断当前用户是否拥有查看结算价的权限
        Boolean showUnitPriceFlag = RoleUtils.hasPermission("biz:order:unitPrice:view");

        try {
            DecimalFormat df = new DecimalFormat();
            BizOrderDetail orderDetail = new BizOrderDetail();
            BizPayRecord bizPayRecord = new BizPayRecord();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String fileName = "订单数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            List<BizOrderHeader> pageList = null;
            if (cendExportbs != null && "cend_listPage".equals(cendExportbs)) {
                //C端导出
                bizOrderHeader.setDataStatus("filter");
                Page<BizOrderHeader> bizOrderHeaderPage = bizOrderHeaderService.cendfindPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
                pageList = bizOrderHeaderPage.getList();
            } else {
                pageList = bizOrderHeaderService.findListExport(bizOrderHeader);
            }
            List<List<String>> data = new ArrayList<List<String>>();
            List<List<String>> detailData = new ArrayList<List<String>>();
            for (BizOrderHeader order : pageList) {
                bizPayRecord.setOrderNum(order.getOrderNum());
                List<BizPayRecord> payList = bizPayRecordService.findList(bizPayRecord);
                order.setBizLocation(bizOrderAddressService.get(order.getBizLocation().getId()));
                List<BizOrderDetail> list = new ArrayList<>();
                if (!order.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                    orderDetail.setOrderHeader(order);
                    list = bizOrderDetailService.findList(orderDetail);
                } else {
                    order.setTotalBuyPrice(order.getTotalDetail());
                }
                if (CollectionUtils.isEmpty(payList)) {
                    if (CollectionUtils.isNotEmpty(list)) {
                        Double dou = 0.0;
                        for (BizOrderDetail detail : list) {
                            double buy = 0.0;
                            int ord = 0;
                            if (detail.getBuyPrice() != null) {
                                buy = detail.getBuyPrice();
                            }
                            if (detail.getOrdQty() != null) {
                                ord = detail.getOrdQty();
                            }
                            dou += buy * ord;

                            List<String> detailListData = Lists.newArrayList();
                            detailListData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                            detailListData.add(detail.getSkuName() == null ? StringUtils.EMPTY : detail.getSkuName());
                            detailListData.add(detail.getPartNo() == null ? StringUtils.EMPTY : detail.getPartNo());
                            //供应商
                            if (detail.getVendor() != null && detail.getVendor().getName() != null) {
                                detailListData.add(detail.getVendor().getName());
                            } else {
                                detailListData.add(StringUtils.EMPTY);
                            }
                            detailListData.add(detail.getUnitPrice() == null ? StringUtils.EMPTY : String.valueOf(detail.getUnitPrice()));
                            //隐藏结算价
                            if (showUnitPriceFlag) {
                                detailListData.add(detail.getBuyPrice() == null ? StringUtils.EMPTY : String.valueOf(detail.getBuyPrice()));
                            }
                            detailListData.add(detail.getOrdQty() == null ? StringUtils.EMPTY : String.valueOf(detail.getOrdQty()));
                            //商品总价
                            double unitPrice = 0.0;
                            if (detail.getUnitPrice() != null) {
                                unitPrice = detail.getUnitPrice();
                            }
                            detailListData.add(String.valueOf(df.format(unitPrice * ord)));
                            detailData.add(detailListData);
                        }
                        order.setTotalBuyPrice(dou);
                    }

                    List<String> rowData = Lists.newArrayList();
                    //订单编号
                    rowData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                    //描述
                    Dict dict = new Dict();
                    dict.setDescription("订单类型");
                    dict.setType("biz_order_type");
                    List<Dict> dictList = dictService.findList(dict);
                    for (Dict di : dictList) {
                        if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                            //订单类型
                            rowData.add(di.getLabel());
                            break;
                        }
                    }
                    //经销店名称/电话
                    if (order.getCustomer() != null && order.getCustomer().getName() != null || order.getCustomer().getPhone() != null) {
                        rowData.add(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")");
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    //所属采购中心
                    rowData.add(order.getCentersName() == null ? StringUtils.EMPTY : order.getCentersName());
                    //所属客户专员
                    if (order.getCon() != null && order.getCon().getName() != null) {
                        rowData.add(order.getCon().getName());
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    if (order.getTotalDetail() != null) {
                        rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    //商品结算总价
                    if (order.getTotalBuyPrice() != null) {
                        rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    rowData.add(order.getTotalExp() == null ? StringUtils.EMPTY : String.valueOf(order.getTotalExp()));
                    rowData.add(order.getFreight() == null ? StringUtils.EMPTY : String.valueOf(order.getFreight()));
                    //是否万户通发货
                    if (order.getBizOrderLogistics() != null && order.getBizOrderLogistics().getLogisticsLines() != null) {
                        if (order.getBizOrderLogistics().getLogisticsLines().equals(BizOrderLogisticsEnum.CUSTOMER_PICK_UP.getDesc())
                                || order.getBizOrderLogistics().getLogisticsLines().equals(BizOrderLogisticsEnum.DELIVER_HOME.getDesc())) {
                            rowData.add("是");
                        } else {
                            rowData.add("否");
                        }
                    } else {
                        rowData.add("否");
                    }
                    double total = 0.0;
                    double exp = 0.0;
                    double fre = 0.0;
                    double buy = 0.0;
                    double serviceFee = 0.0;
                    if (order.getTotalBuyPrice() != null) {
                        buy = order.getTotalBuyPrice();
                    }
                    if (order.getTotalDetail() != null) {
                        total = order.getTotalDetail();
                    }
                    if (order.getTotalExp() != null) {
                        exp = order.getTotalExp();
                    }
                    if (order.getFreight() != null) {
                        fre = order.getFreight();
                    }
                    if (order.getServiceFee() != null) {
                        serviceFee = order.getServiceFee();
                    }
                    double sumTotal = total + exp + fre + serviceFee;
                    rowData.add(String.valueOf(sumTotal));
                    rowData.add(order.getReceiveTotal() == null ? StringUtils.EMPTY : String.valueOf(order.getReceiveTotal()));
                    double receiveTotal = order.getReceiveTotal() == null ? 0.0 : order.getReceiveTotal() + (order.getScoreMoney() == null ? 0.0 : order.getScoreMoney().doubleValue());
                    if (!OrderHeaderBizStatusEnum.EXPORT_TAIL.contains(OrderHeaderBizStatusEnum.stateOf(order.getBizStatus())) && sumTotal > receiveTotal) {
                        //尾款信息
                        rowData.add("有尾款");
                    } else {
                        rowData.add(StringUtils.EMPTY);
                    }
                    rowData.add(order.getScoreMoney().toString());
                    //利润
                    //                        orderHeader.totalExp+orderHeader.serviceFee+orderHeader.freight
                    rowData.add(df.format(exp + (order.getServiceFee() == null ? 0 : order.getServiceFee()) + fre));
                    // 佣金
                    //                        orderHeader.totalDetail-orderHeader.totalBuyPrice
                    //隐藏佣金
                    if (permFlag) {
                        rowData.add(df.format(total - buy));
                    }
                    Dict dictInv = new Dict();
                    dictInv.setDescription("发票状态");
                    dictInv.setType("biz_order_invStatus");
                    List<Dict> dictListInv = dictService.findList(dictInv);
                    for (Dict dinv : dictListInv) {
                        if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
                            //发票状态
                            rowData.add(dinv.getLabel());
                            break;
                        }
                    }
                    Dict dictBiz = new Dict();
                    dictBiz.setDescription("业务状态");
                    dictBiz.setType("biz_order_status");
                    List<Dict> dictListBiz = dictService.findList(dictBiz);
                    for (Dict dbiz : dictListBiz) {
                        if (dbiz.getValue().equals(String.valueOf(order.getBizStatus()))) {
                            //业务状态
                            rowData.add(dbiz.getLabel());
                            break;
                        }
                    }
                    //订单创建时间
                    rowData.add(sdf.format(order.getCreateDate()));
                    data.add(rowData);
                }
                if (CollectionUtils.isNotEmpty(payList)) {

                    for (BizPayRecord p : payList) {
                        Double douSum = 0.0;
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (BizOrderDetail d : list) {
                                double buy = 0.0;
                                double ord = 0;
                                if (d.getBuyPrice() != null) {
                                    buy = d.getBuyPrice();
                                }
                                if (d.getOrdQty() != null) {
                                    ord = d.getOrdQty();
                                }
                                douSum += buy * ord;
                                List<String> detailListData = Lists.newArrayList();
                                detailListData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                                detailListData.add(d.getSkuName() == null ? StringUtils.EMPTY : d.getSkuName());
                                detailListData.add(d.getPartNo() == null ? StringUtils.EMPTY : d.getPartNo());
                                //供应商
                                if (d.getVendor() != null && d.getVendor().getName() != null) {
                                    detailListData.add(d.getVendor().getName());
                                } else {
                                    detailListData.add(StringUtils.EMPTY);
                                }
                                detailListData.add(d.getUnitPrice() == null ? StringUtils.EMPTY : String.valueOf(d.getUnitPrice()));
                                //隐藏结算价
                                if (showUnitPriceFlag) {
                                    detailListData.add(d.getBuyPrice() == null ? StringUtils.EMPTY : String.valueOf(d.getBuyPrice()));
                                }
                                detailListData.add(d.getOrdQty() == null ? StringUtils.EMPTY : String.valueOf(d.getOrdQty()));
                                //商品总价
                                double unPri = 0.0;
                                int ordQty = 0;
                                if (d.getUnitPrice() != null) {
                                    unPri = d.getUnitPrice();
                                }
                                if (d.getOrdQty() != null) {
                                    ordQty = d.getOrdQty();
                                }
                                detailListData.add(String.valueOf(df.format(unPri * ordQty)));
                                detailData.add(detailListData);
                            }
                            order.setTotalBuyPrice(douSum);
                        }
                        //地址查询
                        List<String> rowData = Lists.newArrayList();
                        rowData.add(order.getOrderNum() == null ? StringUtils.EMPTY : order.getOrderNum());
                        //描述
                        Dict dict = new Dict();
                        dict.setDescription("订单类型");
                        dict.setType("biz_order_type");
                        List<Dict> dictList = dictService.findList(dict);
                        for (Dict di : dictList) {
                            if (di.getValue().equals(String.valueOf(order.getOrderType()))) {
                                //订单类型
                                rowData.add(di.getLabel());
                                break;
                            }
                        }
                        if (order.getCustomer() != null && order.getCustomer().getName() != null) {
                            rowData.add(order.getCustomer().getName() + "(" + order.getCustomer().getPhone() + ")");
                        }
                        rowData.add(order.getCentersName() == null ? StringUtils.EMPTY : order.getCentersName());
                        //所属客户专员
                        if (order.getCon() != null && order.getCon().getName() != null) {
                            rowData.add(order.getCon().getName());
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        if (order.getTotalDetail() != null) {
                            rowData.add(String.valueOf(df.format(order.getTotalDetail())));
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        //商品结算总价
                        if (order.getTotalBuyPrice() != null) {
                            rowData.add(String.valueOf(df.format(order.getTotalBuyPrice())));
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        rowData.add(order.getTotalExp() == null ? StringUtils.EMPTY : String.valueOf(order.getTotalExp()));
                        rowData.add(order.getFreight() == null ? StringUtils.EMPTY : String.valueOf(order.getFreight()));
                        //是否万户通发货
                        if (order.getBizOrderLogistics() != null && order.getBizOrderLogistics().getLogisticsLines() != null) {
                            if (order.getBizOrderLogistics().getLogisticsLines().equals(BizOrderLogisticsEnum.CUSTOMER_PICK_UP.getDesc())
                                    || order.getBizOrderLogistics().getLogisticsLines().equals(BizOrderLogisticsEnum.DELIVER_HOME.getDesc())) {
                                rowData.add("是");
                            } else {
                                rowData.add("否");
                            }
                        } else {
                            rowData.add("否");
                        }
                        //应付金额
                        double total = 0.0;
                        double exp = 0.0;
                        double fre = 0.0;
                        double buy = 0.0;
                        double serviceFee = 0.0;
                        if (order.getTotalBuyPrice() != null) {
                            buy = order.getTotalBuyPrice();
                        }
                        if (order.getTotalDetail() != null) {
                            total = order.getTotalDetail();
                        }
                        if (order.getTotalExp() != null) {
                            exp = order.getTotalExp();
                        }
                        if (order.getFreight() != null) {
                            fre = order.getFreight();
                        }
                        if (order.getServiceFee() != null) {
                            serviceFee = order.getServiceFee();
                        }
                        double sumTotal = total + exp + fre + serviceFee;
                        rowData.add(String.valueOf(sumTotal));
                        //已收货款
                        rowData.add(String.valueOf(order.getReceiveTotal() == null ? StringUtils.EMPTY : order.getReceiveTotal()));
                        double receiveTotal = order.getReceiveTotal() == null ? 0.0 : order.getReceiveTotal() + (order.getScoreMoney() == null ? 0.0 : order.getScoreMoney().doubleValue());
                        if (!OrderHeaderBizStatusEnum.EXPORT_TAIL.contains(OrderHeaderBizStatusEnum.stateOf(order.getBizStatus())) && sumTotal > receiveTotal) {
                            rowData.add("有尾款");
                        } else {
                            rowData.add(StringUtils.EMPTY);
                        }
                        rowData.add(order.getScoreMoney().toString());
                        //服务费
//                        orderHeader.totalExp+orderHeader.serviceFee+orderHeader.freight
                        rowData.add(df.format(exp + (order.getServiceFee() == null ? 0 : order.getServiceFee()) + fre));
                        // 佣金
//                        orderHeader.totalDetail-orderHeader.totalBuyPrice
                        //隐藏佣金
                        if (permFlag) {
                            rowData.add(df.format(total - buy));
                        }
                        Dict dictInv = new Dict();
                        dictInv.setDescription("发票状态");
                        dictInv.setType("biz_order_invStatus");
                        List<Dict> dictListInv = dictService.findList(dictInv);
                        for (Dict dinv : dictListInv) {
                            if (dinv.getValue().equals(String.valueOf(order.getInvStatus()))) {
                                //发票状态
                                rowData.add(dinv.getLabel());
                                break;
                            }
                        }
                        Dict dictBiz = new Dict();
                        dictBiz.setDescription("业务状态");
                        dictBiz.setType("biz_order_status");
                        List<Dict> dictListBiz = dictService.findList(dictBiz);
                        for (Dict dbiz : dictListBiz) {
                            if (dbiz.getValue().equals(String.valueOf(order.getBizStatus()))) {
                                //业务状态
                                rowData.add(dbiz.getLabel());
                                break;
                            }
                        }
                        //订单创建时间
                        rowData.add(String.valueOf(sdf.format(order.getCreateDate())));
                        if (p.getBizStatus() != null && p.getBizStatus() == 1) {
                            //支付类型名称
                            rowData.add(p.getPayTypeName() == null ? StringUtils.EMPTY : p.getPayTypeName());
                            //支付编号
                            rowData.add(p.getPayNum() == null ? StringUtils.EMPTY : p.getPayNum());
                            rowData.add(p.getOutTradeNo() == null ? StringUtils.EMPTY : p.getOutTradeNo());
                            //支付账号
                            rowData.add(p.getAccount() == null ? StringUtils.EMPTY : p.getAccount());
                            //交易类型名称
                            rowData.add(p.getRecordTypeName() == null ? StringUtils.EMPTY : p.getRecordTypeName());
                            rowData.add(p.getPayMoney() == null ? StringUtils.EMPTY : String.valueOf(p.getPayMoney()));
                            //交易时间
                            rowData.add(String.valueOf(sdf.format(p.getCreateDate())));
                        }
                        data.add(rowData);
                    }
                }
            }
            String[] headers = null;
            //隐藏佣金
            if (permFlag) {
                headers = new String[]{"订单编号", "订单类型", "经销店名称/电话", "所属采购中心", "所属客户专员", "商品总价", "商品结算总价", "调整金额", "运费", "是否万户通发货",
                        "应付金额", "已收货款", "尾款信息", "积分抵扣", "服务费", "佣金", "发票状态", "业务状态", "创建时间", "支付类型名称", "支付编号", "业务流水号", "支付账号", "交易类型名称", "支付金额", "交易时间"};
            } else {
                headers = new String[]{"订单编号", "订单类型", "经销店名称/电话", "所属采购中心", "所属客户专员", "商品总价", "商品结算总价", "调整金额", "运费", "是否万户通发货",
                        "应付金额", "已收货款", "尾款信息", "积分抵扣", "服务费", "发票状态", "业务状态", "创建时间", "支付类型名称", "支付编号", "业务流水号", "支付账号", "交易类型名称", "支付金额", "交易时间"};
            }

            //隐藏结算价
            String[] details = null;
            if (showUnitPriceFlag) {
                details = new String[]{"订单编号", "商品名称", "商品编码", "供应商", "商品单价", "商品结算价", "采购数量", "商品总价"};
            } else {
                details = new String[]{"订单编号", "商品名称", "商品编码", "供应商", "商品单价", "采购数量", "商品总价"};
            }

            OrderHeaderExportExcelUtils eeu = new OrderHeaderExportExcelUtils();
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            eeu.exportExcel(workbook, 0, "订单数据", headers, data, fileName);
            eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
            response.reset();
            response.setContentType("application/octet-stream; charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
            workbook.write(response.getOutputStream());
            workbook.dispose();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            addMessage(redirectAttributes, "导出订单数据失败！失败信息：" + e.getMessage());
        }
        if ("cend_listPage".equals(cendExportbs)) {
            //跳转C端列表
            return "redirect:" + adminPath + "/biz/order/bizOrderHeader/cendList";
        }
        return "redirect:" + adminPath + "/biz/order/bizOrderHeader/";
    }

    /**
     * C端订单列表
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "cendList")
    public String cendList(BizOrderHeader bizOrderHeader, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<BizOrderHeader> page = bizOrderHeaderService.cendfindPage(new Page<BizOrderHeader>(request, response), bizOrderHeader);
        model.addAttribute("page", page);
        return "modules/biz/order/bizOrderHeaderCendList";
    }

    /**
     * C端订单编辑
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "cendform")
    public String cendform(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = new Office();
            User user = systemService.getUser(bizOrderHeader.getCustomer().getId());
            office.setId(user.getId());
            office.setName(user.getName());
            bizOrderHeader.setCustomer(office);
        }
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = orderHeader.getTotalDetail();
            Double totalExp = orderHeader.getTotalExp();
            Double freight = orderHeader.getFreight();
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            //cendForm页面显示待支付总价
            bizOrderHeader.setTobePaid(orderHeaderTotal - orderHeader.getReceiveTotal());
            if (orderNoEditable != null && "editable".equals(orderNoEditable)) {
                orderHeader.setOrderNoEditable("editable");
            }
            if (orderDetails != null && "details".equals(orderDetails)) {
                orderHeader.setOrderDetails("details");
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(orderHeader.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            if (CollectionUtils.isNotEmpty(list)) {
                for (BizOrderAddress orderAddress : list) {
                    //收货地址
                    if (orderAddress.getType() == 1) {
                        model.addAttribute("orderAddress", orderAddress);
                    }
                }
            }
        }
        model.addAttribute("entity", bizOrderHeader);
        return "modules/biz/order/bizOrderHeaderCendForm";
    }

    /**
     * C端订单保存
     */
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "cendSave")
    public String cendSave(BizOrderHeader bizOrderHeader, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, bizOrderHeader)) {
            return cendform(bizOrderHeader, model, null, null);
        }
        if (bizOrderHeader.getPlatformInfo() == null) {
            bizOrderHeader.getPlatformInfo().setId(6);
        }
        bizOrderHeaderService.CendorderSave(bizOrderHeader);
        addMessage(redirectAttributes, "保存订单信息成功");
        return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/cendList";
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "checkTotalExp")
    public String checkTotalExp(BizOrderHeader bizOrderHeader) {
        String flag = "ok".intern();
        BigDecimal totalDetail = BigDecimal.valueOf(bizOrderHeader.getTotalDetail() == null ? 0 : bizOrderHeader.getTotalDetail());
        BigDecimal freight = BigDecimal.valueOf(bizOrderHeader.getFreight() == null ? 0 : bizOrderHeader.getFreight());
        BigDecimal totalExp = BigDecimal.valueOf(bizOrderHeader.getTotalExp() == null ? 0 : -bizOrderHeader.getTotalExp());

        if (totalExp.compareTo(BigDecimal.ZERO) <= 0) {
            return flag;
        }

        if (bizOrderHeader.getId() == null) {
            return flag;
        }

        SystemConfig systemConfig = ConfigGeneral.SYSTEM_CONFIG.get();

        BigDecimal photoOrderRatio = systemConfig.getPhotoOrderRatio();


        boolean allActivityShlef = true;
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (orderHeader != null && BizOrderTypeEnum.PHOTO_ORDER.getState().equals(orderHeader.getOrderType())
                && totalExp.compareTo(totalDetail.multiply(photoOrderRatio)) > 0) {
            return "photoOrder";
        }

        BizOrderDetail orderDetail = new BizOrderDetail();
        orderDetail.setOrderHeader(orderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(orderDetail);
        BigDecimal totalBuyPrice = BigDecimal.ZERO;
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                totalBuyPrice = totalBuyPrice.add(BigDecimal.valueOf(bizOrderDetail.getBuyPrice()).multiply(BigDecimal.valueOf(bizOrderDetail.getOrdQty())));
                if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(orderHeader.getOrderType())) {
                    allActivityShlef = false;
                } else {
                    if (!StringUtils.equals(String.valueOf(systemConfig.getActivityShelfId()), String.valueOf(bizOrderDetail.getShelfInfo().getOpShelfInfo().getId()))) {
                        allActivityShlef = false;
                    }
                }
            }
        }

        String activityDate = systemConfig.getActivityDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(activityDate);
        } catch (ParseException e) {
            LOGGER.error("order edit checkTotalExp error", e);
        }


        if (orderHeader == null || orderHeader.getCustomer() == null || orderHeader.getCustomer().getId() == null) {
            return flag;
        }

        BizOrderHeader header = new BizOrderHeader();
        header.setCustomer(orderHeader.getCustomer());

        User user = UserUtils.getUser();

        BigDecimal resultPrice = totalDetail.subtract(totalExp).add(freight);

        if (parse != null && (System.currentTimeMillis() < parse.getTime()) && allActivityShlef) {
            if (resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.8))) < 0) {
                return "orderLowest8";
            }
            return flag;
        }

        List<String> serviceChargeAudit = systemConfig.getServiceChargeAudit();
        List<String> orderLossAudit = systemConfig.getOrderLossAudit();
        List<String> orderLowestAudit = systemConfig.getOrderLowestAudit();

        boolean serviceChargeStatus = RoleUtils.hasRole(user, serviceChargeAudit);
        //if (!serviceChargeStatus && totalExp.compareTo(totalDetail.subtract((totalBuyPrice).multiply(BigDecimal.valueOf(0.5)))) > 0) {
        BigDecimal totalService = (BigDecimal.valueOf(bizOrderHeader.getServiceFee())).add(BigDecimal.valueOf(bizOrderHeader.getFreight()));
        if (!serviceChargeStatus && totalExp.compareTo(totalService.multiply(BigDecimal.valueOf(0.5))) > 0) {
            return "serviceCharge";
        }

        boolean orderLossStatus = RoleUtils.hasRole(user, orderLossAudit);
        if (!orderLossStatus && resultPrice.compareTo(totalBuyPrice) < 0) {
            return "orderLoss";
        }

        boolean orderLowestStatus = RoleUtils.hasRole(user, orderLowestAudit);
        if (!orderLowestStatus && resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.95))) < 0) {
            return "orderLowest";
        }

        return flag;
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "checkTotalExp4Mobile")
    public String checkTotalExp4Mobile(BizOrderHeader bizOrderHeader) {
        Map<String, Object> resultMap = Maps.newHashMap();
        String flag = "ok".intern();
        BigDecimal totalDetail = BigDecimal.valueOf(bizOrderHeader.getTotalDetail() == null ? 0 : bizOrderHeader.getTotalDetail());
        BigDecimal freight = BigDecimal.valueOf(bizOrderHeader.getFreight() == null ? 0 : bizOrderHeader.getFreight());
        BigDecimal totalExp = BigDecimal.valueOf(bizOrderHeader.getTotalExp() == null ? 0 : -bizOrderHeader.getTotalExp());

        if (totalExp.compareTo(BigDecimal.ZERO) <= 0) {
            resultMap.put("resultValue",flag);
            return JsonUtil.generateData(resultMap, null);
        }

        if (bizOrderHeader.getId() == null) {
            resultMap.put("resultValue",flag);
            return JsonUtil.generateData(resultMap, null);
        }

        SystemConfig systemConfig = ConfigGeneral.SYSTEM_CONFIG.get();

        BigDecimal photoOrderRatio = systemConfig.getPhotoOrderRatio();


        boolean allActivityShlef = true;
        BizOrderHeader orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (orderHeader != null && BizOrderTypeEnum.PHOTO_ORDER.getState().equals(orderHeader.getOrderType())
                && totalExp.compareTo(totalDetail.multiply(photoOrderRatio)) > 0) {
            resultMap.put("resultValue","photoOrder");
            return JsonUtil.generateData(resultMap, null);
        }

        BizOrderDetail orderDetail = new BizOrderDetail();
        orderDetail.setOrderHeader(orderHeader);
        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(orderDetail);
        BigDecimal totalBuyPrice = BigDecimal.ZERO;
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            for (BizOrderDetail bizOrderDetail : orderDetailList) {
                totalBuyPrice = totalBuyPrice.add(BigDecimal.valueOf(bizOrderDetail.getBuyPrice()).multiply(BigDecimal.valueOf(bizOrderDetail.getOrdQty())));
                if (BizOrderTypeEnum.PURCHASE_ORDER.getState().equals(orderHeader.getOrderType())) {
                    allActivityShlef = false;
                } else {
                    if (!StringUtils.equals(String.valueOf(systemConfig.getActivityShelfId()), String.valueOf(bizOrderDetail.getShelfInfo().getOpShelfInfo().getId()))) {
                        allActivityShlef = false;
                    }
                }
            }
        }

        String activityDate = systemConfig.getActivityDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(activityDate);
        } catch (ParseException e) {
            LOGGER.error("order edit checkTotalExp error", e);
        }


        if (orderHeader == null || orderHeader.getCustomer() == null || orderHeader.getCustomer().getId() == null) {
            resultMap.put("resultValue",flag);
            return JsonUtil.generateData(resultMap, null);
        }

        BizOrderHeader header = new BizOrderHeader();
        header.setCustomer(orderHeader.getCustomer());

        User user = UserUtils.getUser();

        BigDecimal resultPrice = totalDetail.subtract(totalExp).add(freight);

        if (parse != null && (System.currentTimeMillis() < parse.getTime()) && allActivityShlef) {
            if (resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.8))) < 0) {
                resultMap.put("resultValue","orderLowest8");
                return JsonUtil.generateData(resultMap, null);
            }
            resultMap.put("resultValue",flag);
            return JsonUtil.generateData(resultMap, null);
        }

        List<String> serviceChargeAudit = systemConfig.getServiceChargeAudit();
        List<String> orderLossAudit = systemConfig.getOrderLossAudit();
        List<String> orderLowestAudit = systemConfig.getOrderLowestAudit();

        boolean serviceChargeStatus = RoleUtils.hasRole(user, serviceChargeAudit);
        //if (!serviceChargeStatus && totalExp.compareTo(totalDetail.subtract(totalBuyPrice).multiply(BigDecimal.valueOf(0.5))) > 0) {
        BigDecimal totalService = (BigDecimal.valueOf(bizOrderHeader.getServiceFee())).add(BigDecimal.valueOf(bizOrderHeader.getFreight()));
        if (!serviceChargeStatus && totalExp.compareTo(totalService.multiply(BigDecimal.valueOf(0.5))) > 0) {
            resultMap.put("resultValue","serviceCharge");
            return JsonUtil.generateData(resultMap, null);
        }

        boolean orderLossStatus = RoleUtils.hasRole(user, orderLossAudit);
        if (!orderLossStatus && resultPrice.compareTo(totalBuyPrice) < 0) {
            resultMap.put("resultValue","orderLoss");
            return JsonUtil.generateData(resultMap, null);
        }

        boolean orderLowestStatus = RoleUtils.hasRole(user, orderLowestAudit);
        if (!orderLowestStatus && resultPrice.compareTo(totalBuyPrice.multiply(BigDecimal.valueOf(0.95))) < 0) {
            resultMap.put("resultValue","orderLowest");
            return JsonUtil.generateData(resultMap, null);
        }

        resultMap.put("resultValue",flag);
        return JsonUtil.generateData(resultMap, null);
    }

    @ResponseBody
    @RequiresPermissions("biz:order:bizOrderHeader:edit")
    @RequestMapping(value = "saveInfo")
    public boolean saveInfo(BizOrderHeader bizOrderHeader, String checkStatus, Integer id) {
        bizOrderHeader = bizOrderHeaderService.get(id);
        bizOrderHeader.setBizStatus(Integer.parseInt(checkStatus));
        boolean boo = false;
        try {
            bizOrderHeaderService.save(bizOrderHeader);
            boo = true;
        } catch (Exception e) {
            boo = false;
            logger.error(e.getMessage());
        }
        return boo;
    }

    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "refund")
    public String refund(BizOrderHeader bizOrderHeader, Model model, String orderNoEditable, String orderDetails) {
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        Map<Integer, String> orderNumMap = new HashMap<Integer, String>();
        Map<Integer, Integer> detailIdMap = new HashMap<Integer, Integer>();
        if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null) {
                bizOrderHeader.setCustomer(office);
                model.addAttribute("entity2", bizOrderHeader);
            }
//			用于销售订单页面展示属于哪个采购中心哪个客户专员
            if (bizOrderHeader.getCustomer() != null && bizOrderHeader.getCustomer().getId() != null) {
                BizCustomCenterConsultant bizCustomCenterConsultant = bizCustomCenterConsultantService.get(bizOrderHeader.getCustomer().getId());
                if (bizCustomCenterConsultant != null && bizCustomCenterConsultant.getConsultants() != null &&
                        bizCustomCenterConsultant.getConsultants().getName() != null) {
                    bizCustomCenterConsultant.setConsultants(systemService.getUser(bizCustomCenterConsultant.getConsultants().getId()));
                    model.addAttribute("orderCenter", bizCustomCenterConsultant);
                } else {
                    model.addAttribute("orderCenter", new BizCustomCenterConsultant());
                }
            }
        }
        BizOrderHeader bizOrderHeaderTwo = bizOrderHeaderService.get(bizOrderHeader.getId());
        if (bizOrderHeader.getId() != null) {
            Double totalDetail = bizOrderHeaderTwo.getTotalDetail();//订单详情总价
            Double totalExp = bizOrderHeaderTwo.getTotalExp();//订单总费用
            Double freight = bizOrderHeaderTwo.getFreight();//运费
            Double orderHeaderTotal = totalDetail + totalExp + freight;
            bizOrderHeader.setTobePaid(orderHeaderTotal - bizOrderHeaderTwo.getReceiveTotal());//页面显示待支付总价
            if (orderNoEditable != null && orderNoEditable.equals("editable")) {//不可编辑标识符
                bizOrderHeaderTwo.setOrderNoEditable("editable");//待支付页面不能修改
            }
            if (orderDetails != null && orderDetails.equals("details")) {
                bizOrderHeaderTwo.setOrderDetails("details");//查看详情页面不能修改
            }
            BizOrderAddress bizOrderAddress = new BizOrderAddress();
            bizOrderAddress.setId(bizOrderHeaderTwo.getBizLocation().getId());
            List<BizOrderAddress> list = bizOrderAddressService.findList(bizOrderAddress);
            for (BizOrderAddress orderAddress : list) {
//				    收货地址
                if (orderAddress.getType() == 1) {
                    model.addAttribute("orderAddress", orderAddress);
                }
            }
            BizOrderAddress orderAddress = new BizOrderAddress();
            orderAddress.setOrderHeaderID(bizOrderHeaderTwo);
            List<BizOrderAddress> addresslist = bizOrderAddressService.findList(orderAddress);
            if (CollectionUtils.isNotEmpty(addresslist)) {
                for (BizOrderAddress address : addresslist) {
                    //				交货地址
                    if (address.getType() == 2) {
                        model.addAttribute("address", address);
                    }
                }
            }

            //经销店
            Office office = officeService.get(bizOrderHeader.getCustomer().getId());
            if (office != null && office.getPrimaryPerson() != null && office.getPrimaryPerson().getId() != null) {
                User user = systemService.getUser(office.getPrimaryPerson().getId());
                model.addAttribute("custUser", user);
            }
            //供应商
            List<User> vendUser = bizOrderHeaderService.findVendUserV2(bizOrderHeader.getId());
            if (CollectionUtils.isNotEmpty(vendUser)) {
                model.addAttribute("vendUser", vendUser.get(0));
            }

            //代采
            if (bizOrderHeaderTwo != null) {
                if (bizOrderHeaderTwo.getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    BizOrderAppointedTime bizOrderAppointedTime = new BizOrderAppointedTime();
                    bizOrderAppointedTime.setOrderHeader(bizOrderHeader);
                    List<BizOrderAppointedTime> appointedTimeList = bizOrderAppointedTimeService.findList(bizOrderAppointedTime);
                    if (appointedTimeList != null && !appointedTimeList.isEmpty()) {
                        model.addAttribute("appointedTimeList", appointedTimeList);
                    }
                }
            }

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
            for (BizOrderDetail orderDetail : orderDetailList) {
                BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                if (bizSkuInfo != null) {
                    BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfo);
                    if (skuInfo != null) {
                        orderDetail.setSkuInfo(skuInfo);
                    }
                }
                ordDetailList.add(orderDetail);
                int keyId = orderDetail.getLineNo();
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getOrderNum() != null) {
                    String orderNum = orderDetail.getPoHeader().getOrderNum();
                    orderNumMap.put(keyId, orderNum);
                }
                if (orderDetail.getPoHeader() != null && orderDetail.getPoHeader().getId() != null) {
                    int detailId = orderDetail.getPoHeader().getId();
                    detailIdMap.put(keyId, detailId);
                }
            }
        }
        boolean flag = false;
        User user = UserUtils.getUser();
        if (user.getRoleList() != null) {
            for (Role role : user.getRoleList()) {
                if (RoleEnNameEnum.FINANCE.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        if (bizOrderHeader.getId() != null) {
            BizOrderHeaderUnline bizOrderHeaderUnline = new BizOrderHeaderUnline();
            bizOrderHeaderUnline.setOrderHeader(bizOrderHeader);
            List<BizOrderHeaderUnline> unlineList = bizOrderHeaderUnlineService.findList(bizOrderHeaderUnline);
            if (CollectionUtils.isNotEmpty(unlineList)) {
                model.addAttribute("unlineList", unlineList);
            }
        }

        BizOrderStatus bizOrderStatus = new BizOrderStatus();
        bizOrderStatus.setOrderHeader(bizOrderHeader);
        bizOrderStatus.setOrderType(BizOrderStatus.OrderType.ORDER.getType());


        model.addAttribute("statu", bizOrderHeader.getStatu() == null ? "" : bizOrderHeader.getStatu());
        model.addAttribute("entity", bizOrderHeader);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("orderNumMap", orderNumMap);
        model.addAttribute("detailIdMap", detailIdMap);

        //图片处理
        CommonImg commonImg = new CommonImg();
        commonImg.setImgType(ImgEnum.UNlINE_REFUND_VOUCHER.getCode());
        commonImg.setObjectId(bizOrderHeader.getId());
        commonImg.setObjectName("biz_order_header");
        if (bizOrderHeader.getId() != null) {
            List<CommonImg> imgList = commonImgService.findList(commonImg);
            String photos = "";
            Map<String, Integer> photosMap = new LinkedHashMap<>();

            for (CommonImg img : imgList) {
                photos += img.getImgServer().concat(img.getImgPath()).concat("|");
                photosMap.put(img.getImgServer() + img.getImgPath(), img.getImgSort());
            }
            if (StringUtils.isNotBlank(photos)) {
                bizOrderHeader.setPhotos(photos);
            }
            if (imgList != null && !imgList.isEmpty()) {
                model.addAttribute("photosMap", photosMap);
            }
        }


        return "modules/biz/order/bizOrderHeaderRefund";
    }

    @RequestMapping(value = "saveColorImg")
    public void saveFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> resultMap = Maps.newHashMap();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 获取上传文件名
        MultipartFile colorFile = multipartRequest.getFile("colorImg");
        if (colorFile != null) {
            String originalFilename = colorFile.getOriginalFilename();
            String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
            String msg = "";
            boolean ret = false;
            try {
                String result = AliOssClientUtil.uploadObject2OSS(colorFile.getInputStream(), fullName, colorFile.getSize(), AliOssClientUtil.getOssUploadPath());
                if (StringUtils.isNotBlank(result)) {
                    fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                    ret = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            resultMap.put("fullName", fullName);
            resultMap.put("ret", ret);
            resultMap.put("msg", msg);
        }

        List<MultipartFile> files = multipartRequest.getFiles("productImg");
        if (CollectionUtils.isNotEmpty(files)) {
            List<String> imgList = Lists.newArrayList();
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String fullName = UUID.randomUUID().toString().replaceAll("-", "").concat(originalFilename.substring(originalFilename.indexOf(".")));
                try {
                    String result = AliOssClientUtil.uploadObject2OSS(file.getInputStream(), fullName, file.getSize(), AliOssClientUtil.getOssUploadPath());
                    if (StringUtils.isNotBlank(result)) {
                        fullName = DsConfig.getImgServer().concat("/").concat(AliOssClientUtil.getOssUploadPath()).concat(fullName);
                        imgList.add(fullName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resultMap.put("imgList", imgList);
            resultMap.put("ret", CollectionUtils.isNotEmpty(imgList));
            resultMap.put("msg", "");
        }

        response.getWriter().write(JSONObject.fromObject(resultMap).toString());
    }

    /**
     * 审核列表
     *
     * @param request
     * @param type
     * @param id
     * @return
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "auditList")
    public String auditList(HttpServletRequest request, int type, int id) {
        // type = 0 产地直发
        // type = 1 本地备货
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(id));
        commonProcessEntity.setObjectName(type == 0 ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);

        request.setAttribute("id", id);
        request.setAttribute("list", list);
        request.setAttribute("type", type);
        request.setAttribute("processMap", type == 0 ?
                ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap()
                : ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get().getProcessMap());

        return "modules/biz/order/bizOrderHeaderAuditList";
    }

    /**
     * 审核
     *
     * @param request
     * @param auditType
     * @param id
     * @param currentType
     * @param description
     * @return
     */
    @RequiresPermissions("biz:order:bizOrderHeader:view")
    @RequestMapping(value = "auditSo")
    @ResponseBody
    public String auditSo(HttpServletRequest request, int auditType, int id, String currentType, String description, int orderType, String createPo, String lastPayDateVal) {
        try {
            Pair<Boolean, String> audit = doAudit(id, auditType, currentType, description, orderType, createPo, lastPayDateVal);
            if (audit.getLeft()) {
                return JsonUtil.generateData(audit.getRight(), null);
            }
            return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, audit.getRight(), null);
        }catch (Exception e) {
            LOGGER.error("audit so error ", e);
        }
        return JsonUtil.generateErrorData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "操作失败,发生异常,请联系技术部", null);
    }


    /**
     * 审核
     *
     * @param id
     * @param auditType
     * @param currentType
     * @param description
     * @param orderType
     * @return
     */
    private Pair<Boolean, String> doAudit(int id, int auditType, String currentType, String description, int orderType, String createPo, String lastPayDateVal) {
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(id));
        commonProcessEntity.setObjectName(orderType == 0 ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (list.size() != 1) {
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常! current process 不为 1");
        }

        CommonProcessEntity cureentProcessEntity = list.get(0);
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", id, currentType);
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常!");
        }

        JointOperationOrderProcessLocalConfig localConfig = ConfigGeneral.JOINT_OPERATION_LOCAL_CONFIG.get();
        JointOperationOrderProcessOriginConfig originConfig = ConfigGeneral.JOINT_OPERATION_ORIGIN_CONFIG.get();
        Process currentProcess = null;
        Process nextProcess = null;
        Integer passProcessCode = null;

        BizOrderHeader orderHeader = bizOrderHeaderService.get(id);

        Map<Integer, Process> processMap = null;

        if (orderType == 0) {
            processMap = originConfig.getProcessMap();
        } else {
            processMap = localConfig.getProcessMap();
        }

        currentProcess = processMap.get(Integer.valueOf(currentType));
        switch (OrderPayProportionStatusEnum.parse(orderHeader)) {
            case ZERO:
                passProcessCode = currentProcess.getZeroPassCode();
                break;
            case FIFTH:
                passProcessCode = currentProcess.getFifthPassCode();
                break;
            case ALL:
                passProcessCode = currentProcess.getAllPassCode();
                break;
            default:
                break;
        }

        if (passProcessCode == null || passProcessCode == 0) {
            return Pair.of(Boolean.FALSE, "操作失败,没有下级流程!");
        }

        nextProcess = processMap.get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? passProcessCode : currentProcess.getRejectCode());

        // 当前流程
        // 下一流程
        if (nextProcess == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前流程已经结束!");
        }

        User user = UserUtils.getUser();
        List<String> roleEnNameEnumList = currentProcess.getRoleEnNameEnum();
        boolean hasRole = false;
        for (String s : roleEnNameEnumList) {
            RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
            Role role = new Role();
            role.setEnname(roleEnNameEnum.getState());
            if (user.getRoleList().contains(role)) {
                hasRole = true;
                break;
            }
        }

        if (!user.isAdmin() && !hasRole) {
            return Pair.of(Boolean.FALSE, "操作失败,该用户没有权限!");
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return Pair.of(Boolean.FALSE, "请输入驳回理由!");
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        cureentProcessEntity.setCurrent(0);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(String.valueOf(id));
        nextProcessEntity.setObjectName(orderType == 0 ? JointOperationOrderProcessOriginConfig.ORDER_TABLE_NAME : JointOperationOrderProcessLocalConfig.ORDER_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
        nextProcessEntity.setCurrent(1);
        commonProcessService.save(nextProcessEntity);

        Boolean poFlag = false;
        String poId = "";
        //if (originConfig.getGenPoProcessId().contains(Integer.valueOf(nextProcessEntity.getType()))) {
        //品类主管审核是生成采购单
        if ("yes".equals(createPo)) {
            Pair<Boolean, String> booleanStringPair = bizPoHeaderService.autoGenPO(id, lastPayDateVal);
            if (Boolean.TRUE.equals(booleanStringPair.getLeft())) {
                poFlag = true;
                poId = booleanStringPair.getRight();
            }
            LOGGER.warn("auto gen po[{}][{}]", booleanStringPair.getLeft(), booleanStringPair.getRight());
        }

        Pair<Boolean, String> audit = null;
        if (poFlag) {
            audit = Pair.of(Boolean.TRUE, "采购单生成," + poId);
        } else {
            audit = Pair.of(Boolean.TRUE, "操作成功");
        }
        return audit;
    }

    /**
     * 代采订单审核
     * @param orderHeaderId
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    public Pair<Boolean, String> auditFifty(HttpServletRequest request, HttpServletResponse response, Integer orderHeaderId, String currentType, int auditType, String description, String createPo, String lastPayDateVal) throws ParseException {
        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(String.valueOf(orderHeaderId));
        commonProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setCurrent(1);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (list.size() != 1) {
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常! current process 不为 1");
        }
        CommonProcessEntity cureentProcessEntity = list.get(0);
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            return Pair.of(Boolean.FALSE, "操作失败,当前审核状态异常!");
        }
        if (cureentProcessEntity == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前订单无审核状态!");
        }

        BizOrderHeader bizOrderHeader = this.get(orderHeaderId);
        DoOrderHeaderProcessFifthConfig doOrderHeaderProcessConfig = ConfigGeneral.DO_ORDER_HEADER_PROCESS_FIFTH_CONFIG.get();

        Integer passProcessCode = null;
        // 当前流程
        DoOrderHeaderProcessFifthConfig.OrderHeaderProcess currentProcess = doOrderHeaderProcessConfig.processMap.get(Integer.valueOf(currentType));
        switch (OrderPayProportionStatusEnum.parse(bizOrderHeader.getTotalDetail(), bizOrderHeader.getReceiveTotal())) {
            case FIFTH:
                passProcessCode = currentProcess.getFifthPassCode();
                break;
            case ALL:
                passProcessCode = currentProcess.getAllPassCode();
                break;
            default:
                break;
        }
        if (passProcessCode == null || passProcessCode == 0) {
            return Pair.of(Boolean.FALSE, "操作失败,没有下级流程!");
        }

        // 下一流程
        DoOrderHeaderProcessFifthConfig.OrderHeaderProcess nextProcess = doOrderHeaderProcessConfig.processMap.get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? passProcessCode : currentProcess.getRejectCode());
        if (nextProcess == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前流程已经结束!");
        }
        User user = UserUtils.getUser();
        RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(currentProcess.getRoleEnNameEnum());
        Role role = new Role();
        role.setEnname(roleEnNameEnum.getState());
        if (!user.isAdmin() && !user.getRoleList().contains(role)) {
            return Pair.of(Boolean.FALSE, "操作失败,该用户没有权限!");
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return Pair.of(Boolean.FALSE, "请输入驳回理由!");
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        cureentProcessEntity.setCurrent(0);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(bizOrderHeader.getId().toString());
        nextProcessEntity.setObjectName(BizOrderHeaderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setCurrent(1);
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());

        commonProcessService.save(nextProcessEntity);

        StringBuilder phone = new StringBuilder();
        String roleEnNameEnumStr = nextProcess.getRoleEnNameEnum();
        if (StringUtils.isNotBlank(roleEnNameEnumStr)) {
            if ("MARKETINGMANAGER".equals(roleEnNameEnumStr)) {
                roleEnNameEnumStr = "MARKETING_MANAGER".toLowerCase();
            } else {
                roleEnNameEnumStr = roleEnNameEnumStr.toLowerCase();
            }

            User sendUser=new User(systemService.getRoleByEnname(roleEnNameEnumStr));
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
                        ImmutableMap.of("order","代采清单", "orderNum", bizOrderHeader.getOrderNum()));
            }
        }

        //自动生成采购单
        Boolean poFlag = false;
        String poId = "";
        if ("yes".equals(createPo)) {
            //订单标识类型
            String type = "2";
            Pair<Boolean, String> booleanStringPair = bizPoHeaderService.goListForAutoSave(orderHeaderId, type, lastPayDateVal, request, response);
            if (Boolean.TRUE.equals(booleanStringPair.getLeft())) {
                return booleanStringPair;
            }
        }
        return Pair.of(Boolean.TRUE, "操作成功!");
    }

}
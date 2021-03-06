/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.web.po;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wanhutong.backend.common.thread.ThreadPoolManager;
import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.JsonUtil;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * 采购付款单Controller
 *
 * @author Ma.Qiang
 * @version 2018-05-04
 */
@Controller
@RequestMapping(value = "${adminPath}/biz/po/bizPoPaymentOrder")
public class BizPoPaymentOrderController extends BaseController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BizPoPaymentOrderController.class);

    @Autowired
    private BizPoPaymentOrderService bizPoPaymentOrderService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private SystemService systemService;

    @ModelAttribute
    public BizPoPaymentOrder get(@RequestParam(required = false) Integer id) {
        BizPoPaymentOrder entity = null;
        if (id != null) {
            entity = bizPoPaymentOrderService.get(id);
        }
        if (entity == null) {
            entity = new BizPoPaymentOrder();
        }
        return entity;
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = {"list", ""})
    public String list(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model, Integer poId) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("roleSet", roleSet);

        String fromPage = request.getParameter("fromPage");
        model.addAttribute("fromPage", fromPage);
        if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(poId);
            model.addAttribute("bizRequestHeader", bizRequestHeader);
        } else if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.ORDER_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            bizOrderHeaderService.get(poId);
        } else {
            bizPoPaymentOrder.setOrderType(PoPayMentOrderTypeEnum.PO_TYPE.getType());
            BizPoHeader bizPoHeader = bizPoHeaderService.get(poId);
            if (fromPage != null) {
                switch (fromPage) {
                    case "requestHeader":
                        bizPoHeader.setFromPage("requestHeader");
                        List<String> reqNoList = bizPoHeaderService.getOrderNumOrReqNoByPoId(bizPoHeader);
                        if (CollectionUtils.isNotEmpty(reqNoList)) {
                            model.addAttribute("headerNum", reqNoList.get(0));
                        }
                        BizRequestHeader bizRequestHeader = new BizRequestHeader();
                        bizRequestHeader.setBizPoHeader(bizPoHeader);
                        List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
                        if (CollectionUtils.isNotEmpty(requestHeaderList)) {
                            model.addAttribute("requestHeader", requestHeaderList.get(0));
                            model.addAttribute("headerNum", requestHeaderList.get(0).getReqNo());
                        }
                        break;
                    case "orderHeader":
                        bizPoHeader.setFromPage("orderHeader");
                        List<String> orderNumList = bizPoHeaderService.getOrderNumOrReqNoByPoId(bizPoHeader);
                        if (CollectionUtils.isNotEmpty(orderNumList)) {
                            model.addAttribute("headerNum", orderNumList.get(0));
                        }
                        BizOrderHeader bizOrderHeader = new BizOrderHeader();
                        bizOrderHeader.setBizPoHeader(bizPoHeader);
                        List<BizOrderHeader> orderHeaderList = bizOrderHeaderService.findList(bizOrderHeader);
                        if (CollectionUtils.isNotEmpty(orderHeaderList)) {
                            model.addAttribute("orderHeader", orderHeaderList.get(0));
                            model.addAttribute("headerNum", orderHeaderList.get(0).getOrderNum());
                        }
                        break;
                    default:
                        break;
                }
            }
            model.addAttribute("bizPoHeader", bizPoHeader);
        }
        if (poId == null) {
            bizPoPaymentOrder.setPoHeaderId(-1);
        } else {
            bizPoPaymentOrder.setPoHeaderId(poId);
        }
        Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);

        //更新BizPoPaymentOrder审核按钮控制flag
        bizPoPaymentOrderService.updateHasRole(page);

        model.addAttribute("page", page);
        String orderId = request.getParameter("orderId");
        model.addAttribute("orderId", orderId);
        return "modules/biz/po/bizPoPaymentOrderList";
    }
    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = "listData4MobileNew")
    @ResponseBody
    public String listData4MobileNew(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model, Integer poId) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("roleSet", roleSet);
        resultMap.put("roleSet", roleSet);

        String fromPage = request.getParameter("fromPage");
        model.addAttribute("fromPage", fromPage);
        resultMap.put("fromPage", fromPage);
        if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(poId);
            model.addAttribute("bizRequestHeader", bizRequestHeader);
            resultMap.put("bizRequestHeader", bizRequestHeader);
        } else if (bizPoPaymentOrder.getOrderType() != null && PoPayMentOrderTypeEnum.ORDER_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            bizOrderHeaderService.get(poId);
        } else {
            BizPoHeader bizPoHeader = bizPoHeaderService.get(poId);
            if (fromPage != null) {
                switch (fromPage) {
                    case "requestHeader":
                        bizPoHeader.setFromPage("requestHeader");
                        List<String> reqNoList = bizPoHeaderService.getOrderNumOrReqNoByPoId(bizPoHeader);
                        if (CollectionUtils.isNotEmpty(reqNoList)) {
                            model.addAttribute("headerNum", reqNoList.get(0));
                            resultMap.put("headerNum", reqNoList.get(0));
                        }
                        BizRequestHeader bizRequestHeader = new BizRequestHeader();
                        bizRequestHeader.setBizPoHeader(bizPoHeader);
                        List<BizRequestHeader> requestHeaderList = bizRequestHeaderForVendorService.findList(bizRequestHeader);
                        if (CollectionUtils.isNotEmpty(requestHeaderList)) {
                            model.addAttribute("requestHeader", requestHeaderList.get(0));
                            resultMap.put("requestHeader", requestHeaderList.get(0));
                        }
                        break;
                    case "orderHeader":
                        bizPoHeader.setFromPage("orderHeader");
                        List<String> orderNumList = bizPoHeaderService.getOrderNumOrReqNoByPoId(bizPoHeader);
                        if (CollectionUtils.isNotEmpty(orderNumList)) {
                            model.addAttribute("headerNum", orderNumList.get(0));
                            resultMap.put("headerNum", orderNumList.get(0));
                        }
                        BizOrderHeader bizOrderHeader = new BizOrderHeader();
                        bizOrderHeader.setBizPoHeader(bizPoHeader);
                        List<BizOrderHeader> orderHeaderList = bizOrderHeaderService.findList(bizOrderHeader);
                        if (CollectionUtils.isNotEmpty(orderHeaderList)) {
                            model.addAttribute("orderHeader", orderHeaderList.get(0));
                            resultMap.put("orderHeader", orderHeaderList.get(0));
                        }
                        break;
                    default:
                        break;
                }
            }
            model.addAttribute("bizPoHeader", bizPoHeader);
            resultMap.put("bizPoHeader", bizPoHeader);
        }
        if (poId == null) {
            bizPoPaymentOrder.setPoHeaderId(-1);
        } else {
            bizPoPaymentOrder.setPoHeaderId(poId);
        }
        Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
        model.addAttribute("page", page);
        resultMap.put("page", page);
        String orderId = request.getParameter("orderId");
        model.addAttribute("orderId", orderId);
        resultMap.put("orderId", orderId);

        resultMap.put("PO_TYPE", PoPayMentOrderTypeEnum.PO_TYPE.getType());
        resultMap.put("CLOSE", ReqHeaderStatusEnum.CLOSE.getState());

        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = {"listV2"})
    public String listV2(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("roleSet", roleSet);

        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        Map<String, String> configMap = Maps.newLinkedHashMap();

        configMap.put("待总经理", "待总经理");
        configMap.put("财务总监", "财务总监");
        configMap.put("财务总经理", "财务总经理");
        configMap.put("完成", "完成");
        configMap.put("驳回", "驳回");


        for(PaymentOrderProcessConfig.Process process : paymentOrderProcessConfig.getProcessList()) {
            if (StringUtils.isNotBlank(bizPoPaymentOrder.getSelectAuditStatus()) && process.getName().contains(bizPoPaymentOrder.getSelectAuditStatus())) {
                bizPoPaymentOrder.setAuditStatusCode(process.getCode());
            }
        }

        if (StringUtils.isNotBlank(bizPoPaymentOrder.getOrderNum())) {
            if (bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("SO") || bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("DO")) {
                BizOrderHeader orderHeader = bizOrderHeaderService.getByOrderNum(bizPoPaymentOrder.getOrderNum());
                if (orderHeader != null && orderHeader.getBizPoHeader() != null) {
                    bizPoPaymentOrder.setPoHeaderId(orderHeader.getBizPoHeader().getId());
                }
            }
            if (bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("RE")) {
                BizRequestHeader requestHeader = new BizRequestHeader();
                requestHeader.setReqNo(bizPoPaymentOrder.getOrderNum());
                List<BizRequestHeader> list = bizRequestHeaderForVendorService.findList(requestHeader);
                if (CollectionUtils.isNotEmpty(list) && list.get(0).getBizPoHeader() != null) {
                    bizPoPaymentOrder.setPoHeaderId(list.get(0).getBizPoHeader().getId());
                }
            }
        }

        Page<BizPoPaymentOrder> page = new Page<BizPoPaymentOrder>(request, response);
        page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        if (StringUtils.isBlank(bizPoPaymentOrder.getOrderNum())) {
//            page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        } else if (StringUtils.isNotBlank(bizPoPaymentOrder.getOrderNum()) && bizPoPaymentOrder.getPoHeaderId() != null) {
//            page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        }

        //更新BizPoPaymentOrder审核按钮控制flag
        bizPoPaymentOrderService.updateHasRole(page);

        model.addAttribute("page", page);
        String orderId = request.getParameter("orderId");
        model.addAttribute("orderId", orderId);
        model.addAttribute("configMap", configMap);
        return "modules/biz/po/bizPoPaymentOrderListV2";
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = {"listV2ForMobile"})
    @ResponseBody
    public String listV2ForMobile(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("roleSet", roleSet);
        resultMap.put("roleSet", roleSet);

        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        Map<String, String> configMap = Maps.newLinkedHashMap();

        configMap.put("待总经理", "待总经理");
        configMap.put("财务总监", "财务总监");
        configMap.put("财务总经理", "财务总经理");
        configMap.put("完成", "完成");
        configMap.put("驳回", "驳回");


        for(PaymentOrderProcessConfig.Process process : paymentOrderProcessConfig.getProcessList()) {
            if (StringUtils.isNotBlank(bizPoPaymentOrder.getSelectAuditStatus()) && process.getName().contains(bizPoPaymentOrder.getSelectAuditStatus())) {
                bizPoPaymentOrder.setAuditStatusCode(process.getCode());
            }
        }

        if (StringUtils.isNotBlank(bizPoPaymentOrder.getOrderNum())) {
            if (bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("SO") || bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("DO")) {
                BizOrderHeader orderHeader = bizOrderHeaderService.getByOrderNum(bizPoPaymentOrder.getOrderNum());
                if (orderHeader != null && orderHeader.getBizPoHeader() != null) {
                    bizPoPaymentOrder.setPoHeaderId(orderHeader.getBizPoHeader().getId());
                }
            }
            if (bizPoPaymentOrder.getOrderNum().toUpperCase().startsWith("RE")) {
                BizRequestHeader requestHeader = new BizRequestHeader();
                requestHeader.setReqNo(bizPoPaymentOrder.getOrderNum());
                List<BizRequestHeader> list = bizRequestHeaderForVendorService.findList(requestHeader);
                if (CollectionUtils.isNotEmpty(list) && list.get(0).getBizPoHeader() != null) {
                    bizPoPaymentOrder.setPoHeaderId(list.get(0).getBizPoHeader().getId());
                }
            }
        }

        Page<BizPoPaymentOrder> page = new Page<BizPoPaymentOrder>(request, response);
        page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        if (StringUtils.isBlank(bizPoPaymentOrder.getOrderNum())) {
//            page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        } else if (StringUtils.isNotBlank(bizPoPaymentOrder.getOrderNum()) && bizPoPaymentOrder.getPoHeaderId() != null) {
//            page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
//        }

        //更新BizPoPaymentOrder审核按钮控制flag
        bizPoPaymentOrderService.updateHasRole(page);

        model.addAttribute("page", page);
        String orderId = request.getParameter("orderId");
        model.addAttribute("orderId", orderId);
        model.addAttribute("configMap", configMap);

        resultMap.put("page", page);
        resultMap.put("orderId", orderId);
        resultMap.put("configMap", configMap);

        return JsonUtil.generateData(resultMap, null);
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = {"listData4Mobile"})
    @ResponseBody
    public String listData4Mobile(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, int poId) {
        bizPoPaymentOrder.setPoHeaderId(poId);
        BizPoHeader bizPoHeader = bizPoHeaderService.get(poId);
        Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("bizPoHeader", bizPoHeader);
        resultMap.put("page", page);
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = {"listV2Data4Mobile"})
    @ResponseBody
    public String listV2Data4Mobile(BizPoPaymentOrder bizPoPaymentOrder, HttpServletRequest request, HttpServletResponse response, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        Set<String> roleSet = Sets.newHashSet();
        for (Role r : roleList) {
            RoleEnNameEnum parse = RoleEnNameEnum.parse(r.getEnname());
            if (parse != null) {
                roleSet.add(parse.name());
            }
        }
        model.addAttribute("roleSet", roleSet);
        resultMap.put("roleSet", roleSet);

        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        Map<String, String> configMap = Maps.newLinkedHashMap();

        configMap.put("供货部", "供货部");
        configMap.put("财务经理", "财务经理");
        configMap.put("财务总监", "财务总监");
        configMap.put("完成", "完成");
        configMap.put("驳回", "驳回");


        for(PaymentOrderProcessConfig.Process process : paymentOrderProcessConfig.getProcessList()) {
            if (StringUtils.isNotBlank(bizPoPaymentOrder.getSelectAuditStatus()) && process.getName().contains(bizPoPaymentOrder.getSelectAuditStatus())) {
                bizPoPaymentOrder.setAuditStatusCode(process.getCode());
            }
        }


        if (StringUtils.isNotBlank(bizPoPaymentOrder.getOrderNum())) {
            if (bizPoPaymentOrder.getOrderNum().startsWith("SO") || bizPoPaymentOrder.getOrderNum().startsWith("DO")) {
                BizOrderHeader orderHeader = bizOrderHeaderService.getByOrderNum(bizPoPaymentOrder.getOrderNum());
                if (orderHeader != null) {
                    bizPoPaymentOrder.setPoHeaderId(orderHeader.getBizPoHeader().getId());
                }
            }
        }

        Page<BizPoPaymentOrder> page = bizPoPaymentOrderService.findPage(new Page<BizPoPaymentOrder>(request, response), bizPoPaymentOrder);
        model.addAttribute("page", page);
        String orderId = request.getParameter("orderId");
        model.addAttribute("orderId", orderId);
        model.addAttribute("configMap", configMap);
        resultMap.put("page", page);
        resultMap.put("orderId", orderId);
        resultMap.put("configMap", configMap);
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = "form4Mobile")
    @ResponseBody
    public String form4Mobile(HttpServletRequest request, HttpServletResponse response, BizPoPaymentOrder bizPoPaymentOrder, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        String fromPage = request.getParameter("fromPage");
        model.addAttribute("fromPage", fromPage);
        resultMap.put("fromPage", fromPage);
        bizPoPaymentOrder = bizPoPaymentOrderService.get(bizPoPaymentOrder.getId());
        if (bizPoPaymentOrder.getPoHeaderId() != null) {
            BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoPaymentOrder.getPoHeaderId());
            if (bizPoHeader != null) {
                BigDecimal totalDetail = bizPoHeader.getTotalDetail() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalDetail());
                BigDecimal totalExp = bizPoHeader.getTotalExp() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalExp());
                BigDecimal totalDetailResult = totalDetail.add(totalExp);
                DecimalFormat df = new DecimalFormat("#0.00");
                model.addAttribute("totalDetailResult", df.format(totalDetailResult));
                resultMap.put("totalDetailResult", df.format(totalDetailResult));
            }
        }
        model.addAttribute("bizPoPaymentOrder", bizPoPaymentOrder);
        resultMap.put("bizPoPaymentOrder", bizPoPaymentOrder);

        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = "form")
    public String form(HttpServletRequest request, HttpServletResponse response, BizPoPaymentOrder bizPoPaymentOrder, Model model) {
        String fromPage = request.getParameter("fromPage");
        model.addAttribute("fromPage", fromPage);
        bizPoPaymentOrder = bizPoPaymentOrderService.get(bizPoPaymentOrder.getId());
        if (bizPoPaymentOrder.getPoHeaderId() != null) {
            BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoPaymentOrder.getPoHeaderId());
            if (bizPoHeader != null) {
                BigDecimal totalDetail = bizPoHeader.getTotalDetail() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalDetail());
                BigDecimal totalExp = bizPoHeader.getTotalExp() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalExp());
                BigDecimal totalDetailResult = totalDetail.add(totalExp);
                DecimalFormat df = new DecimalFormat("#0.00");
                model.addAttribute("totalDetailResult", df.format(totalDetailResult));
            }
        }

        bizPoPaymentOrder.setFromPage(fromPage);
        model.addAttribute("bizPoPaymentOrder", bizPoPaymentOrder);
        return "modules/biz/po/bizPoPaymentOrderForm";
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = "formV2")
    public String formV2(BizPoPaymentOrder bizPoPaymentOrder, Model model) {
        bizPoPaymentOrder = bizPoPaymentOrderService.get(bizPoPaymentOrder.getId());
        if (bizPoPaymentOrder.getPoHeaderId() != null) {
            BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoPaymentOrder.getPoHeaderId());
            if (bizPoHeader != null) {
                BigDecimal totalDetail = bizPoHeader.getTotalDetail() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalDetail());
                BigDecimal totalExp = bizPoHeader.getTotalExp() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalExp());
                BigDecimal totalDetailResult = totalDetail.add(totalExp);
                DecimalFormat df = new DecimalFormat("#0.00");
                model.addAttribute("totalDetailResult", df.format(totalDetailResult));
            }
        }

        if (bizPoPaymentOrder.getId() != null) {
            Integer processId = bizPoPaymentOrder.getProcessId();
            CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
            commonProcessEntity.setObjectId(String.valueOf(bizPoPaymentOrder.getId()));
            commonProcessEntity.setObjectName("biz_po_payment_order");
            BizPoPaymentOrder bizPoPaymentOrder2 = bizPoPaymentOrderService.get(bizPoPaymentOrder.getId());
            Date createDate=bizPoPaymentOrder2.getCreateDate();
            List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
            list = list.stream().filter(t -> t.getCreateTime().compareTo(createDate)>=0).collect(Collectors.toList());
            model.addAttribute("auditList", list);
            model.addAttribute("processId", processId);
        }
        model.addAttribute("bizPoPaymentOrder", bizPoPaymentOrder);
        return "modules/biz/po/bizPoPaymentOrderFormV2";
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:view")
    @RequestMapping(value = "formV2ForMobile")
    @ResponseBody
    public String formV2ForMobile(BizPoPaymentOrder bizPoPaymentOrder, Model model) {
        Map<String, Object> resultMap = Maps.newHashMap();
        bizPoPaymentOrder = bizPoPaymentOrderService.get(bizPoPaymentOrder.getId());
        if (bizPoPaymentOrder.getPoHeaderId() != null) {
            BizPoHeader bizPoHeader = bizPoHeaderService.get(bizPoPaymentOrder.getPoHeaderId());
            if (bizPoHeader != null) {
                BigDecimal totalDetail = bizPoHeader.getTotalDetail() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalDetail());
                BigDecimal totalExp = bizPoHeader.getTotalExp() == null ? BigDecimal.ZERO : new BigDecimal(bizPoHeader.getTotalExp());
                BigDecimal totalDetailResult = totalDetail.add(totalExp);
                DecimalFormat df = new DecimalFormat("#0.00");
                model.addAttribute("totalDetailResult", df.format(totalDetailResult));
                resultMap.put("totalDetailResult", df.format(totalDetailResult));
            }
        }

        if (bizPoPaymentOrder.getId() != null) {
            Integer processId = bizPoPaymentOrder.getProcessId();
            CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
            commonProcessEntity.setObjectId(String.valueOf(bizPoPaymentOrder.getId()));
            commonProcessEntity.setObjectName("biz_po_payment_order");
            List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
            model.addAttribute("auditList", list);
            model.addAttribute("processId", processId);

            resultMap.put("auditList", list);
            resultMap.put("processId", processId);
        }
        model.addAttribute("bizPoPaymentOrder", bizPoPaymentOrder);
        resultMap.put("bizPoPaymentOrder", bizPoPaymentOrder);
        return JsonUtil.generateData(resultMap, null);
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
    @RequestMapping(value = "save")
    public String save(HttpServletRequest request, HttpServletResponse response, BizPoPaymentOrder bizPoPaymentOrder, Model model, RedirectAttributes redirectAttributes) {
        Integer id = bizPoPaymentOrder.getId();
        if (!beanValidator(model, bizPoPaymentOrder)) {
            return form(request, response, bizPoPaymentOrder, model);
        }
        bizPoPaymentOrderService.save(bizPoPaymentOrder);
        addMessage(redirectAttributes, "保存付款单成功");

        if (id != null) {
            List<BizPoPaymentOrder> list = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
            BizPoPaymentOrder poPaymentOrder = list.get(0);
            BizPoHeader bizPoHeader = bizPoHeaderService.get(poPaymentOrder.getPoHeader().getId());


            //自动发送短信通知审核第一个节点
            StringBuilder phone = new StringBuilder();
            PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
            PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
            List<PaymentOrderProcessConfig.MoneyRole> moneyRoleList = purchaseOrderProcess.getMoneyRole();
            if (moneyRoleList != null && moneyRoleList.get(0) != null) {
                String roleEnNameEnumStr = moneyRoleList.get(0).getRoleEnNameEnum().get(0);
                RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(roleEnNameEnumStr);
                User sendUser = new User(systemService.getRoleByEnname(roleEnNameEnum == null ? "" : roleEnNameEnum.getState()));
                List<User> userList = systemService.findUser(sendUser);
                if (CollectionUtils.isNotEmpty(userList)) {
                    for (User u : userList) {
                        phone.append(u.getMobile()).append(",");
                    }
                }

                Byte soType = bizPoHeaderService.getBizPoOrderReqByPo(bizPoHeader);

                String orderStr = "";
                String orderNum = "";
                if (soType == Byte.parseByte("1")) {
                    orderStr = "订单支付";
                    orderNum = poPaymentOrder.getOrderNum();

                } else {
                    orderStr = "备货单支付";
                    orderNum = poPaymentOrder.getReqNo();
                }

                AliyunSmsClient.getInstance().sendSMS(
                        SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                        phone.toString(),
                        ImmutableMap.of("order", orderStr, "orderNum", orderNum));

            }
        }
        if(bizPoPaymentOrder.getRequestId()!=null){
            return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor/list";
        }
        else {
            return "redirect:" + Global.getAdminPath() + "/biz/order/bizOrderHeader/list";
        }
        //return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/?repage&poId=" + bizPoPaymentOrder.getPoHeaderId() + "&orderType=" + bizPoPaymentOrder.getOrderType();
       // return "redirect:" + Global.getAdminPath() + "/biz/request/bizRequestHeaderForVendor/form?id=" + bizPoPaymentOrder.getRequestId() + "&str=detail";

    }

//    @RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
//    @RequestMapping(value = "save")
//    @ResponseBody
//    public String savePayment() {
//        if (!beanValidator(model, bizPoPaymentOrder)) {
//            return form(request, response, bizPoPaymentOrder, model);
//        }
//        bizPoPaymentOrderService.save(bizPoPaymentOrder);
//        addMessage(redirectAttributes, "保存付款单成功");
//        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/?repage&poId=" + bizPoPaymentOrder.getPoHeaderId()
//                + "&orderType=" + bizPoPaymentOrder.getOrderType() + "&fromPage=" + bizPoPaymentOrder.getFromPage();
//    }


    @RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
    @RequestMapping(value = "save4Mobile")
    @ResponseBody
    public String save4Mobile(HttpServletRequest request, HttpServletResponse response, BizPoPaymentOrder bizPoPaymentOrder, Model model, RedirectAttributes redirectAttributes) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (!beanValidator(model, bizPoPaymentOrder)) {
            return form(request, response, bizPoPaymentOrder, model);
        }
        Boolean result = false;
        try {
            bizPoPaymentOrderService.save(bizPoPaymentOrder);
            result = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        resultMap.put("result", result);
        //return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/?repage&poId=" + bizPoPaymentOrder.getPoHeaderId() + "&orderType=" + bizPoPaymentOrder.getOrderType();
        return JsonUtil.generateData(resultMap, request.getParameter("callback"));
    }

    @RequiresPermissions("biz:po:bizPoPaymentOrder:edit")
    @RequestMapping(value = "delete")
    public String delete(BizPoPaymentOrder bizPoPaymentOrder, RedirectAttributes redirectAttributes) {
        bizPoPaymentOrderService.delete(bizPoPaymentOrder);
        addMessage(redirectAttributes, "删除付款单成功");
        return "redirect:" + Global.getAdminPath() + "/biz/po/bizPoPaymentOrder/?repage";
    }

}
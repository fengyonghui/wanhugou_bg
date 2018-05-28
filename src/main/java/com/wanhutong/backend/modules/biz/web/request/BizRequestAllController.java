package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 备货清单和销售订单（source=gh审核通过、采购中，source=kc 采购中、采购完成、供应中心供货, source=sh 备货中, source=ghs 供货详情，ship=xs 销售单，ship=bh 备货单）
 * 采购中心和供货中心（bizStatu=0采购中心，bizStatu=1供货中心）
 */

@Controller
@RequestMapping(value = "${adminPath}/biz/request/bizRequestAll")
public class BizRequestAllController {
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private DefaultPropService defaultPropService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizLogisticsService bizLogisticsService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizOrderAddressService bizOrderAddressService;
    @Autowired
    private DictService dictService;

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source, Integer bizStatu, String ship, HttpServletRequest request, HttpServletResponse response, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
        DefaultProp defaultProp = new DefaultProp();
        defaultProp.setPropKey("vend_center");
        Integer vendId = 0;
        List<DefaultProp> defaultPropList = defaultPropService.findList(defaultProp);
        if (defaultPropList != null && defaultPropList.size() > 0) {
            DefaultProp prop = defaultPropList.get(0);
            vendId = Integer.parseInt(prop.getPropValue());
        }
        //用户属于供应中心或管理员

       /*String type= user.getCompany().getType();
        System.out.println(type);

        boolean flag=false;
        if(user.getRoleList()!=null) {
            for (Role role : user.getRoleList()) {
                if (!RoleEnNameEnum.P_CENTER_MANAGER.getState().equals(role.getEnname())) {
                    flag = true;
                    break;
                }
            }
        }
        if(flag){
            model.addAttribute("ship",ship);
        }*/
//        String type = officeService.get(user.getCompany().getId()).getType();
//        if (type.equals(OfficeTypeEnum.SUPPLYCENTER.getType())  || user.isAdmin()){
        model.addAttribute("ship", ship);
//        }else {
//            model.addAttribute("ship","");
//        }
        model.addAttribute("source", source);
        if (bizOrderHeader == null) {
            bizOrderHeader = new BizOrderHeader();
        }
        if ("kc".equals(source)) {
            bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
            bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCK_COMPLETE.getState().byteValue());
            if (bizStatu == 0) {
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());
            }
            if (bizStatu == 1) {
                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());

            }
            if (ship.equals("xs")) {
                Page<BizOrderHeader> page = new Page<>(request, response);
                page = bizOrderHeaderService.findPageForSendGoods(page, bizOrderHeader);
                for (BizOrderHeader orderHeader : page.getList()) {
                    if(orderHeader.getBizLocation()!=null && orderHeader.getBizLocation().getId()!=null){
                        orderHeader.setBizLocation(bizOrderAddressService.get(orderHeader.getBizLocation().getId()));
                    }
                }
                model.addAttribute("page", page);
            } else {
                Page<BizRequestHeader> page = new Page<>(request, response);
                page = bizRequestHeaderService.findPageForSendGoods(page, bizRequestHeader);
                model.addAttribute("page", page);
            }
        } else if ("sh".equals(source)) {
            bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
            bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.COMPLETE.getState().byteValue());

            Page<BizRequestHeader> page = new Page<>(request, response);
            page = bizRequestHeaderService.findPageForSendGoods(page, bizRequestHeader);
            model.addAttribute("page", page);
            //                bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.STOCKING.getState());
            //                bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.COMPLETE.getState());
        }
        if ("gh".equals(source)) {
            bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.APPROVE.getState().byteValue());
            bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.APPROVE.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            if (ship.equals("xs")) {
                Page<BizOrderHeader> page = new Page<>(request, response);
                page = bizOrderHeaderService.findPageForSendGoods(page, bizOrderHeader);
                model.addAttribute("page", page);
            } else {
                Page<BizRequestHeader> page = new Page<>(request, response);
                page = bizRequestHeaderService.findPageForSendGoods(page, bizRequestHeader);
                model.addAttribute("page", page);
            }
        }
        if ("ghs".equals(source)) {
            bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
            bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
            bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.STOCKING.getState());
            if (ship.equals("xs")) {
                Page<BizOrderHeader> page = new Page<>(request, response);
                page = bizOrderHeaderService.findPageForSendGoods(page, bizOrderHeader);
                model.addAttribute("page", page);
            } else {
                Page<BizRequestHeader> page = new Page<>(request, response);
                page = bizRequestHeaderService.findPageForSendGoods(page, bizRequestHeader);
                model.addAttribute("page", page);
            }
        }


        if (bizStatu != null) {
            model.addAttribute("bizStatu", bizStatu.toString());
        }
        return "modules/biz/request/bizRequestAllList";
    }

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = "form")
    public String form(String source, Integer id, Model model, Integer bizStatu, String ship) {
        List<BizRequestDetail> reqDetailList = Lists.newArrayList();
        List<BizOrderDetail> ordDetailList = Lists.newArrayList();
        BizOrderHeader orderHeader = null;
        BizRequestHeader requestHeader = null;
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        if ("kc".equals(source) && "xs".equals(ship) || "ghs".equals(source)) {
            bizOrderHeader = bizOrderHeaderService.get(id);
        } else {
            bizRequestHeader = bizRequestHeaderService.get(id);
        }
        if (bizRequestHeader != null && bizRequestHeader.getId() != null) {
            //取出用户所属采购中心
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            BizLogistics bizLogistics = new BizLogistics();
            User user = UserUtils.getUser();
//            List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
            if (user.isAdmin()) {
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList", invInfoList);
                List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
                model.addAttribute("logisticsList", logisticsList);
            } else {
                Office company = officeService.get(user.getCompany().getId());
                bizInventoryInfo.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList", invInfoList);
                List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
                model.addAttribute("logisticsList", logisticsList);
            }
            BizRequestDetail bizRequestDetail = new BizRequestDetail();
            bizRequestDetail.setRequestHeader(bizRequestHeader);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findPoRequet(bizRequestDetail);
            requestHeader = bizRequestHeaderService.get(bizRequestHeader.getId());
            for (BizRequestDetail requestDetail : requestDetailList) {
                if(requestDetail.getBizPoHeader()==null){
                    requestHeader.setPoSource("poHeaderSource");
                }
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
                requestDetail.setSkuInfo(skuInfo);
                reqDetailList.add(requestDetail);
            }
            if(requestDetailList.size()==0){
                requestHeader.setPoSource("poHeaderSource");
            }
        }
        if (bizOrderHeader != null && bizOrderHeader.getId() != null) {
            //取出用户所属采购中心
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            BizLogistics bizLogistics = new BizLogistics();
            User user = UserUtils.getUser();
            if (user.isAdmin()) {
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList", invInfoList);
                List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
                model.addAttribute("logisticsList", logisticsList);
            } else {
                Office company = officeService.get(user.getCompany().getId());
                bizInventoryInfo.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList", invInfoList);
                List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
                model.addAttribute("logisticsList", logisticsList);
            }
            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
            orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
            for (BizOrderDetail orderDetail : orderDetailList) {
                if(orderDetail.getPoHeader()==null){
                    orderHeader.setPoSource("poHeaderSource");
                }
                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                orderDetail.setSkuInfo(skuInfo);
                ordDetailList.add(orderDetail);
            }
            if(orderDetailList.size()==0){
                orderHeader.setPoSource("poHeaderSource");
            }

        }
        model.addAttribute("bizRequestHeader", requestHeader);
        model.addAttribute("bizOrderHeader", orderHeader);
        model.addAttribute("reqDetailList", reqDetailList);
        model.addAttribute("ordDetailList", ordDetailList);
        model.addAttribute("source", source);
        model.addAttribute("bizStatu", bizStatu);
        model.addAttribute("ship", ship);
        if (source != null && "kc".equals(source)) {
            return "modules/biz/request/bizRequestHeaderKcForm";
        }
        if (source != null && "ghs".equals(source)) {
            return "modules/biz/request/bizRequestKcXqForm";
        }
        if (source != null && "sh".equals(source)) {
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            User user = UserUtils.getUser();
            if (user.isAdmin()) {
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
                model.addAttribute("invInfoList", invInfoList);
            } else {
                Office company = systemService.getUser(user.getId()).getCompany();
                BizInventoryInfo bizInventoryInfo1 = new BizInventoryInfo();
                bizInventoryInfo1.setCustomer(company);
                List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo1);
                model.addAttribute("invInfoList", invInfoList);
            }
            return "modules/biz/request/bizRequestKcForm";
        }
        // if(source!=null && "gh".equals(source)){
        return "modules/biz/request/bizRequestHeaderGhForm";
        //  }

    }

    /**
     * excle导出
     * */
    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value ="listExport",method = RequestMethod.POST)
    public String listExport(String source, Integer bizStatu, String ship, HttpServletRequest request, HttpServletResponse response, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,RedirectAttributes redirectAttributes) {
        DefaultProp defaultProp = new DefaultProp();
        defaultProp.setPropKey("vend_center");
        Integer vendId = 0;
        List<DefaultProp> defaultPropList = defaultPropService.findList(defaultProp);
        if (defaultPropList != null && defaultPropList.size() > 0) {
            DefaultProp prop = defaultPropList.get(0);
            vendId = Integer.parseInt(prop.getPropValue());
        }
        model.addAttribute("ship", ship);
        model.addAttribute("source", source);
        if (bizOrderHeader == null) {
            bizOrderHeader = new BizOrderHeader();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String fileName =null;
        try {
            if ("kc".equals(source)) {
                fileName = "订单出库数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }else if ("sh".equals(source)) {
                fileName = "备货单收货数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }
            //1备货单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            if ("kc".equals(source)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCK_COMPLETE.getState().byteValue());
                if (bizStatu == 0) {
                    bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
                    bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());
                }
                if (bizStatu == 1) {
                    bizOrderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.PURCHASING.getState());
                    bizOrderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.SEND.getState());
                }
                if (ship.equals("xs")) {
                    Page<BizOrderHeader> page = new Page<>(request, response);
                    page = bizOrderHeaderService.findPageForSendGoods(page, bizOrderHeader);
                    for (BizOrderHeader orderHeader : page.getList()) {
                        List<String> headerList = new ArrayList();
                        headerList.add(String.valueOf(orderHeader.getOrderNum()));
                        headerList.add(String.valueOf("销售订单"));
                        if(orderHeader.getCustomer()!=null && orderHeader.getCustomer().getName()!=null){
                            headerList.add(String.valueOf(orderHeader.getCustomer().getName()));
                        }else{
                            headerList.add("");
                        }
                        if(orderHeader.getDeliveryDate()!=null){
                            headerList.add(String.valueOf(orderHeader.getDeliveryDate()));
                        }else{
                            headerList.add("");
                        }
                        Dict dictBiz = new Dict();
                        dictBiz.setDescription("业务状态");
                        dictBiz.setType("biz_order_status");
                        List<Dict> dictListBiz = dictService.findList(dictBiz);
                        for (Dict dbiz : dictListBiz) {
                            if (dbiz.getValue().equals(String.valueOf(orderHeader.getBizStatus()))) {
                                //业务状态
                                headerList.add(String.valueOf(dbiz.getLabel()));
                                break;
                            }
                        }
                        if(orderHeader.getCreateBy()!=null && orderHeader.getCreateBy().getName()!=null){
                            headerList.add(String.valueOf(orderHeader.getUpdateBy().getName()));
                        }else{
                            headerList.add("");
                        }
                        headerList.add(String.valueOf(orderHeader.getUpdateBy().getName()));
                        headerList.add(String.valueOf(sdf.format(orderHeader.getCreateDate())));
                        headerList.add(String.valueOf(sdf.format(orderHeader.getUpdateDate())));
                        data.add(headerList);
                        //商品详情
                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(orderHeader);
                        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
                        if(orderDetailList.size()!=0){
                            for (BizOrderDetail orderDetail : orderDetailList) {
                                List<String> deTaList = new ArrayList();
                                BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(orderDetail.getSkuInfo().getId()));
                                orderDetail.setSkuInfo(skuInfo);
                                deTaList.add(String.valueOf(orderHeader.getOrderNum()));
                                if(bizOrderHeader.getCustomer()!=null && bizOrderHeader.getCustomer().getName()!=null){
                                    //2采购中心
                                    deTaList.add(String.valueOf(bizOrderHeader.getCustomer().getName()));
                                }else{
                                    deTaList.add("");
                                }
                                if(orderHeader.getDeliveryDate()!=null){
                                    //3期望收货时间
                                    deTaList.add(String.valueOf(sdf.format(orderHeader.getDeliveryDate())));
                                }else{
                                    deTaList.add("");
                                }
                                if(skuInfo!=null && skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getName()!=null){
                                    //商品名称
                                    deTaList.add(String.valueOf(skuInfo.getProductInfo().getName()));
                                }else{
                                    deTaList.add("");
                                }
                                if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getCategoryInfoList()!=null){
                                    //分类
                                    List<BizCategoryInfo> categoryInfoList = skuInfo.getProductInfo().getCategoryInfoList();
                                    String classification="";
                                    if(categoryInfoList.size()!=0){
                                        for (BizCategoryInfo bizCategoryInfo : categoryInfoList) {
                                            if(bizCategoryInfo.getName()!=null){
                                                classification+=String.valueOf(bizCategoryInfo.getName());
                                            }
                                        }
                                        deTaList.add(String.valueOf(classification));
                                    }else{
                                        deTaList.add("");
                                    }
                                }else{
                                    deTaList.add("");
                                }
                                if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getProdCode()!=null){
                                    //商品代码
                                    deTaList.add(String.valueOf(skuInfo.getProductInfo().getProdCode()));
                                }else{
                                    deTaList.add("");
                                }
                                //品牌名称
                                if(skuInfo!=null && skuInfo.getProductInfo().getBrandName()!=null){
                                    deTaList.add(String.valueOf(skuInfo.getProductInfo().getBrandName()));
                                }else{
                                    deTaList.add("");
                                }
                                //供应商
                                if(skuInfo!=null && skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getOffice()!=null && skuInfo.getProductInfo().getOffice().getName()!=null){
                                    deTaList.add(String.valueOf(skuInfo.getProductInfo().getOffice().getName()));
                                }else{
                                    deTaList.add("");
                                }
                                if(skuInfo!=null && skuInfo.getName()!=null || skuInfo.getPartNo()!=null){
                                    deTaList.add(String.valueOf(skuInfo.getName()));
                                    deTaList.add(String.valueOf(skuInfo.getPartNo()));
                                }else{
                                    deTaList.add("");
                                    deTaList.add("");
                                }
                                deTaList.add(String.valueOf(orderDetail.getOrdQty()));
                                deTaList.add(String.valueOf(orderDetail.getSentQty()));
                                detailData.add(deTaList);
                            }
                        }else{
                            List<String> deTaList = new ArrayList();
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            deTaList.add("");
                            detailData.add(deTaList);
                        }
                    }
                }
            }else if ("sh".equals(source)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.COMPLETE.getState().byteValue());
                Page<BizRequestHeader> page = new Page<>(request, response);
                page = bizRequestHeaderService.findPageForSendGoods(page, bizRequestHeader);
                for (BizRequestHeader requestHeader : page.getList()) {
                    List<String> headerList = new ArrayList();
                    //1单号
                    headerList.add(String.valueOf(requestHeader.getReqNo()));
                    Dict typedict = new Dict();
                    typedict.setDescription("备货单类型");
                    typedict.setType("biz_req_type");
                    List<Dict> typeDictList = dictService.findList(typedict);
                    for (Dict di : typeDictList) {
                        if (di.getValue().equals(String.valueOf(requestHeader.getReqType()))) {
                            //2类型
                            headerList.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    if(requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                        //3采购中心
                        headerList.add(String.valueOf(requestHeader.getFromOffice().getName()));
                    }else{
                        headerList.add("");
                    }
                    headerList.add(String.valueOf(sdf.format(requestHeader.getRecvEta())));
                    headerList.add(String.valueOf(requestHeader.getRemark()));
                    Dict reqDict = new Dict();
                    reqDict.setDescription("备货单业务状态");
                    reqDict.setType("biz_req_status");
                    List<Dict> reqDictList = dictService.findList(reqDict);
                    for (Dict di : reqDictList) {
                        if (di.getValue().equals(String.valueOf(requestHeader.getBizStatus()))) {
                            //2类型
                            headerList.add(String.valueOf(di.getLabel()));
                            break;
                        }
                    }
                    if(requestHeader.getCreateBy()!=null && requestHeader.getCreateBy().getName()!=null){
                        headerList.add(String.valueOf(requestHeader.getCreateBy().getName()));
                    }else{
                        headerList.add("");
                    }
                    headerList.add(String.valueOf(sdf.format(requestHeader.getCreateDate())));
                    headerList.add(String.valueOf(sdf.format(requestHeader.getUpdateDate())));
                    data.add(headerList);

                    BizRequestDetail bizRequestDetail = new BizRequestDetail();
                    bizRequestDetail.setRequestHeader(requestHeader);
                    List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
                    if(requestDetailList.size()!=0){
                        for (BizRequestDetail requestDetail : requestDetailList) {
                            //商品
                            List<String> rowData = new ArrayList();
                            rowData.add(String.valueOf(requestHeader.getReqNo()));
                            if(requestHeader.getFromOffice()!=null && requestHeader.getFromOffice().getName()!=null){
                                //3采购中心
                                rowData.add(String.valueOf(requestHeader.getFromOffice().getName()));
                            }else{
                                rowData.add("");
                            }
                            rowData.add(String.valueOf(sdf.format(requestHeader.getRecvEta())));
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(bizSkuInfoService.get(requestDetail.getSkuInfo().getId()));
                            if(skuInfo!=null){
                                requestDetail.setSkuInfo(skuInfo);
                                if(skuInfo.getName()!=null){
                                    rowData.add(String.valueOf(requestDetail.getSkuInfo().getName()));
                                }else{
                                    rowData.add("");
                                }
                                if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getCategoryInfoList()!=null){
                                    //分类
                                    List<BizCategoryInfo> categoryInfoList = skuInfo.getProductInfo().getCategoryInfoList();
                                    String classification="";
                                    if(categoryInfoList.size()!=0){
                                        for (BizCategoryInfo bizCategoryInfo : categoryInfoList) {
                                            if(bizCategoryInfo.getName()!=null){
                                                classification+=String.valueOf(bizCategoryInfo.getName());
                                            }
                                        }
                                        rowData.add(String.valueOf(classification));
                                    }else{
                                        rowData.add("");
                                    }
                                }
                                if(skuInfo.getProductInfo()!=null && skuInfo.getProductInfo().getProdCode()!=null){
                                    rowData.add(String.valueOf(skuInfo.getProductInfo().getProdCode()));
                                }else{
                                    rowData.add("");
                                }
                                if(skuInfo.getProductInfo()!=null){
                                    //品牌名称
                                    if(skuInfo.getProductInfo().getBrandName()!=null){
                                        rowData.add(String.valueOf(skuInfo.getProductInfo().getBrandName()));
                                    }else{
                                        rowData.add("");
                                    }
                                    if(skuInfo.getProductInfo().getOffice()!=null && skuInfo.getProductInfo().getOffice().getName()!=null){
                                        rowData.add(String.valueOf(skuInfo.getProductInfo().getOffice().getName()));
                                    }else{
                                        rowData.add("");
                                    }
                                }else{
                                    rowData.add("");
                                    rowData.add("");
                                }
                                if(skuInfo.getName()!=null){
                                    //SKU
                                    rowData.add(String.valueOf(skuInfo.getName()));
                                }else{
                                    rowData.add("");
                                }
                                if(skuInfo.getPartNo()!=null){
                                    //SKU编号
                                    rowData.add(String.valueOf(skuInfo.getPartNo()));
                                }else{
                                    rowData.add("");
                                }
                            }
                            //申报数量\已供货数量
                            rowData.add(String.valueOf(requestDetail.getReqQty()));
                            rowData.add(String.valueOf(requestDetail.getSendQty()));
                            detailData.add(rowData);
                        }
                    }else{
                        List<String> rowData = new ArrayList();
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        rowData.add("");
                        detailData.add(rowData);
                    }
                }
            }
            if ("kc".equals(source)) {
                //订单出库
                String[] headers = {"销售单号", "类型", "采购中心", "期望收货时间", "备注","业务状态", "更新人", "发货时间", "更新时间"};
                String[] details = {"销售单号", "采购中心", "期望收货时间", "商品名称", "商品分类","商品代码", "品牌名称", "供应商", "SKU",
                        "SKU编号", "申报数量", "已供货数量"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "订单数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            }else if ("sh".equals(source)) {
                //备货单收货
                String[] headers = {"备货单号", "类型", "采购中心", "期望收货时间", "备注","业务状态", "更新人", "发货时间", "更新时间"};
                String[] details = {"备货单号", "采购中心", "期望收货时间", "商品名称", "商品分类","商品代码", "品牌名称", "供应商", "SKU",
                        "SKU编号", "申报数量", "已供货数量"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "订单数据", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        if ("kc".equals(source)) {
            //订单出库
            return "modules/biz/request/bizRequestKcXqForm";
        }
        //备货单收货
        return "modules/biz/request/bizRequestAllList";
    }

}
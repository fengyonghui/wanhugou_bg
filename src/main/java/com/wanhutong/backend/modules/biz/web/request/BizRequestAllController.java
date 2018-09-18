package com.wanhutong.backend.modules.biz.web.request;

import com.google.common.collect.Lists;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.supcan.treelist.cols.Col;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.Encodes;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.excel.ExportExcelUtils;
import com.wanhutong.backend.common.utils.excel.OrderHeaderExportExcelUtils;
import com.wanhutong.backend.common.web.BaseController;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.custom.BizCustomCenterConsultant;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizLogistics;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.custom.BizCustomCenterConsultantService;
import com.wanhutong.backend.modules.biz.service.inventory.BizCollectGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInventoryInfoService;
import com.wanhutong.backend.modules.biz.service.inventory.BizInvoiceService;
import com.wanhutong.backend.modules.biz.service.inventory.BizLogisticsService;
import com.wanhutong.backend.modules.biz.service.inventory.BizSendGoodsRecordService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderAddressService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.biz.service.variety.BizVarietyUserInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.*;
import com.wanhutong.backend.modules.sys.service.DefaultPropService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.DictService;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
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
    private BizRequestHeaderForVendorService bizRequestHeaderService;
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
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizInvoiceService bizInvoiceService;
    @Autowired
    private BizCollectGoodsRecordService bizCollectGoodsRecordService;
    @Autowired
    private BizVarietyUserInfoService bizVarietyUserInfoService;
    @Autowired
    private BizCustomCenterConsultantService bizCustomCenterConsultantService;
    @Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = {"list", ""})
    public String list(String source, Integer bizStatu, String ship, HttpServletRequest request, HttpServletResponse response, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        List<String> enNameList = Lists.newArrayList();
        for (Role role : roleList) {
            enNameList.add(role.getEnname());
        }
        if (!user.isAdmin() && enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
            model.addAttribute("vendor","vendor");
        }
        model.addAttribute("ship", ship);
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
        if("kc".equals(source) && ship!=null && "xs".equals(ship) && bizStatu!=null && bizStatu==1){
            //订单发货
            return "modules/biz/request/bizRequestAllCollectList";
        }
        if("kc".equals(source) && ship!=null && "bh".equals(ship) && bizStatu!=null && bizStatu==1){
            //备货单发货
            return "modules/biz/request/bizRequestAllCollectList";
        }
        return "modules/biz/request/bizRequestAllList";
    }

    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value = "form")
    public String form(String source, Integer id, Model model, Integer bizStatu, String ship) {
        User user = UserUtils.getUser();
        List<Role> roleList = user.getRoleList();
        List<String> enNameList = Lists.newArrayList();
        for (Role role : roleList) {
            enNameList.add(role.getEnname());
        }
        if (!user.isAdmin() && enNameList.contains(RoleEnNameEnum.SUPPLY_CHAIN.getState())) {
            model.addAttribute("vendor","vendor");
        }
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
            if (!user.isAdmin()) {
                Office company = officeService.get(user.getCompany().getId());
                bizInventoryInfo.setCustomer(company);
            }
            List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
            model.addAttribute("invInfoList", invInfoList);
            List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
            model.addAttribute("logisticsList", logisticsList);

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
                BizVarietyUserInfo bizVarietyUserInfo = new BizVarietyUserInfo();
                bizVarietyUserInfo.setVarietyInfo(skuInfo.getProductInfo().getBizVarietyInfo());
                List<BizVarietyUserInfo> varietyUserInfoList = bizVarietyUserInfoService.findList(bizVarietyUserInfo);
                if (CollectionUtils.isNotEmpty(varietyUserInfoList)) {
                    requestDetail.setVarietyUser(varietyUserInfoList.get(0).getUser());
                }
                reqDetailList.add(requestDetail);
            }
            if(requestDetailList.size()==0){
                requestHeader.setPoSource("poHeaderSource");
            }
            BizInvoice invoice = new BizInvoice();
            if ("bh".equals(ship)) {
                invoice.setShip(BizInvoice.Ship.RE.getShip());
                invoice.setReqNo(bizRequestHeader.getReqNo());
                invoice.setIsConfirm(BizInvoice.IsConfirm.YES.getIsConfirm());
            }
            List<BizInvoice> invoiceList = bizInvoiceService.findList(invoice);
            if (CollectionUtils.isNotEmpty(invoiceList)) {
                model.addAttribute("invoiceList",invoiceList);
            }
        }
        if (bizOrderHeader != null && bizOrderHeader.getId() != null) {
            //取出用户所属采购中心
            BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
            BizLogistics bizLogistics = new BizLogistics();
            if (!user.isAdmin()) {
                Office company = officeService.get(user.getCompany().getId());
                bizInventoryInfo.setCustomer(company);
            }
            List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
            model.addAttribute("invInfoList", invInfoList);
            List<BizLogistics> logisticsList = bizLogisticsService.findList(bizLogistics);
            model.addAttribute("logisticsList", logisticsList);

            BizOrderDetail bizOrderDetail = new BizOrderDetail();
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            List<BizOrderDetail> orderDetailList;
            orderHeader = bizOrderHeaderService.get(bizOrderHeader.getId());
            if (bizStatu == 0) {
                bizOrderDetail.setSuplyis(new Office(bizOrderHeader.getCenterId()));
                orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            } else {
                orderDetailList = bizOrderDetailService.findPoHeader(bizOrderDetail);
                BizInvoice invoice = new BizInvoice();
                if ("xs".equals(ship)) {
                    invoice.setShip(BizInvoice.Ship.SO.getShip());
                    invoice.setOrderNum(orderHeader.getOrderNum());
                    invoice.setIsConfirm(BizInvoice.IsConfirm.YES.getIsConfirm());
                }
                List<BizInvoice> invoiceList = bizInvoiceService.findList(invoice);
                if (CollectionUtils.isNotEmpty(invoiceList)) {
                    model.addAttribute("invoiceList",invoiceList);
                }
            }
            if (orderHeader.getOrderType().equals(BizOrderTypeEnum.PHOTO_ORDER.getState())) {
                CommonImg commonImg = new CommonImg();
                commonImg.setObjectId(orderHeader.getId());
                commonImg.setObjectName(ImgEnum.ORDER_SKU_PHOTO.getTableName());
                commonImg.setImgType(ImgEnum.ORDER_SKU_PHOTO.getCode());
                List<CommonImg> commonImgList = commonImgService.findList(commonImg);
                if (CollectionUtils.isNotEmpty(commonImgList)) {
                    model.addAttribute("commonImgList",commonImgList);
                }
                BizPoHeader bizPoHeader = new BizPoHeader();
                bizPoHeader.setNum(orderHeader.getOrderNum());
                List<BizPoHeader> poHeaderList = bizPoHeaderService.findList(bizPoHeader);
                if (CollectionUtils.isNotEmpty(poHeaderList)) {
                    model.addAttribute("poHeader", poHeaderList.get(0));
                }
            }
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
            bizInventoryInfo.setReqHeader(new BizRequestHeader(id));
            List<BizInventoryInfo> invInfoList = bizInventoryInfoService.findList(bizInventoryInfo);
            model.addAttribute("invInfoList", invInfoList);
            List<String> deliverNoList = findDeliverNoByReqId(new BizRequestHeader(id));
            model.addAttribute("deliverNoList",deliverNoList);
            Integer s = bizCollectGoodsRecordService.findContByCentId(bizRequestHeader.getFromOffice().getId());
            String collectNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.RIO,bizRequestHeader.getFromOffice().getId(),bizRequestHeader.getToOffice().getId(),s+1);
            BizCollectGoodsRecord bizCollectGoodsRecord = new BizCollectGoodsRecord();
            bizCollectGoodsRecord.setBizRequestHeader(bizRequestHeader);
            List<BizCollectGoodsRecord> bizCollectGoodsRecordList = bizCollectGoodsRecordService.findList(bizCollectGoodsRecord);
            if (CollectionUtils.isEmpty(bizCollectGoodsRecordList)) {
                model.addAttribute("collectNo",collectNo+"_1");
            }else {
                BizCollectGoodsRecord c = bizCollectGoodsRecordList.get(0);
                String[] split = c.getCollectNo().split("_");
                model.addAttribute("collectNo",collectNo + "_" + Integer.valueOf(split[1])+1);
            }
            model.addAttribute("collectNo",collectNo);
            return "modules/biz/request/bizRequestKcForm";
        }
        return "modules/biz/request/bizRequestHeaderGhForm";
    }

    /**
     * 根据备货单ID查询发货单
     * @param bizRequestHeader
     * @return
     */
    private List<String> findDeliverNoByReqId(BizRequestHeader bizRequestHeader) {
        return bizInvoiceService.findDeliverNoByReqId(bizRequestHeader.getId());
    }

    /**
     * 出库页面跳转
     * @param orderHeaderId
     * @return
     */
    @RequiresPermissions("biz:request:confirmOut:view")
    @RequestMapping(value = "confirmOut")
    public String confirmOut(Integer orderHeaderId,Model model) {
        if (orderHeaderId == null) {
            return "";
        }
        BizOrderHeader orderHeader = bizOrderHeaderService.get(orderHeaderId);
        BizCustomCenterConsultant bizCustomCenterConsultant = new BizCustomCenterConsultant();
        bizCustomCenterConsultant.setCustoms(orderHeader.getCustomer());
        List<BizCustomCenterConsultant> list = bizCustomCenterConsultantService.findList(bizCustomCenterConsultant);
        Office center = new Office();
        if (CollectionUtils.isNotEmpty(list)) {
            center = list.get(0).getCenters();
        }
        //出库单号
        Integer s = bizOrderHeaderService.findCountByCentId(center.getId());
        String sendNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.ODO,orderHeader.getCustomer().getId(),orderHeader.getCenterId(),s + 1);
        BizSendGoodsRecord bizSendGoodsRecord = new BizSendGoodsRecord();
        bizSendGoodsRecord.setBizOrderHeader(orderHeader);
        bizSendGoodsRecord.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
        List<BizSendGoodsRecord> sendGoodsRecordList = bizSendGoodsRecordService.findList(bizSendGoodsRecord);
        if (CollectionUtils.isEmpty(sendGoodsRecordList)) {
            model.addAttribute("sendNo", sendNo + "_1");
        } else {
            BizSendGoodsRecord sendGoodsRecord = sendGoodsRecordList.get(0);
            String[] split = sendGoodsRecord.getSendNo().split("_");
            model.addAttribute("sendNo",sendNo + "_" + Integer.valueOf(split[1]) + 1);
        }
        return "modules/biz/request/bizRequestConfirmOut";
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
                    List<BizOrderHeader> pageList = bizOrderHeaderService.pageFindListExprot(bizOrderHeader);
                    for (BizOrderHeader orderHeader : pageList) {
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
                List<BizRequestHeader> pageHeaderList = bizRequestHeaderService.findListAllExport(bizRequestHeader);
                for (BizRequestHeader requestHeader : pageHeaderList) {
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

    /**
     * 订单发货、备货单发货导出
     * */
    @RequiresPermissions("biz:request:selecting:supplier:view")
    @RequestMapping(value ="requestOrderHeaderExport",method = RequestMethod.POST)
    public String requestOrderHeaderExport(String source, Integer bizStatu, String ship, HttpServletRequest request, HttpServletResponse response, Model model, BizRequestHeader bizRequestHeader, BizOrderHeader bizOrderHeader,RedirectAttributes redirectAttributes) {
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
            if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "xs".equals(ship)) {
                fileName = "订单发货数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }else if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "bh".equals(ship)) {
                fileName = "备货单发货数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            }
            //1备货单
            List<List<String>> data = new ArrayList<List<String>>();
            //2商品
            List<List<String>> detailData = new ArrayList<List<String>>();
            if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "xs".equals(ship)) {
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
                    List<BizOrderHeader> pageList = bizOrderHeaderService.pageFindListExprot(bizOrderHeader);
                    for (BizOrderHeader orderHeader : pageList) {
                        orderHeader.setBizLocation(bizOrderAddressService.get(orderHeader.getBizLocation().getId()));
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
                        //收货地址
                        if(orderHeader.getBizLocation()!=null){
                            if(orderHeader.getBizLocation().getProvince()!=null && orderHeader.getBizLocation().getProvince().getName()!=null &&
                                    orderHeader.getBizLocation().getCity()!=null && orderHeader.getBizLocation().getCity().getName()!=null ||
                                    orderHeader.getBizLocation().getRegion()!=null && orderHeader.getBizLocation().getRegion().getName()!=null ){
                                headerList.add(String.valueOf(orderHeader.getBizLocation().getProvince().getName()+orderHeader.getBizLocation().getCity().getName()+
                                        orderHeader.getBizLocation().getRegion().getName()+orderHeader.getBizLocation().getAddress()));
                            }else{headerList.add("");}
                        }else{headerList.add("");}

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
                                if(orderHeader.getCustomer()!=null && orderHeader.getCustomer().getName()!=null){
                                    //2采购商
                                    deTaList.add(String.valueOf(orderHeader.getCustomer().getName()));
                                }else{
                                    deTaList.add("");
                                }
                                if(orderHeader.getBizLocation()!=null){
                                    if(orderHeader.getBizLocation().getProvince()!=null && orderHeader.getBizLocation().getProvince().getName()!=null &&
                                            orderHeader.getBizLocation().getCity()!=null && orderHeader.getBizLocation().getCity().getName()!=null ||
                                            orderHeader.getBizLocation().getRegion()!=null && orderHeader.getBizLocation().getRegion().getName()!=null ){
                                        deTaList.add(String.valueOf(orderHeader.getBizLocation().getProvince().getName()+""+orderHeader.getBizLocation().getCity().getName()+""+
                                                orderHeader.getBizLocation().getRegion().getName()+""+orderHeader.getBizLocation().getAddress()));
                                    }else{deTaList.add("");}
                                }else{deTaList.add("");}
                                //联系人电话
                                if(orderHeader.getBizLocation()!=null){
                                    if(orderHeader.getBizLocation().getReceiver()!=null || orderHeader.getBizLocation().getPhone()!=null){
                                        deTaList.add(String.valueOf(orderHeader.getBizLocation().getReceiver()+"/"+orderHeader.getBizLocation().getPhone()));
                                    }else{deTaList.add("");}
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
                            deTaList.add("");
                            detailData.add(deTaList);
                        }
                    }
                }
            }else if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "bh".equals(ship)) {
                bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.PURCHASING.getState().byteValue());
                bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.STOCK_COMPLETE.getState().byteValue());
                List<BizRequestHeader> HeaderPageList = bizRequestHeaderService.findListAllExport(bizRequestHeader);
                for (BizRequestHeader requestHeader : HeaderPageList) {
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
            if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "xs".equals(ship)) {
                //订单发货
                String[] headers = {"销售单号", "类型", "采购客户", "期望收货时间", "收货地址","业务状态", "更新人", "创建时间", "更新时间"};
                String[] details = {"销售单号", "经销店", "收货地址", "联系人/电话", "商品名称", "商品分类","商品代码", "品牌名称", "供应商", "SKU",
                        "SKU编号", "申报数量", "已供货数量"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "订单发货", headers, data, fileName);
                eeu.exportExcel(workbook, 1, "商品数据", details, detailData, fileName);
                response.reset();
                response.setContentType("application/octet-stream; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + Encodes.urlEncode(fileName));
                workbook.write(response.getOutputStream());
                workbook.dispose();
            }else if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "bh".equals(ship)) {
                //备货单发货
                String[] headers = {"备货单号", "类型", "采购中心", "期望收货时间", "备注","业务状态", "更新人", "发货时间", "更新时间"};
                String[] details = {"备货单号", "采购中心", "期望收货时间", "商品名称", "商品分类","商品代码", "品牌名称", "供应商", "SKU",
                        "SKU编号", "申报数量", "已供货数量"};
                ExportExcelUtils eeu = new ExportExcelUtils();
                SXSSFWorkbook workbook = new SXSSFWorkbook();
                eeu.exportExcel(workbook, 0, "备货单发货", headers, data, fileName);
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
        if ("kc".equals(source) && bizStatu!=null && bizStatu==1 && "xs".equals(ship)) {
            //订单发货
            return "modules/biz/request/bizRequestAllCollectList";
        }
        //备货单发货
        return "modules/biz/request/bizRequestAllCollectList";
    }

}